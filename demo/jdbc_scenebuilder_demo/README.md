Use the same dbSetup.java code from jdbc_demo
Assumes javafx sdk is in parent directory
For Mac, replace ; with :

Compile:
> javac --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml -cp ".;postgresql-42.2.8.jar" DatabaseApp.java DatabaseController.java

Run:
> java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-25/lib/ --add-modules javafx.controls,javafx.fxml -cp ".;postgresql-42.2.8.jar" DatabaseApp.java
