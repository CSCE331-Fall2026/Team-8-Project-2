import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

public class ManagerMenuController {

  // ===================== INVENTORY SUMMARY =====================

  @FXML private Text avgStockText;
  @FXML private Text highestStockText;
  @FXML private Text lowestStockText;

  @FXML
  private void initialize() {
    // TODO: load the stock summary (avg / highest / lowest) into the three Text fields
  }

  // ======================== INVENTORY ========================

  @FXML
  private void openViewInventory(ActionEvent event) {
    String fxmlPath = "fxml/inventory_levels.fxml";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  @FXML
  private void openUpdateInventory(ActionEvent event) {
    String fxmlPath = "fxml/inventory_levels.fxml";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  // ======================== REPORTS ========================

  @FXML
  private void openRestockReport(ActionEvent event) {
    String fxmlPath = "";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  @FXML
  private void openSalesReport(ActionEvent event) {
    String fxmlPath = "";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  @FXML
  private void openProductUsageChart(ActionEvent event) {
    String fxmlPath = "";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  @FXML
  private void openXReport(ActionEvent event) {
    String fxmlPath = "fxml/sales_hour.fxml";   // TODO: fill in (sales_hour.fxml)
    loadScreen(event, fxmlPath);
  }

  @FXML
  private void openRevenueCostReport(ActionEvent event) {
    String fxmlPath = "";   // TODO: fill in
    loadScreen(event, fxmlPath);
  }

  // swaps the current scene's root for the given fxml (same approach as goBack in ManagerController)
  private void loadScreen(ActionEvent event, String fxmlPath) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent root = fxmlLoader.load();
      Node source = (Node) event.getSource();
      source.getScene().setRoot(root);
    }
    catch (Exception e) {
      System.out.println("Error loading " + fxmlPath + ":\n" + e.getMessage());
      e.printStackTrace();
    }
  }
}
