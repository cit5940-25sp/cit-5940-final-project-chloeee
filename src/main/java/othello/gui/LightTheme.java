package othello.gui;

import javafx.scene.paint.Color;

public class LightTheme implements Theme {
    @Override public Color getBackgroundColor() { return Color.LIGHTGRAY; }
    @Override public Color getBoardColor() { return Color.LIMEGREEN; }
    @Override public Color getHighlightColor() { return Color.LIGHTYELLOW; }
    @Override public Color getTextColor() { return Color.BLACK; }

    @Override
    public Color getAvailableMoveColor() {
        return Color.LIGHTYELLOW;
    }

    @Override
    public Color getAvailableMoveHoverColor() {
        return Color.LIMEGREEN;
    }

}