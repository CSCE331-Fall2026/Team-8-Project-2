import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;


public class ManagerMenuController {

  // ===================== INVENTORY SUMMARY =====================

  @FXML
  private Text numNeedsRestockText;
  @FXML
  private Text lowestStockText;

  private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/team8db"; // database

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

  /** Loads inventory status values for the manager dashboard. */
  private void getStatus() {
    String needsRestockQuery =
        "SELECT COUNT(*) FROM ingredients WHERE quantity_in_stock < min_qty_on_hand";
    String lowestStockQuery =
        "SELECT ingredient_name FROM ingredients " +
        "WHERE min_qty_on_hand > 0 " +
        "ORDER BY quantity_in_stock::numeric / min_qty_on_hand ASC LIMIT 1";

    try {
      Connection conn = connectDb();
      if (conn == null) {
        return;
      }
      try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(needsRestockQuery)) {
                if (rs.next()) {
                    numNeedsRestockText.setText(String.valueOf(rs.getInt(1)));
                }
            }
            try (ResultSet rs = stmt.executeQuery(lowestStockQuery)) {
                if (rs.next()) {
                    lowestStockText.setText(rs.getString("ingredient_name"));
                }
            }
        }
    } catch (SQLException e) {
      return;
    }

  }

  /** Initializes the manager dashboard. */
  @FXML
  private void initialize() {

    getStatus();

  }

  /** Returns to the main screen. */
  @FXML
  private void goBack(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/GUI.fxml"));
      Parent root = fxmlLoader.load();
      Node source = (Node) event.getSource();
      source.getScene().setRoot(root);
    } catch (Exception e) {
      System.out.println("Error returning to main screen:\n" + e.getMessage());
      e.printStackTrace();
    }
  }

  // ======================== INVENTORY ========================

  /** Opens the inventory view. */
  @FXML
  private void openViewInventory(ActionEvent event) {
    String fxmlPath = "fxml/inventory_levels.fxml";
    loadScreen(event, fxmlPath);
  }

  /** Opens the inventory update view. */
  @FXML
  private void openUpdateInventory(ActionEvent event) {
    String fxmlPath = "fxml/inventory_levels.fxml";
    loadScreen(event, fxmlPath);
  }

  // ======================== REPORTS ========================

  /** Opens the restock report. */
  @FXML
  private void openRestockReport(ActionEvent event) {
    String fxmlPath = "fxml/restock_report.fxml";
    loadScreen(event, fxmlPath);
  }

  /** Opens the sales report. */
  @FXML
  private void openSalesReport(ActionEvent event) {
    String fxmlPath = "fxml/sales_report.fxml";
    loadScreen(event, fxmlPath);
  }

  /** Opens the product usage chart. */
  @FXML
  private void openProductUsageChart(ActionEvent event) {
    String fxmlPath = "fxml/product_usage.fxml";
    loadScreen(event, fxmlPath);
  }

  /** Opens the hourly sales report. */
  @FXML
  private void openXReport(ActionEvent event) {
    String fxmlPath = "fxml/sales_hour.fxml";
    loadScreen(event, fxmlPath);
  }

  /** Opens the revenue and cost report. */
  @FXML
  private void openRevenueCostReport(ActionEvent event) {
    String fxmlPath = "";
    loadScreen(event, fxmlPath);
  }

  // swaps the current scene's root for the given fxml (same approach as goBack in
  // ManagerController)
  /** Loads the requested FXML view into the current window. */
  private void loadScreen(ActionEvent event, String fxmlPath) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent root = fxmlLoader.load();
      Node source = (Node) event.getSource();
      source.getScene().setRoot(root);
    } catch (Exception e) {
      System.out.println("Error loading " + fxmlPath + ":\n" + e.getMessage());
      e.printStackTrace();
    }
  }
}
