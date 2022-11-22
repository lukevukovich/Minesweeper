module com.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.minesweeper to javafx.fxml;
    exports com.minesweeper;
}