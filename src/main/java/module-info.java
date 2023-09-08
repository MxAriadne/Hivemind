module com.hivemind {
    requires javafx.controls;
    requires javafx.fxml;
            
    exports com.hivemind;
    opens com.hivemind to javafx.fxml;
}