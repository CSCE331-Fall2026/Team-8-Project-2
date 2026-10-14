import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ChoiceBox;
import java.sql.*;

public class RestockReportController {

    // ===================== Restock Report =====================

    @FXML
    private TableView<HourRow> restockReportTable;
    @FXML
    private TableColumn<HourRow, String> itemColumn;
    @FXML
    private TableColumn<HourRow, String> qtyColumn;
    @FXML
    private TableColumn<HourRow, String> expiryColumn;
    @FXML
    private ChoiceBox<String> storageTypePicker;

    private record HourRow(int hour, double sales, int orders) {
    }

    private final ObservableList<HourRow> restockItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

    }

    @FXML
    private void refreshSalesPerHour() {


        Connection conn = connectDb();
        if (conn == null) {
            System.out.println("Could not connect to the database.");
            return;
        }

        try {
            // THIS EXAMPLE IS FROM OTHER FILE
            // Statement stmt = conn.createStatement();
            // String selectSalesPerHourStatement = "SELECT EXTRACT(HOUR FROM created_at)::int AS sale_hour, COUNT(*) as order_count, SUM(order_total) AS total_sales FROM orders WHERE order_status = 'paid' AND created_at::date = CURRENT_DATE GROUP BY sale_hour ORDER BY sale_hour";
            // ResultSet result = stmt.executeQuery(selectSalesPerHourStatement);
            // while (result.next()) {
            //     int orders = result.getInt("order_count");
            //     double sales = result.getDouble("total_sales");
            //     hourItems.add(new HourRow(result.getInt("sale_hour"), sales, orders));
            // }
            // System.out.println("Loaded " + hourItems.size() + " hours of sales");

            result.close();
            stmt.close();
        } catch (Exception e) {
            // System.out.println("Error loading sales per hour:\n" + e.getMessage());
            // reportStatusLabel.setText("Error loading sales per hour.");
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