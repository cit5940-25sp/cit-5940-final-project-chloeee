package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * The {@code LightTheme} class provides a light color scheme for the Othello game's graphical user interface.
 *
 * <p>This implementation of the {@link Theme} interface defines specific colors and animation durations
 * to be used for rendering the game board, UI elements, and animations under a light theme aesthetic.</p>
 */
public class LightTheme implements Theme {
    // Basic colors
    /** {@inheritDoc} */
    @Override public Color getBackgroundColor() { return Color.LIGHTGRAY; }

    /** {@inheritDoc} */
    @Override public Color getBoardColor() { return Color.web("#A0C878");}

    /** {@inheritDoc} */
    @Override public Color getTextColor() { return Color.BLACK; }

    // Move indication
    /** {@inheritDoc} */
    @Override public Color getAvailableMoveColor() { return Color.LIGHTYELLOW; }

    /** {@inheritDoc} */
    @Override public Color getAvailableMoveHoverColor() { return Color.LIMEGREEN; }

    // Animation
    /** {@inheritDoc} */
    @Override public Duration getFlipAnimationDuration() { return Duration.millis(300); }

    /** {@inheritDoc} */
    @Override public Duration getButtonPressAnimationDuration() { return Duration.millis(150); }

    /** {@inheritDoc} */
    @Override public Color getButtonPressAnimationColor() { return Color.LIGHTBLUE; }

    // Scoreboard
    /** {@inheritDoc} */
    @Override public Color getPlayerOneScoreColor() { return Color.BLACK; }
    /** {@inheritDoc} */
    @Override public Color getPlayerTwoScoreColor() { return Color.WHITE; }
    /** {@inheritDoc} */
    @Override public Color getScoreBackgroundColor() { return Color.LIGHTGRAY; }
    /** {@inheritDoc} */
    @Override public Color getScoreTextColor() { return Color.BLACK; }

    /**
     * Returns the color used for the right-side panel background in the GUI.
     *
     * @return a {@link Color} object representing the panel background color.
     */
    @Override
    public Color getRightPanelColor() {
        return Color.web("#FDAB9E"); // 라이트 모드 기본값 (원하는 색상으로 변경 가능)
    }
}