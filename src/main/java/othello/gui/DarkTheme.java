package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

public class DarkTheme implements Theme {
    // Basic colors
    @Override public Color getBackgroundColor() { return Color.DARKSLATEGRAY; }
    @Override public Color getBoardColor() { return Color.DARKGREEN; }
    @Override public Color getTextColor() { return Color.WHITE; }

    // Move indication
    @Override public Color getAvailableMoveColor() { return Color.DARKGOLDENROD; }
    @Override public Color getAvailableMoveHoverColor() { return Color.DARKGREEN; }

    // Animation
    @Override public Duration getFlipAnimationDuration() { return Duration.millis(300); }
    @Override public Duration getButtonPressAnimationDuration() { return Duration.millis(150); }
    @Override public Color getButtonPressAnimationColor() { return Color.DARKBLUE; }

    // Scoreboard
    @Override public Color getPlayerOneScoreColor() { return Color.BLACK; }
    @Override public Color getPlayerTwoScoreColor() { return Color.WHITE; }
    @Override public Color getScoreBackgroundColor() { return Color.DARKSLATEGRAY.darker(); }
    @Override public Color getScoreTextColor() { return Color.WHITE; }

    @Override
    public Color getRightPanelColor() {
        return Color.web("#8C3A3A");
    }
}