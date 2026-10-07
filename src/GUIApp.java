import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIApp extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/GUI.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 500);
    stage.setTitle("Team 8 Project");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
