import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.sql.*;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import java.util.List;
import java.util.ArrayList;



public class InventoryController {
  @FXML
  private TextField newMenuItemNameField;
  @FXML
  private TextField newIngredientName;
  @FXML
  private TextField newIngredientQty;
  @FXML
  private ChoiceBox<String> newIngredientStorageType;
  @FXML
  private TextField newIngredientUnits;
  @FXML
  private TextField newIngredientMinQty;
  @FXML
  private TextField newIngredientShelfLife;
  @FXML
  private TextField newMenuItemPremiumCharge;
  @FXML
  private TextField newMenuItemServingsUsed;
  @FXML
  private ChoiceBox<String> newMenuItemType;
  @FXML
  private Label seasonalStatusLabel;
  @FXML
  private ListView<IngredientOption> ingredientListView;
  private final ObservableList<IngredientOption> ingredientOptions = FXCollections.observableArrayList();
  private record IngredientOption(int id, String name, BooleanProperty selected) {
    /** Returns the ingredient name used by list cells. */
    @Override
    public String toString() {
      return name;
    } // CheckBoxListCell uses this for the label
  }

  // new menu item fx:ids are newMenuItemXXXX
  // new ingredient fx:ids are newIngredientXXXX

  @FXML
  private TableView<InventoryRow> inventoryTable;
  @FXML
  private TableColumn<InventoryRow, String> inventoryNameColumn;
  @FXML
  private TableColumn<InventoryRow, String> inventoryPercentColumn;
  @FXML
  private TableColumn<InventoryRow, String> inventoryAmountColumn;
  @FXML
  private TableColumn<InventoryRow, String> inventoryUnitsColumn;
  @FXML
  private TableColumn<InventoryRow, String> inventoryStorageColumn;
  @FXML
  private TextField newQuantityField;
  @FXML
  private Button updateQuantityButton;
  @FXML
  private Label inventoryStatusLabel;

  @FXML 
  private ChoiceBox<String> storageTypePicker;

  @FXML
  private TableView<ComboRow> comboTable;
  @FXML
  private TableColumn<ComboRow, String> comboNameColumn;
  @FXML
  private TableColumn<ComboRow, String> comboPriceColumn;
  @FXML
  private TextField newComboPriceField;
  @FXML
  private Button updateComboPriceButton;
  @FXML
  private TextField newPremiumPriceField;
  @FXML
  private Button updatePremiumButton;
  @FXML
  private TableView<MenuItemRow> menuItemTable;
  @FXML
  private TableColumn<MenuItemRow, String> menuItemNameColumn;
  @FXML
  private TableColumn<MenuItemRow, String> menuItemPremiumColumn;
  @FXML
  private TableColumn<MenuItemRow, String> menuItemActiveColumn;
  @FXML
  private Button discontinueMenuItemButton;
  @FXML
  private Button reactivateMenuItemButton;
  @FXML
  private Label menuStatusLabel;

  private record InventoryRow(int id, String name, double quantity, String units, String storage) {
  }

  private record ComboRow(int id, String name, double price) {
  }

  private record MenuItemRow(int id, String name, double premiumCharge, boolean active) {
  }

  private final ObservableList<InventoryRow> inventoryItems = FXCollections.observableArrayList();
  private final ObservableList<ComboRow> comboItems = FXCollections.observableArrayList();
  private final ObservableList<MenuItemRow> menuItems = FXCollections.observableArrayList();

  /** Initializes the inventory, combo, and menu item tables. */
  @FXML
  private void initialize() {
    // Inventory table
    inventoryTable.setItems(inventoryItems);
    inventoryNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
    // % Remaining intentionally left blank until it is defined (no value factory)
    inventoryAmountColumn
        .setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().quantity())));
    inventoryUnitsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().units()));
    inventoryStorageColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().storage()));

    updateQuantityButton.setDisable(true);
    inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      updateQuantityButton.setDisable(newVal == null);
      if (newVal != null) {
        newQuantityField.setText(String.valueOf(newVal.quantity()));
      }
    });

    ingredientListView.setItems(ingredientOptions);
    ingredientListView.setCellFactory(CheckBoxListCell.forListView(IngredientOption::selected));
    loadIngredientOptions();

    comboTable.setItems(comboItems);
    comboNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
    comboPriceColumn
        .setCellValueFactory(cell -> new SimpleStringProperty("$" + String.format("%.2f", cell.getValue().price())));

    updateComboPriceButton.setDisable(true);
    comboTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      updateComboPriceButton.setDisable(newVal == null);
      if (newVal != null) {
        newComboPriceField.setText(String.format("%.2f", newVal.price()));
      }
    });

    menuItemTable.setItems(menuItems);
    menuItemNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
    menuItemPremiumColumn.setCellValueFactory(
        cell -> new SimpleStringProperty("$" + String.format("%.2f", cell.getValue().premiumCharge())));
    menuItemActiveColumn
        .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().active() ? "Active" : "Discontinued"));

    discontinueMenuItemButton.setDisable(true);
    reactivateMenuItemButton.setDisable(true);
    menuItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      discontinueMenuItemButton.setDisable(newVal == null || !newVal.active());
      reactivateMenuItemButton.setDisable(newVal == null || newVal.active());
    });
    storageTypePicker.getItems().addAll("freezer", "refrigerator", "pantry", "None");
    newIngredientStorageType.getItems().addAll("freezer", "refrigerator", "pantry");
    newIngredientStorageType.getSelectionModel().selectFirst();
    newMenuItemType.getItems().addAll("entree", "side", "appetizer", "drink");
    newMenuItemType.getSelectionModel().selectFirst();

    refreshInventory();
    refreshMenu();
  }

  // ===================== INVENTORY =====================

  /** Loads ingredients that can be assigned to a menu item. */
  private void loadIngredientOptions() {
    ingredientOptions.clear();

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      ResultSet result = stmt.executeQuery(
          "SELECT ingredient_id, ingredient_name FROM ingredients ORDER BY ingredient_name");
      while (result.next()) {
        ingredientOptions.add(new IngredientOption(
            result.getInt("ingredient_id"),
            result.getString("ingredient_name"),
            new SimpleBooleanProperty(false)));
      }
      result.close();
      stmt.close();
    } catch (Exception e) {
      System.out.println("Error loading ingredient options:\n" + e.getMessage());
      menuStatusLabel.setText("Error loading ingredients.");
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /** Loads inventory rows using the selected storage filter. */
  @FXML
  private void refreshInventory() {
    inventoryItems.clear();

    Connection conn = connectDb();
    if (conn == null) {
      inventoryStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      String filter = "";
      if (storageTypePicker.getValue() != null && storageTypePicker.getValue() != "None")  {
        filter = "WHERE storage_type = '" + storageTypePicker.getValue() +  "' ";
      }
      String selectInventoryStatement = "SELECT ingredient_id, ingredient_name, quantity_in_stock, measurement_units, storage_type"
          +
          " FROM ingredients " + filter
          + "ORDER BY ingredient_name";
      ResultSet result = stmt.executeQuery(selectInventoryStatement);

      while (result.next()) {
        inventoryItems.add(new InventoryRow(
            result.getInt("ingredient_id"),
            result.getString("ingredient_name"),
            result.getDouble("quantity_in_stock"),
            result.getString("measurement_units"),
            result.getString("storage_type")));
      }
      System.out.println("Loaded " + inventoryItems.size() + " ingredients");

      result.close();
      stmt.close();
    } catch (Exception e) {
      System.out.println("Error loading inventory:\n" + e.getMessage());
      inventoryStatusLabel.setText("Error loading inventory.");
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /** Updates the selected ingredient quantity and recalculates its expiry date. */
  @FXML
  private void updateInventoryQuantity() {
    InventoryRow selected = inventoryTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    // Validate before building SQL so only a clean number is ever concatenated in
    double newQuantity;
    try {
      newQuantity = Double.parseDouble(newQuantityField.getText().trim());
    } catch (NumberFormatException e) {
      inventoryStatusLabel.setText("Enter a valid number for the new amount.");
      return;
    }
    if (!Double.isFinite(newQuantity) || newQuantity < 0) {
      inventoryStatusLabel.setText("Amount must be zero or greater.");
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      inventoryStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      String updateQuantityStatement = "UPDATE ingredients SET quantity_in_stock = " + newQuantity + " " +
          "WHERE ingredient_id = " + selected.id();
      stmt.executeUpdate(updateQuantityStatement);
      String updateExpDateStatement = "UPDATE ingredients SET expiry_date = CURRENT_DATE::DATE + shelf_life_days " +
              "WHERE ingredient_id = " + selected.id();
      stmt.executeUpdate(updateExpDateStatement);
      stmt.close();

      inventoryStatusLabel.setText("Updated " + selected.name() + " to " +
          String.format("%.2f", newQuantity) + " " + (selected.units() == null ? "" : selected.units()));
      newQuantityField.clear();
    } catch (Exception e) {
      System.out.println("Error updating inventory:\n" + e.getMessage());
      inventoryStatusLabel.setText("Error updating inventory.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshInventory();
  }

  // ======================== MENU ========================
  /** Adds a new ingredient using the entered inventory details. */
  @FXML
  private void addIngredient() {
    String name = newIngredientName.getText().trim();
    String units = newIngredientUnits.getText().trim();
    String storage = newIngredientStorageType.getValue();
    if (name.isEmpty() || units.isEmpty() || storage == null) {
      seasonalStatusLabel.setText("Enter a name, units, and storage type.");
      return;
    }

    double quantity;
    double minQuantity;
    int shelfLife;
    try {
      quantity = Double.parseDouble(newIngredientQty.getText().trim());
      minQuantity = Double.parseDouble(newIngredientMinQty.getText().trim());
      shelfLife = Integer.parseInt(newIngredientShelfLife.getText().trim());
    } catch (NumberFormatException e) {
      seasonalStatusLabel.setText("Quantity, min qty, and shelf life must be numbers.");
      return;
    }
    if (!Double.isFinite(quantity) || quantity < 0 || !Double.isFinite(minQuantity) || minQuantity < 0
        || shelfLife <= 0) {
      seasonalStatusLabel.setText("Quantities must be >= 0 and shelf life must be > 0.");
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      seasonalStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try (PreparedStatement insert = conn.prepareStatement(
        "INSERT INTO ingredients (ingredient_name, quantity_in_stock, storage_type, expiry_date, " +
            "min_qty_on_hand, measurement_units, shelf_life_days) " +
            "VALUES (?, ?, ?::storage_type, CURRENT_DATE + ?, ?, ?, ?)")) {
      insert.setString(1, name);
      insert.setDouble(2, quantity);
      insert.setString(3, storage);
      insert.setInt(4, shelfLife);
      insert.setDouble(5, minQuantity);
      insert.setString(6, units);
      insert.setInt(7, shelfLife);
      insert.executeUpdate();

      seasonalStatusLabel.setText("Added ingredient " + name + ".");
      newIngredientName.clear();
      newIngredientQty.clear();
      newIngredientUnits.clear();
      newIngredientMinQty.clear();
      newIngredientShelfLife.clear();
    } catch (Exception e) {
      System.out.println("Error adding ingredient:\n" + e.getMessage());
      seasonalStatusLabel.setText("Error adding ingredient.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    loadIngredientOptions();
    refreshInventory();
  }

  /** Adds a menu item and links it to the selected ingredients. */
  @FXML
  private void addMenuItem() {
    String name = newMenuItemNameField.getText().trim();
    if (name.isEmpty()) {
      seasonalStatusLabel.setText("Enter a name for the menu item.");
      return;
    }

    List<IngredientOption> chosen = new ArrayList<>();
    for (IngredientOption option : ingredientOptions) {
      if (option.selected().get()) {
        chosen.add(option);
      }
    }
    if (chosen.isEmpty()) {
      seasonalStatusLabel.setText("Select at least one ingredient.");
      return;
    }

    String type = newMenuItemType.getValue();
    if (type == null) {
      seasonalStatusLabel.setText("Choose a menu item type.");
      return;
    }

    double premiumCharge = 0.0;
    String premiumText = newMenuItemPremiumCharge.getText().trim();
    double servings = 1.0;
    String servingsText = newMenuItemServingsUsed.getText().trim();
    try {
      if (!premiumText.isEmpty()) {
        premiumCharge = Double.parseDouble(premiumText);
      }
      if (!servingsText.isEmpty()) {
        servings = Double.parseDouble(servingsText);
      }
    } catch (NumberFormatException e) {
      seasonalStatusLabel.setText("Premium charge and servings must be numbers.");
      return;
    }
    if (!Double.isFinite(premiumCharge) || premiumCharge < 0 || !Double.isFinite(servings) || servings <= 0) {
      seasonalStatusLabel.setText("Premium charge must be >= 0 and servings must be > 0.");
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      seasonalStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      conn.setAutoCommit(false);
      int menuItemId;
      try (PreparedStatement insertItem = conn.prepareStatement(
          "INSERT INTO menu_items (menu_item_name, premium_charge, active, menu_item_type) " +
              "VALUES (?, ?, TRUE, ?::menu_item_type)",
          Statement.RETURN_GENERATED_KEYS)) {
        insertItem.setString(1, name);
        insertItem.setDouble(2, premiumCharge);
        insertItem.setString(3, type);
        insertItem.executeUpdate();
        try (ResultSet keys = insertItem.getGeneratedKeys()) {
          keys.next();
          menuItemId = keys.getInt("menu_item_id");
        }
      }

      try (PreparedStatement insertDetail = conn.prepareStatement(
          "INSERT INTO menu_item_details (menu_item_id, ingredient_id, ingredient_quantity) VALUES (?, ?, ?)")) {
        for (IngredientOption option : chosen) {
          insertDetail.setInt(1, menuItemId);
          insertDetail.setInt(2, option.id());
          insertDetail.setDouble(3, servings);
          insertDetail.addBatch();
        }
        insertDetail.executeBatch();
      }
      conn.commit();

      seasonalStatusLabel.setText("Added " + name + ".");
      newMenuItemNameField.clear();
      newMenuItemPremiumCharge.clear();
      newMenuItemServingsUsed.clear();
      newMenuItemType.getSelectionModel().selectFirst();
      for (IngredientOption option : ingredientOptions) {
        option.selected().set(false);
      }
    } catch (Exception e) {
      System.out.println("Error adding menu item:\n" + e.getMessage());
      try {
        conn.rollback();
      } catch (SQLException rollbackError) {
        rollbackError.printStackTrace();
      }
      seasonalStatusLabel.setText("Error adding menu item.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshMenu();
  }

  /** Loads combo prices and active or discontinued menu items. */
  @FXML
  private void refreshMenu() {
    comboItems.clear();
    menuItems.clear();

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();

      String selectCombosStatement = "SELECT combo_id, combo_name, combo_price FROM combos ORDER BY combo_id";
      ResultSet comboResult = stmt.executeQuery(selectCombosStatement);
      while (comboResult.next()) {
        comboItems.add(new ComboRow(
            comboResult.getInt("combo_id"),
            comboResult.getString("combo_name"),
            comboResult.getDouble("combo_price")));
      }
      comboResult.close();

      String selectMenuItemsStatement = "SELECT menu_item_id, menu_item_name, premium_charge, active " +
          "FROM menu_items " +
          "ORDER BY menu_item_name";
      ResultSet itemResult = stmt.executeQuery(selectMenuItemsStatement);
      while (itemResult.next()) {
        menuItems.add(new MenuItemRow(
            itemResult.getInt("menu_item_id"),
            itemResult.getString("menu_item_name"),
            itemResult.getDouble("premium_charge"),
            itemResult.getBoolean("active")));
      }
      itemResult.close();

      stmt.close();
    } catch (Exception e) {
      System.out.println("Error loading menu:\n" + e.getMessage());
      menuStatusLabel.setText("Error loading menu.");
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /** Updates the selected combo's base price. */
  @FXML
  private void updateComboPrice() {
    ComboRow selected = comboTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    // Validate before building SQL so only a clean number is ever concatenated in
    double newPrice;
    try {
      newPrice = Double.parseDouble(newComboPriceField.getText().trim());
    } catch (NumberFormatException e) {
      menuStatusLabel.setText("Enter a valid number for the new price.");
      return;
    }
    if (!Double.isFinite(newPrice) || newPrice < 0) {
      menuStatusLabel.setText("Price must be zero or greater.");
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      String updateComboPriceStatement = "UPDATE combos SET combo_price = " + newPrice + " " +
          "WHERE combo_id = " + selected.id();
      stmt.executeUpdate(updateComboPriceStatement);
      stmt.close();

      menuStatusLabel.setText("Updated " + selected.name() + " to $" + String.format("%.2f", newPrice));
      newComboPriceField.clear();
    } catch (Exception e) {
      System.out.println("Error updating combo price:\n" + e.getMessage());
      menuStatusLabel.setText("Error updating combo price.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshMenu();
  }

  /** Updates the premium charge for the selected menu item. */
  @FXML
  private void updatePremiumPrice() {
    MenuItemRow selected = menuItemTable.getSelectionModel().getSelectedItem();
    if (selected == null || !selected.active()) {
      return;
    }

    // Validate before building SQL so only a clean number is ever concatenated in
    double newPrice;
    try {
      newPrice = Double.parseDouble(newPremiumPriceField.getText().trim());
    } catch (NumberFormatException e) {
      menuStatusLabel.setText("Enter a valid number for the new price.");
      return;
    }
    if (!Double.isFinite(newPrice) || newPrice < 0) {
      menuStatusLabel.setText("Price must be zero or greater.");
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      String updatePremiumPriceStatement = "UPDATE menu_items SET premium_charge = " + newPrice + " " +
          "WHERE menu_item_id = " + selected.id();
      stmt.executeUpdate(updatePremiumPriceStatement);
      stmt.close();

      menuStatusLabel.setText("Updated " + selected.name() + " to $" + String.format("%.2f", newPrice));
      newPremiumPriceField.clear();
    } catch (Exception e) {
      System.out.println("Error updating combo price:\n" + e.getMessage());
      menuStatusLabel.setText("Error updating combo price.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshMenu();
  }

  /** Marks the selected menu item as discontinued. */
  @FXML
  private void discontinueMenuItem() {
    MenuItemRow selected = menuItemTable.getSelectionModel().getSelectedItem();
    if (selected == null || !selected.active()) {
      return;
    }

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
        "Discontinue \"" + selected.name() + "\"? It will no longer be available to order.",
        ButtonType.YES, ButtonType.NO);
    confirm.setHeaderText(null);
    Optional<ButtonType> choice = confirm.showAndWait();
    if (choice.isEmpty() || choice.get() != ButtonType.YES) {
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      // Soft delete: keeps the row so past combo_details still point at a valid menu
      // item
      Statement stmt = conn.createStatement();
      String discontinueStatement = "UPDATE menu_items SET active = FALSE " +
          "WHERE menu_item_id = " + selected.id();
      stmt.executeUpdate(discontinueStatement);
      stmt.close();

      menuStatusLabel.setText("Discontinued " + selected.name() + ".");
    } catch (Exception e) {
      System.out.println("Error discontinuing menu item:\n" + e.getMessage());
      menuStatusLabel.setText("Error discontinuing menu item.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshMenu();
  }

  /** Reactivates the selected menu item. */
  @FXML
  private void reactivateMenuItem() {
    MenuItemRow selected = menuItemTable.getSelectionModel().getSelectedItem();
    if (selected == null || selected.active()) {
      return;
    }

    Connection conn = connectDb();
    if (conn == null) {
      menuStatusLabel.setText("Could not connect to the database.");
      return;
    }

    try {
      Statement stmt = conn.createStatement();
      String reactivateStatement = "UPDATE menu_items SET active = TRUE " +
          "WHERE menu_item_id = " + selected.id();
      stmt.executeUpdate(reactivateStatement);
      stmt.close();

      menuStatusLabel.setText("Reactivated " + selected.name() + ".");
    } catch (Exception e) {
      System.out.println("Error reactivating menu item:\n" + e.getMessage());
      menuStatusLabel.setText("Error reactivating menu item.");
      return;
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    refreshMenu();
  }

  /** Returns to the manager screen. */
  @FXML
  private void goBack(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/manager.fxml"));
      Parent root = fxmlLoader.load();
      Node source = (Node) event.getSource();
      source.getScene().setRoot(root);
    } catch (Exception e) {
      System.out.println("Error returning to main screen:\n" + e.getMessage());
      e.printStackTrace();
    }
  }

  // database connection
  private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db"; // database location

  /** Opens a connection to the application database. */
  private Connection connectDb() {

    try {
      // Get database credentials
      dbSetup my = new dbSetup();

      // Build the connection
      Class.forName("org.postgresql.Driver");
      Connection conn = DriverManager.getConnection(DB_URL, my.user, my.pswd);
      return conn;

    } catch (Exception e) {
      System.out.println("Error connecting to database:\n" + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
