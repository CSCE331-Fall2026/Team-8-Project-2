import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIController {

  @FXML
  private void openCashier(ActionEvent event) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/Cashier.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root, 800, 500));
    stage.setTitle("Cashier");
  }

  @FXML
  private void openManager(ActionEvent event) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/inventory_levels.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root, 800, 500));
    stage.setTitle("Manager");
  }
}
