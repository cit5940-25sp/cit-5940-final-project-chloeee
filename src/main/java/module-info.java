module othello {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires junit;
    requires org.junit.jupiter.api;
    requires java.desktop;


    opens othello to javafx.fxml;
    exports othello;
    exports othello.gui;
    opens othello.gui to javafx.fxml;
    exports othello.gamelogic;
    opens othello.gamelogic to javafx.fxml;
}