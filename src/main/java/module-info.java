module com.hivemind {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.sql;
	requires java.desktop;

	exports com.hivemind;
    opens com.hivemind to javafx.fxml;
    exports com.hivemind.controllers;
    opens com.hivemind.controllers to javafx.fxml;
}