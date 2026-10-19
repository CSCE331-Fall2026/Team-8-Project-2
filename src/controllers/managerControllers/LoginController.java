package controllers.managerControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LoginController {

    // Fields: names must match fx:id values in login.fxml
    @FXML private Button loginButton;

    // Called automatically after the FXML is loaded and fields are injected.
    // getScene() is still null here.
    @FXML
    private void initialize() {
    }

    // Handler: wire to onAction="#onLoginClicked" in the FXML
    @FXML
    private void onLoginClicked() {
    }

    // Optional: call from Navigator before this view is replaced
    public void dispose() {
    }
}
