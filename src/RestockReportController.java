import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.Comparator;
import java.sql.*;

public class RestockReportController {

    // ===================== Restock Report =====================

    @FXML
    private TableView<RestockRow> restockReportTable;
    @FXML
    private TableColumn<RestockRow, String> idColumn;
    @FXML
    private TableColumn<RestockRow, String> itemNameColumn;
    @FXML
    private TableColumn<RestockRow, String> storageTypeColumn;
    @FXML
    private TableColumn<RestockRow, String> itemQtyColumn;
    @FXML
    private TableColumn<RestockRow, String> itemMinQtyColumn;
    @FXML
    private TableColumn<RestockRow, String> itemExpiryColumn;
    @FXML
    private TableColumn<RestockRow, String> shelfLifeColumn;
    @FXML
    private TableColumn<RestockRow, String> timeTilExpiryColumn;
    @FXML
    private ChoiceBox<StorageType> storageTypePicker;
    @FXML
    private Label reportStatusLabel;

    private record RestockRow (
            int itemId,
            String itemName,
            double itemQty,
            String itemExpiry,
            String storageType,
            double itemMinQty,
            int shelfLife,
            int timeTilExpiry
    ) { }

    private enum StorageType {
        ALL("All", null),
        PANTRY("Pantry", "pantry"),
        REFRIGERATOR("Refrigerator", "refrigerator"),
        FREEZER("Freezer", "freezer");

        StorageType(String storageLabel, String dbLabel) {
            this.storageLabel = storageLabel;
            this.dbLabel = dbLabel;
        }

        public final String storageLabel;
        public final String dbLabel;

        @Override
        public String toString() { return storageLabel; }

        public String getDbLabel() { return dbLabel; }
    }

    private void applyFilter() {
        StorageType selected = storageTypePicker.getValue();

        filteredItems.setPredicate(row ->
                selected == null || selected == StorageType.ALL || selected.getDbLabel().equalsIgnoreCase(row.storageType()));
    }

    private final ObservableList<RestockRow> restockItems = FXCollections.observableArrayList();
    private FilteredList<RestockRow> filteredItems;

    @FXML
    private void initialize() {
        filteredItems = new FilteredList<>(restockItems, row -> true);
        SortedList<RestockRow> sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(restockReportTable.comparatorProperty());
        restockReportTable.setItems(sortedItems);

        storageTypePicker.getItems().addAll(StorageType.values());
        storageTypePicker.setValue(StorageType.ALL);
        storageTypePicker.valueProperty().addListener(observedValue -> applyFilter());

        idColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().itemId()))
        );
        itemNameColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().itemName())
        );
        itemQtyColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().itemQty()))
        );
        itemExpiryColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().itemExpiry())
        );
        storageTypeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().storageType())
        );
        itemMinQtyColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().itemMinQty()))
        );
        shelfLifeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().shelfLife()))
        );
        timeTilExpiryColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().timeTilExpiry()))
        );

        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        itemQtyColumn.setComparator(Comparator.comparingDouble(Double::parseDouble));
        itemMinQtyColumn.setComparator(Comparator.comparingDouble(Double::parseDouble));
        shelfLifeColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        timeTilExpiryColumn.setComparator(Comparator.comparingInt(Integer::parseInt));

        restockReportTable.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(RestockRow row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) {
                    setStyle("");
                } else if (row.itemQty() <= (row.itemMinQty() * 1.25) || row.timeTilExpiry() <= 2) {
                    setStyle("-fx-border-color: red; -fx-border-width: 2;");
                } else if (row.itemQty() <= (row.itemMinQty() * 2) || row.timeTilExpiry() <= 7) {
                    setStyle("-fx-border-color: yellow; -fx-border-width: 2;");
                } else {
                    setStyle("");
                }
            }
        });

        refreshRestockReport();

        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        restockReportTable.getSortOrder().add(idColumn);
    }

    @FXML
    private void refreshRestockReport() {
        restockItems.clear();

        Connection conn = connectDb();
        if (conn == null) {
            reportStatusLabel.setText("Could not connect to the database.");
            return;
        }

        try {
            Statement stmt = conn.createStatement();
            String selectRestockReportStatement =
                    "SELECT i.ingredient_id, i.ingredient_name, i.quantity_in_stock, i.expiry_date, i.storage_type, " +
                    "i.min_qty_on_hand, i.shelf_life_days, (i.expiry_date::DATE - CURRENT_DATE) AS calc_rem_time FROM ingredients AS i";
            ResultSet result = stmt.executeQuery(selectRestockReportStatement);
            while (result.next()) {
                int ingredientId = result.getInt("ingredient_id");
                String ingredientName = result.getString("ingredient_name");
                double qtyInStock = result.getDouble("quantity_in_stock");
                String expiryDate = result.getString("expiry_date").substring(0, 10);
                String storageType = result.getString("storage_type");
                double minQtyOnHand = result.getDouble("min_qty_on_hand");
                int shelfLife = result.getInt("shelf_life_days");
                int timeTilExpiry = Math.max(result.getInt("calc_rem_time"), 0);

                restockItems.add(new RestockRow(ingredientId,
                        ingredientName,
                        qtyInStock,
                        expiryDate,
                        storageType,
                        minQtyOnHand,
                        shelfLife,
                        timeTilExpiry
                ));
            }
            System.out.println("Loaded " + restockItems.size() + " items");

            result.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error loading restock report:\n" + e.getMessage());
            reportStatusLabel.setText("Error loading restock report.");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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
    private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db"; // database
                                                                                                     // location

    private Connection connectDb() {

        try { 
            dbSetup my = new dbSetup();

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