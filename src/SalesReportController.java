import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SalesReportController {

    @FXML
    private ChoiceBox<MenuItemOption> itemPicker;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView<SalesRow> salesReportTable;
    @FXML
    private TableColumn<SalesRow, String> itemNameColumn;
    @FXML
    private TableColumn<SalesRow, String> quantitySoldColumn;
    @FXML
    private TableColumn<SalesRow, String> premiumRevenueColumn;
    @FXML
    private Label reportStatusLabel;

    private final ObservableList<SalesRow> salesRows = FXCollections.observableArrayList();

    private static final String ITEMS_QUERY =
        "SELECT menu_item_id, menu_item_name FROM menu_items ORDER BY menu_item_name";

    private static final String SALES_QUERY =
        "SELECT mi.menu_item_name, " +
        "SUM(od.order_qty * cd.servings) AS quantity_sold, " +
        "COALESCE(SUM(od.order_qty * cd.servings * COALESCE(mi.premium_charge, 0)), 0) " +
        "AS premium_revenue " +
        "FROM orders o " +
        "JOIN order_details od ON od.order_id = o.order_id " +
        "JOIN combo_details cd ON cd.order_detail_id = od.order_detail_id " +
        "JOIN menu_items mi ON mi.menu_item_id = cd.menu_item_id " +
        "WHERE o.order_status = 'paid' " +
        "AND o.created_at::date BETWEEN ? AND ? " +
        "AND (? = 0 OR mi.menu_item_id = ?) " +
        "GROUP BY mi.menu_item_id, mi.menu_item_name " +
        "ORDER BY mi.menu_item_name";

    private record MenuItemOption(int id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    private record SalesRow(String itemName, double quantitySold, double premiumRevenue) {
    }

    @FXML
    private void initialize() {
        salesReportTable.setItems(salesRows);
        itemNameColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().itemName())
        );
        quantitySoldColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().quantitySold()))
        );
        premiumRevenueColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(String.format("$%.2f", cell.getValue().premiumRevenue()))
        );

        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());

        try (Connection connection = connectDb();
            PreparedStatement statement = connection.prepareStatement(ITEMS_QUERY);
            ResultSet results = statement.executeQuery()) {
            itemPicker.getItems().add(new MenuItemOption(0, "All items"));
            while (results.next()) {
                itemPicker.getItems().add(new MenuItemOption(
                        results.getInt("menu_item_id"),
                        results.getString("menu_item_name")));
            }
            itemPicker.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            reportStatusLabel.setText("Could not load menu items: " + e.getMessage());
            System.err.println("Error loading sales report menu items:");
            return;
        }

        itemPicker.valueProperty().addListener((observable, oldValue, newValue) -> refreshReport());
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> refreshReport());
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> refreshReport());
        refreshReport();
    }

    @FXML
    private void refreshReport() {
        salesRows.clear();

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            reportStatusLabel.setText("Choose both a start date and an end date.");
            return;
        }
        if (startDate.isAfter(endDate)) {
            reportStatusLabel.setText("Start date must be on or before end date.");
            return;
        }

        MenuItemOption selectedItem = itemPicker.getValue();
        int itemId = selectedItem == null ? 0 : selectedItem.id();

        try (Connection connection = connectDb();
            PreparedStatement statement = connection.prepareStatement(SALES_QUERY)) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            statement.setInt(3, itemId);
            statement.setInt(4, itemId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    salesRows.add(new SalesRow(
                            results.getString("menu_item_name"),
                            results.getDouble("quantity_sold"),
                            results.getDouble("premium_revenue")));
                }
            }
            reportStatusLabel.setText(salesRows.isEmpty()
                    ? "No paid sales found for the selected filters."
                    : salesRows.size() + " menu item(s). Revenue uses current premium charges only; " +
                            "combo base prices are excluded.");
        } catch (SQLException e) {
            reportStatusLabel.setText("Could not load sales report: " + e.getMessage());
            System.err.println("Error loading sales report:");
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
            reportStatusLabel.setText("Could not return to the manager screen: " + e.getMessage());
            System.err.println("Error returning to manager screen:");
            e.printStackTrace();
        }
    }

    private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db";

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
