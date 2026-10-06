Assumes javafx sdk is in parent directory
For Mac, replace ; with :

Compile:

> javac --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml GUIApp.java
> OR (check SDK version)
> javac --module-path ../javafx-sdk-27/lib/ --add-modules javafx.controls,javafx.fxml GUIApp.java

Run:

> java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml GUIApp
> OR
> java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-27/lib/ --add-modules javafx.controls,javafx.fxml GUIApp
