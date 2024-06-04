module org.example.sudoku {
    requires javafx.controls;
    requires javafx.fxml;


    exports client.main;
    opens client.main to javafx.fxml;
    exports client.controller;
    opens client.controller to javafx.fxml;
}