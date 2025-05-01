package othello.gui;

import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LightTheme implements Theme {
    // Basic colors
    @Override public Color getBackgroundColor() { return Color.LIGHTGRAY; }
    @Override public Color getBoardColor() { return Color.web("#A0C878");}
    @Override public Color getTextColor() { return Color.BLACK; }

    // Move indication
    @Override public Color getAvailableMoveColor() { return Color.LIGHTYELLOW; }
    @Override public Color getAvailableMoveHoverColor() { return Color.LIMEGREEN; }

    // Animation
    @Override public Duration getFlipAnimationDuration() { return Duration.millis(300); }
    @Override public Duration getButtonPressAnimationDuration() { return Duration.millis(150); }
    @Override public Color getButtonPressAnimationColor() { return Color.LIGHTBLUE; }

    // Scoreboard
    @Override public Color getPlayerOneScoreColor() { return Color.BLACK; }
    @Override public Color getPlayerTwoScoreColor() { return Color.WHITE; }
    @Override public Color getScoreBackgroundColor() { return Color.LIGHTGRAY; }
    @Override public Color getScoreTextColor() { return Color.BLACK; }

    @Override
    public Color getRightPanelColor() {
        return Color.web("#FDAB9E"); // 라이트 모드 기본값 (원하는 색상으로 변경 가능)
    }
}