module org.example.expensetrackerui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.expensetrackerui to javafx.fxml;
    exports org.example.expensetrackerui;
}