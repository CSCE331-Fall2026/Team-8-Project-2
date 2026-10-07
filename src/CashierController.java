import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class CashierController {

  @FXML private ListView<String> cartList;
  @FXML private Separator buildSeparator;
  @FXML private VBox buildPane;
  @FXML private Label buildTitleLabel;
  @FXML private Label buildProgressLabel;
  @FXML private Label buildSummaryLabel;
  @FXML private VBox itemRows;
  @FXML private Button addToCartButton;

  private final ObservableList<String> cartItems = FXCollections.observableArrayList();
  private BuildState build;

  @FXML
  private void initialize() {
    cartList.setItems(cartItems);
    hideBuild();
  }

  @FXML
  private void startType(ActionEvent event) {
    Button source = (Button) event.getSource();
    beginBuild(ComboType.valueOf((String) source.getUserData()));
  }

  @FXML
  private void cancelBuild() {
    hideBuild();
  }

  @FXML
  private void clearCart() {
    cartItems.clear();
  }

  @FXML
  private void doneOrder() {
    cartItems.clear();
    hideBuild();
  }

  @FXML
  private void addToCart() {
    if (build == null || !build.isComplete()) {
      return;
    }
    cartItems.add(build.cartLine());
    hideBuild();
  }

  private void beginBuild(ComboType type) {
    build = new BuildState(type);
    buildTitleLabel.setText(type.displayName());
    rebuildItemButtons();
    refreshBuildState();
    setBuildVisible(true);
  }

  private void hideBuild() {
    build = null;
    itemRows.getChildren().clear();
    setBuildVisible(false);
  }

  private void setBuildVisible(boolean visible) {
    buildSeparator.setVisible(visible);
    buildSeparator.setManaged(visible);
    buildPane.setVisible(visible);
    buildPane.setManaged(visible);
  }

  private void rebuildItemButtons() {
    itemRows.getChildren().clear();
    if (build == null) {
      return;
    }
    for (MenuCatalog.Category category : build.visibleCategories()) {
      Label heading = new Label(MenuCatalog.label(category) + ":");
      heading.setStyle("-fx-font-weight: bold;");

      FlowPane buttons = new FlowPane(10, 10);
      for (MenuCatalog.MenuItem item : MenuCatalog.items(category)) {
        Button button = new Button(item.name());
        button.setOnAction(e -> {
          build.toggle(item);
          refreshBuildState();
        });
        buttons.getChildren().add(button);
      }

      VBox row = new VBox(5, heading, buttons);
      itemRows.getChildren().add(row);
    }
  }

  private void refreshBuildState() {
    buildProgressLabel.setText(build.type().progressText());
    buildSummaryLabel.setText("Selected: " + build.summary());
    addToCartButton.setDisable(!build.isComplete());
  }
}
