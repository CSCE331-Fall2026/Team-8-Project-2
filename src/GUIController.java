import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIController {

  /** Opens the cashier screen in the current window. */
  @FXML
  private void openCashier(ActionEvent event) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/Cashier.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root, 800, 500));
    stage.setTitle("Cashier");
  }

  /** Opens the manager screen in the current window. */
  @FXML
  private void openManager(ActionEvent event) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/manager.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root, 800, 500));
    stage.setTitle("Manager");
  }
}
