Assumes javafx sdk is in parent directory.
For Mac/Linux, replace `;` with `:` in classpath examples elsewhere.
FXML layouts live in `fxml/`.

Compile (from this directory):

> javac --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml *.java
> OR (check SDK version)
> javac --module-path ../javafx-sdk-27/lib/ --add-modules javafx.controls,javafx.fxml *.java

Run:

> java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml GUIApp
> OR
> java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-27/lib/ --add-modules javafx.controls,javafx.fxml GUIApp
