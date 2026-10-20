import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ProductUsageController {

    // ===================== Product Usage =====================

    @FXML
    private BarChart<String, Number> usageChart;
    @FXML
    private MenuButton itemPicker;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private static final String USAGE_QUERY =
            "SELECT i.ingredient_name, SUM(od.order_qty * cd.servings * mid.ingredient_quantity) AS used " +
            "FROM orders o " +
            "JOIN order_details od ON od.order_id = o.order_id " +
            "JOIN combo_details cd ON cd.order_detail_id = od.order_detail_id " +
            "JOIN menu_item_details mid ON mid.menu_item_id = cd.menu_item_id " +
            "JOIN ingredients i ON i.ingredient_id = mid.ingredient_id " +
            "WHERE o.order_status = 'paid' AND o.created_at::date BETWEEN ? AND ? " +
            "GROUP BY i.ingredient_name ORDER BY i.ingredient_name";

    /** Initializes date filters, ingredient choices, and the usage chart. */
    @FXML
    private void initialize() {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        try (Connection conn = connectDb();
             ResultSet result = conn.createStatement().executeQuery(
                     "SELECT ingredient_name FROM ingredients ORDER BY ingredient_name")) {
            while (result.next()) {
                CheckBox checkBox = new CheckBox(result.getString("ingredient_name"));
                checkBox.selectedProperty().addListener(observedValue -> refreshUsageChart());
                CustomMenuItem item = new CustomMenuItem(checkBox);
                item.setHideOnClick(false);
                itemPicker.getItems().add(item);
            }
        } catch (Exception e) {
            System.out.println("Error loading inventory items:\n" + e.getMessage());
        }

        startDatePicker.valueProperty().addListener(observedValue -> refreshUsageChart());
        endDatePicker.valueProperty().addListener(observedValue -> refreshUsageChart());

        refreshUsageChart();
    }

    // an empty selection shows every item
    /** Returns the ingredient names selected in the chart filter. */
    private List<String> selectedItems() {
        return itemPicker.getItems().stream()
                .map(item -> (CheckBox) ((CustomMenuItem) item).getContent())
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();
    }

    /** Loads usage for the selected date range and updates the chart. */
    private void refreshUsageChart() {
        usageChart.getData().clear();
        List<String> selected = selectedItems();
        itemPicker.setText(selected.isEmpty() ? "All" : selected.size() + " selected");
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try (Connection conn = connectDb();
             PreparedStatement stmt = conn.prepareStatement(USAGE_QUERY)) {
            stmt.setDate(1, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(endDatePicker.getValue()));

            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                String name = result.getString("ingredient_name");
                if (selected.isEmpty() || selected.contains(name)) {
                    series.getData().add(new XYChart.Data<>(name, result.getDouble("used")));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading product usage:\n" + e.getMessage());
        }
        usageChart.getData().add(series);
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
    private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db";

    /** Opens a connection to the application database. */
    private Connection connectDb() throws Exception {
        dbSetup my = new dbSetup();
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(DB_URL, my.user, my.pswd);
    }
}
