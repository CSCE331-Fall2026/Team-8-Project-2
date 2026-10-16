import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.*;
import javafx.scene.control.TextArea;
import java.util.List;

public class CashierController {

  @FXML private ListView<CartLine> cartList;
  @FXML private Button removeCartItemButton;
  @FXML private Button editCartItemButton;
  @FXML private Label cartTotalLabel;
  @FXML private Separator buildSeparator;
  @FXML private VBox buildPane;
  @FXML private Label buildTitleLabel;
  @FXML private Label buildProgressLabel;
  @FXML private Label buildSummaryLabel;
  @FXML private VBox itemRows;
  @FXML private Button addToCartButton;
  @FXML private Spinner<Integer> orderQtySpinner;

  private record CartLine(ComboType type, List<MenuCatalog.MenuItem> items, String comboName, float price) {}

  private final ObservableList<CartLine> cartItems = FXCollections.observableArrayList();
  private BuildState build;
  private int editIndex = -1;

  @FXML
  private void initialize() {
    cartList.setItems(cartItems);
    cartList.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(CartLine item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText("$" + String.format("%.2f", item.price()) + " - " + item.comboName());
        }
      }
    });
    removeCartItemButton.setDisable(true);
    editCartItemButton.setDisable(true);
    cartList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      removeCartItemButton.setDisable(newVal == null);
      editCartItemButton.setDisable(newVal == null);
    });
    refreshCart();
    hideBuild();
  }

  @FXML
  private void startType(ActionEvent event) {
    Button source = (Button) event.getSource();
    beginBuild(ComboType.valueOf((String) source.getUserData()));
  }

  @FXML
  private void cancelBuild() {
    hideBuild();
  }

  @FXML
  private void removeSelectedCartItem() {
    int index = cartList.getSelectionModel().getSelectedIndex();
    if (index >= 0) {
      cartItems.remove(index);
      refreshCart();
    }
  }

  @FXML
  private void editSelectedCartItem() {
    int index = cartList.getSelectionModel().getSelectedIndex();
    if (index < 0) {
      return;
    }
    CartLine line = cartItems.get(index);
    beginBuild(line.type());
    line.items().forEach(build::add);
    rebuildItemButtons();
    refreshBuildState();
    editIndex = index;
  }

  @FXML
  private void clearCart() {
    cartItems.clear();
    refreshCart();
  }

  @FXML
  private void doneOrder() {
    if (cartItems.isEmpty()) {
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      return;
    }

    try {
      // SQL statement object that will be used to execute db inserts
      Statement stmt = conn.createStatement();

      // Create a row for order in database and return the id number back to function
      // for use in subsequent queries
      String createOrderStatement = 
        "INSERT INTO orders DEFAULT VALUES RETURNING order_id";
      ResultSet result = stmt.executeQuery(createOrderStatement);
      result.next();
      int orderId = result.getInt("order_id");
      result.close();

      // Create a row for each combo in the order in order details
      for (CartLine line : cartItems) {
        String createOrderDetailStatement =
          "INSERT INTO order_details (order_id, combo_id, order_qty) " +
          "VALUES " +
          "(" + orderId + ", " + line.type().comboId() + ", 1) " + // currently hardcoded as 1 - update once cartItems holds quantity
          "RETURNING order_detail_id"; 
        ResultSet orderDetailResult = stmt.executeQuery(createOrderDetailStatement);
        orderDetailResult.next();
        int orderDetailId = orderDetailResult.getInt("order_detail_id");
        orderDetailResult.close();

        for (MenuCatalog.MenuItem item : line.items()) {
          String createComboDetailStatement = 
            "INSERT INTO combo_details (order_detail_id, menu_item_id, servings) " +
            "VALUES " +
            "(" + orderDetailId + ", " + item.id() + ", 1)";
          stmt.executeUpdate(createComboDetailStatement);
        }
      }

      // With order created and combos tracked, write order total
      // First subquery calculates base combo cost * quantity
      // Second calculates premium charge for the combo
      // Results are added together as order_total
      String writeOrderTotalStatement = 
        "UPDATE orders SET order_total = " +
        "(SELECT SUM(c.combo_price * od.order_qty) " +
        "FROM order_details AS od " +
        "JOIN combos AS c ON c.combo_id = od.combo_id " + 
        "WHERE od.order_id = " + orderId + ") " +
        "+ (SELECT SUM(mi.premium_charge * od.order_qty) " +
        "FROM order_details AS od " +
        "JOIN combo_details AS cd ON cd.order_detail_id = od.order_detail_id " +
        "JOIN menu_items AS mi ON mi.menu_item_id = cd.menu_item_id " +
        "WHERE od.order_id = " + orderId + ") " +
        "WHERE order_id = " + orderId;
      stmt.executeUpdate(writeOrderTotalStatement);

      // Update ingredient stock
      String updateIngredientsStatement = 
        "UPDATE ingredients AS i SET quantity_in_stock = i.quantity_in_stock - u.used " +
        "FROM (SELECT mid.ingredient_id, SUM(mid.ingredient_quantity * cd.servings * od.order_qty) AS used " +
        "FROM order_details AS od " + 
        "JOIN combo_details AS cd ON cd.order_detail_id = od.order_detail_id " +
        "JOIN menu_item_details AS mid ON mid.menu_item_id = cd.menu_item_id " +
        "WHERE od.order_id = " + orderId + " " +
        "GROUP BY mid.ingredient_id) AS u " +
        "WHERE i.ingredient_id = u.ingredient_id";
      stmt.executeUpdate(updateIngredientsStatement);

      stmt.close();
      cartItems.clear();
      refreshCart();
      hideBuild();
    }
    catch (Exception e) {
      System.out.println("Error creating database statement:\n" + e.getMessage());
      return;
    }
    finally {
      try {
        conn.close();
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      
    }
  }

  @FXML
  private void addToCart() {
    if (build == null || !build.isComplete()) {
      return;
    }

    CartLine line = new CartLine(build.type(), build.items(), build.cartLine(), 0f);
    int copies = orderQtySpinner.getValue();
    if (editIndex >= 0) {
      cartItems.set(editIndex, line);
      copies--;
    }
    for (int i = 0; i < copies; i++) {
      cartItems.add(line);
    }
    refreshCart();
    hideBuild();
  }

  private void refreshCart() {
    Connection conn = connectDb();
    float total = 0f;
    try {
      for (int i = 0; i < cartItems.size(); i++) {
        CartLine line = cartItems.get(i);
        float price = line.type().getPrice(conn);
        for (MenuCatalog.MenuItem item : line.items()) {
          price += item.getPremium(conn);
        }
        cartItems.set(i, new CartLine(line.type(), line.items(), line.comboName(), price));
        total += price;
      }
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (Exception e) {
          System.out.println("Error closing database connection:\n" + e.getMessage());
        }
      }
    }
    cartTotalLabel.setText("Total: $" + String.format("%.2f", total));
  }

  private void beginBuild(ComboType type) {
    Connection conn = connectDb();
    if (conn != null) {
      MenuCatalog.refresh(conn);
      try {
        conn.close();
      } catch (SQLException e) {
        System.out.println("Error closing database connection:\n" + e.getMessage());
      }
    }
    build = new BuildState(type);
    editIndex = -1;
    buildTitleLabel.setText(type.displayName());
    orderQtySpinner.getValueFactory().setValue(1);
    rebuildItemButtons();
    refreshBuildState();
    setBuildVisible(true);
  }

  private void hideBuild() {
    build = null;
    editIndex = -1;
    itemRows.getChildren().clear();
    setBuildVisible(false);
  }

  private void setBuildVisible(boolean visible) {
    buildSeparator.setVisible(visible);
    buildSeparator.setManaged(visible);
    buildPane.setVisible(visible);
    buildPane.setManaged(visible);
  }

  private void rebuildItemButtons() {
    itemRows.getChildren().clear();
    if (build == null) {
      return;
    }
    for (MenuCatalog.Category category : build.visibleCategories()) {
      Label heading = new Label(MenuCatalog.label(category) + ":");
      heading.setStyle("-fx-font-weight: bold;");

      itemRows.getChildren().add(heading);
      for (MenuCatalog.MenuItem item : MenuCatalog.items(category)) {
        Label name = new Label(item.name());
        name.setPrefWidth(200);
        Label count = new Label(String.valueOf(build.countOf(item)));
        Button plus = new Button("+");
        Button minus = new Button("-");
        plus.setOnAction(e -> {
          build.add(item);
          count.setText(String.valueOf(build.countOf(item)));
          refreshBuildState();
        });
        minus.setOnAction(e -> {
          build.remove(item);
          count.setText(String.valueOf(build.countOf(item)));
          refreshBuildState();
        });
        itemRows.getChildren().add(new HBox(10, name, count, plus, minus));
      }
    }
  }

  private void refreshBuildState() {
    buildProgressLabel.setText(build.type().progressText());
    buildSummaryLabel.setText("Selected: " + build.summary());
    addToCartButton.setDisable(!build.isComplete());
  }

  @FXML
  private void goBack(ActionEvent event) {
    try {
      // Reload the manager/cashier selection screen into the current window
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/GUI.fxml"));
      Parent root = fxmlLoader.load();
      Node source = (Node) event.getSource();
      source.getScene().setRoot(root);
    }
    catch (Exception e) {
      System.out.println("Error returning to main screen:\n" + e.getMessage());
      e.printStackTrace();
    }
  }

  // database connection   
  private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db"; //database location
  private Connection connectDb() {

    try {
      // Get database credentials
      dbSetup my = new dbSetup();
 
      // Build the connection
      Class.forName("org.postgresql.Driver");
      Connection conn = DriverManager.getConnection(DB_URL, my.user, my.pswd);
      return conn;

    } 
    catch (Exception e) {
      System.out.println("Error connecting to database:\n" + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
