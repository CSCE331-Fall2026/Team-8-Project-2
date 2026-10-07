package controllers.managerControllers;
import org.w3c.dom.Text;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoginController {

    // Fields: names must match fx:id values in login.fxml
    @FXML private Text AvgStockText;
    @FXML private Button

    // Called automatically after the FXML is loaded and fields are injected.
    // getScene() is still null here.
    @FXML
    private void initialize() {
    }

    // Handler: wire to onAction="#onLoginClicked" in the FXML
    @FXML
    private void onLoginClicked() {
        Navigator.show("dashboard.fxml");
        
    }

    // Optional: call from Navigator before this view is replaced
    public void dispose() {
    }
}
