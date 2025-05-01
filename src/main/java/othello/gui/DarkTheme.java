package othello.gui;

import javafx.scene.paint.Color;

public class DarkTheme implements Theme {
    @Override public Color getBackgroundColor() { return Color.DARKSLATEGRAY; }
    @Override public Color getBoardColor() { return Color.DARKGREEN; }
    @Override public Color getHighlightColor() { return Color.DARKGOLDENROD; }
    @Override public Color getTextColor() { return Color.WHITE; }

    @Override
    public Color getAvailableMoveColor() {
        return Color.DARKGOLDENROD;
    }

    @Override
    public Color getAvailableMoveHoverColor() {
        return Color.DARKGREEN;
    }
}