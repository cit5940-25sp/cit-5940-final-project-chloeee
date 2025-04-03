module othello {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens othello to javafx.fxml;
    exports othello;
    exports othello.gui;
    opens othello.gui to javafx.fxml;
    exports othello.gamelogic;
    opens othello.gamelogic to javafx.fxml;
}