package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

public interface Theme {
    // Basic colors
    Color getBackgroundColor();
    Color getBoardColor();
    Color getTextColor();

    // Move indication colors
    Color getAvailableMoveColor();
    Color getAvailableMoveHoverColor();

    // Animation properties
    Duration getFlipAnimationDuration();
    Duration getButtonPressAnimationDuration();
    Color getButtonPressAnimationColor();

    // Scoreboard colors
    Color getPlayerOneScoreColor();
    Color getPlayerTwoScoreColor();
    Color getScoreBackgroundColor();
    Color getScoreTextColor();

    Color getRightPanelColor(); //
    // New visual elements
    default boolean supportsAnimatedTransitions() {
        return true;
    }

    default boolean supportsVisualScoreboard() {
        return true;
    }
}