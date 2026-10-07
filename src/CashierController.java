import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.sql.*;
import javafx.scene.control.TextArea;
import java.util.List;

public class CashierController {

  @FXML private ListView<CartLine> cartList;
  @FXML private Separator buildSeparator;
  @FXML private VBox buildPane;
  @FXML private Label buildTitleLabel;
  @FXML private Label buildProgressLabel;
  @FXML private Label buildSummaryLabel;
  @FXML private VBox itemRows;
  @FXML private Button addToCartButton;



  private record CartLine(ComboType type, List<MenuCatalog.MenuItem> items, String comboName) {
    @Override
    public String toString() {
      return comboName;
    }
  }

  private final ObservableList<CartLine> cartItems = FXCollections.observableArrayList();
  private BuildState build;

  @FXML
  private void initialize() {
    cartList.setItems(cartItems);
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
  private void clearCart() {
    cartItems.clear();
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

      // Create a row for each combo in the order
      for (CartLine line : cartItems) {
        String createComboDetailStatement =
          "INSERT INTO order_details (order_id, combo_id) " +
          "VALUES " +
          "(" + orderId + ", " + line.comboId + ")";
        stmt.executeUpdate(createComboDetailStatement);
      }

      // With order created and combos tracked, write order total
      // Calculates premium charge per combo then sums the combos to get order total
      String writeOrderTotalStatement = 
        "UPDATE orders SET order_total = t.total " +
        "FROM (" +
        "SELECT od.order_id, SUM(c.combo_price + pc.premium) AS total " +
        "FROM order_details AS od " +
        "JOIN combos AS c ON c.combo_id = od.combo_id " + 
        "JOIN (" + 
        "SELECT cd.combo_id, SUM(mi.premium_charge) AS premium " +
        "FROM combo_details AS cd " +
        "JOIN menu_items AS mi ON mi.menu_item_id = cd.menu_item_id " +
        "GROUP BY cd.combo_id " + 
        ") AS pc ON pc.combo_id = c.combo_id " +
        "WHERE od.order_id = " + orderId + " " +
        "GROUP BY od.order_id " + 
        ") AS t " +
        "WHERE orders.order_id = t.order_id";
      stmt.executeUpdate(writeOrderTotalStatement);

      // Update ingredient stock
      String updateIngredientsStatement = 
        "UPDATE ingredients AS i SET quantity_in_stock = i.quantity_in_stock - u.used " +
        "FROM (SELECT mid.ingredient_id, SUM(mid.ingredient_quantity) AS used " +
        "FROM order_details AS od " + 
        "JOIN combo_details AS cd ON cd.combo_id = od.combo_id " +
        "JOIN menu_item_details AS mid ON mid.menu_item_id = cd.menu_item_id " +
        "WHERE od.order_id = " + orderId + " " +
        "GROUP BY mid.ingredient_id) AS u " +
        "WHERE i.ingredient_id = u.ingredient_id";
      stmt.executeUpdate(updateIngredientsStatement);

      stmt.close();
      cartItems.clear();
      hideBuild();
    }
    catch (Exception e) {
      System.out.println("Error creating database statement:\n" + e.getMessage());
      return;
    }
    finally {
      conn.close();
    }
  }

  @FXML
  private void addToCart() {
    if (build == null || !build.isComplete()) {
      return;
    }
    cartItems.add(new CartLine(build.type(), build.items(), build.cartLine()));
    hideBuild();
  }

  private void beginBuild(ComboType type) {
    build = new BuildState(type);
    buildTitleLabel.setText(type.displayName());
    rebuildItemButtons();
    refreshBuildState();
    setBuildVisible(true);
  }

  private void hideBuild() {
    build = null;
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

      FlowPane buttons = new FlowPane(10, 10);
      for (MenuCatalog.MenuItem item : MenuCatalog.items(category)) {
        Button button = new Button(item.name());
        button.setOnAction(e -> {
          build.toggle(item);
          refreshBuildState();
        });
        buttons.getChildren().add(button);
      }

      VBox row = new VBox(5, heading, buttons);
      itemRows.getChildren().add(row);
    }
  }

  private void refreshBuildState() {
    buildProgressLabel.setText(build.type().progressText());
    buildSummaryLabel.setText("Selected: " + build.summary());
    addToCartButton.setDisable(!build.isComplete());
  }


  
  // database connection   
  private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db"; //database location
  private Connection connectDb() {

    try {
      // Get database creditials
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
