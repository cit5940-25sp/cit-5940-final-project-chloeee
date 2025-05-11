package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * The {@code Theme} interface defines the color scheme and animation styles used
 * throughout the Othello GUI. Implementations of this interface provide specific
 * values for board appearance, animations, and scoreboard styling.
 * <p>
 * By abstracting theme-related settings into an interface, the GUI can easily support
 * multiple visual styles (e.g., dark mode, light mode, retro style) without modifying
 * the core game logic.
 */
public interface Theme {
    // Basic colors
    /**
     * Gets the background color of the game scene.
     *
     * @return the background color
     */
    Color getBackgroundColor();
    /**
     * Gets the main color of the Othello game board.
     *
     * @return the board color
     */
    Color getBoardColor();
    /**
     * Gets the default color for text displayed in the GUI.
     *
     * @return the text color
     */
    Color getTextColor();

    // Move indication colors
    /**
     * Gets the color used to indicate available move positions.
     *
     * @return the color for available move indicators
     */
    Color getAvailableMoveColor();
    /**
     * Gets the color used when the mouse hovers over an available move.
     *
     * @return the hover color for available move indicators
     */
    Color getAvailableMoveHoverColor();

    // Animation properties
    /**
     * Gets the duration of the disk flip animation.
     *
     * @return the duration of the flip animation
     */
    Duration getFlipAnimationDuration();
    /**
     * Gets the duration of the button press animation effect.
     *
     * @return the duration of the button press animation
     */
    Duration getButtonPressAnimationDuration();
    /**
     * Gets the color used for visual feedback during button press animations.
     *
     * @return the color used in button press animation
     */
    Color getButtonPressAnimationColor();

    // Scoreboard colors
    /**
     * Gets the color used to display player one's score.
     *
     * @return the color for player one's score
     */
    Color getPlayerOneScoreColor();
    /**
     * Gets the color used to display player two's score.
     *
     * @return the color for player two's score
     */
    Color getPlayerTwoScoreColor();
    /**
     * Gets the background color of the scoreboard area.
     *
     * @return the background color for the scoreboard
     */
    Color getScoreBackgroundColor();

    /**
     * Gets the text color used within the scoreboard.
     *
     * @return the scoreboard text color
     */
    Color getScoreTextColor();

    /**
     * Gets the background color of the right panel in the GUI.
     *
     * @return the color of the right panel
     */
    Color getRightPanelColor(); //
    // New visual elements
    /**
     * Indicates whether the theme supports animated transitions.
     *
     * @return true if animated transitions are supported, false otherwise
     */
    default boolean supportsAnimatedTransitions() {
        return true;
    }

    /**
     * Indicates whether the theme supports a visual scoreboard.
     *
     * @return true if visual scoreboard is supported, false otherwise
     */
    default boolean supportsVisualScoreboard() {
        return true;
    }
}