package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * The {@code DarkTheme} class defines a dark color scheme for the Othello game's graphical user interface.
 *
 * <p>This class implements the {@link Theme} interface, providing specific colors and animation durations
 * tailored for a dark mode appearance, offering better visibility and comfort in low-light environments.</p>
 */
public class DarkTheme implements Theme {
    // Basic colors
    /** {@inheritDoc} */
    @Override public Color getBackgroundColor() { return Color.DARKSLATEGRAY; }
    /** {@inheritDoc} */
    @Override public Color getBoardColor() { return Color.DARKGREEN; }
    /** {@inheritDoc} */
    @Override public Color getTextColor() { return Color.WHITE; }

    // Move indication
    /** {@inheritDoc} */
    @Override public Color getAvailableMoveColor() { return Color.DARKGOLDENROD; }
    /** {@inheritDoc} */
    @Override public Color getAvailableMoveHoverColor() { return Color.DARKGREEN; }

    // Animation
    /** {@inheritDoc} */
    @Override public Duration getFlipAnimationDuration() { return Duration.millis(300); }
    /** {@inheritDoc} */
    @Override public Duration getButtonPressAnimationDuration() { return Duration.millis(150); }
    /** {@inheritDoc} */
    @Override public Color getButtonPressAnimationColor() { return Color.DARKBLUE; }

    // Scoreboard
    /** {@inheritDoc} */
    @Override public Color getPlayerOneScoreColor() { return Color.BLACK; }
    /** {@inheritDoc} */
    @Override public Color getPlayerTwoScoreColor() { return Color.WHITE; }
    /** {@inheritDoc} */
    @Override public Color getScoreBackgroundColor() { return Color.DARKSLATEGRAY.darker(); }
    /** {@inheritDoc} */
    @Override public Color getScoreTextColor() { return Color.WHITE; }

    /**
     * Returns the background color used for the right-side panel in dark theme mode.
     *
     * @return a {@link Color} object representing the panel color.
     */
    @Override
    public Color getRightPanelColor() {
        return Color.web("#8C3A3A");
    }
}