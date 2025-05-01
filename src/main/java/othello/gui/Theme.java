package othello.gui;

import javafx.scene.paint.Color;

public interface Theme {
    Color getBackgroundColor();
    Color getBoardColor();
    Color getHighlightColor();
    Color getTextColor();
    Color getAvailableMoveColor();
    Color getAvailableMoveHoverColor();
}