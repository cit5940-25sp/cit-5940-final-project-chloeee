package othello.gui;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.junit.runner.Computer;
import othello.gamelogic.*;
import java.util.List;
import java.util.Map;

/**
 * Manages the interaction between model and view of the game.
 */
public class GameController  {

    // FXML Variables to manipulate UI components
    @FXML private Label turnLabel;
    @FXML private Pane gameBoard;
    @FXML private Circle turnCircle;
    @FXML private Button computerTurnBtn;
    @FXML private Button themeToggleBtn; // Added button
    @FXML private Rectangle blackScoreBar;
    @FXML private Rectangle whiteScoreBar;
    @FXML private Rectangle blackScoreBackground;
    @FXML private Rectangle whiteScoreBackground;
    @FXML private Pane rightPanel;

    // Private variables
    private OthelloGame og;
    private int skippedTurns;
    private GUISpace[][] guiBoard;
    private Theme currentTheme; // adding the Theme

    @FXML
    public void initialize() {
        currentTheme = new LightTheme(); //set the default theme
        applyTheme();
        rightPanel.setStyle("-fx-background-color: " + colorToHex(currentTheme.getRightPanelColor()));
    }

    /**
     * Starts the game, called after controller initialization  in start method of App.
     * Sets the 2 players, their colors, and creates an OthelloGame for logic handling.
     * Then, shows the first move, beginning the game "loop".
     * @param arg1 type of player for player 1, either "human" or some computer strategy
     * @param arg2 type of player for player 2, either "human" or some computer strategy
     */
    public void initGame(String arg1, String arg2) {
        Player playerOne;
        Player playerTwo;

        // Factory pattern implementation
//        playerOne = PlayerFactory.createPlayer(arg1);
//        playerTwo = PlayerFactory.createPlayer(arg2);
        // Previous code without Factory pattern
//        // Player 1
        if (arg1.equals("human")) {
            playerOne = new HumanPlayer();
        } else {
            playerOne = new ComputerPlayer(arg1);
        }

        // Player 2
        if (arg2.equals("human")) {
            playerTwo = new HumanPlayer();
        } else {
            playerTwo = new ComputerPlayer(arg2);
        }

        // Set Colors
        playerOne.setColor(BoardSpace.SpaceType.BLACK);
        playerTwo.setColor(BoardSpace.SpaceType.WHITE);

        // Make a new game, create the visual board and display it with initial spaces
        og = new OthelloGame(playerOne, playerTwo);
        guiBoard = new GUISpace[8][8];
        displayBoard();
        initSpaces();
        updateScoreBoard();

        // Player 1 starts the game
        turnText(playerOne);
        takeTurn(playerOne);
    }

    /**
     * Toggles between Light and Dark themes and applies the selected theme to the game board and UI.
     */
    @FXML
    protected void toggleTheme() {
        if (currentTheme instanceof LightTheme) {
            currentTheme = new DarkTheme();
            themeToggleBtn.setText("Light Mode");
        } else {
            currentTheme = new LightTheme();
            themeToggleBtn.setText("Dark Mode");
        }
        applyTheme();
        updateScoreBoard();
        animateButtonPress(themeToggleBtn);

        // If it's a human player's turn, re-show available moves with new theme colors
        Player currentPlayer = og.getCurrentPlayer(); // You'll need to add getCurrentPlayer() to OthelloGame
        turnText(currentPlayer);

        // If it's a human player's turn, re-show available moves
        if (currentPlayer instanceof HumanPlayer) {
            computerTurnBtn.setVisible(false);
            computerTurnBtn.setOnAction(null);
            computerTurnBtn.setOnAction(e -> {
                computerDecision((ComputerPlayer) currentPlayer);
            });
            showMoves((HumanPlayer) currentPlayer);
        } else {
            computerTurnBtn.setVisible(true);
            showMoves((HumanPlayer) currentPlayer);
        }
    }

    /**
    /**
     * Updates the visual score bars representing each player's score.
     */
    private void updateScoreBoard() {
        int blackScore = countColor(og.getBoard(), og.getPlayerOne().getColor());
        int whiteScore = countColor(og.getBoard(), og.getPlayerTwo().getColor());
        int total = blackScore + whiteScore;
        double blackWidth = total > 0 ? (blackScore / (double) total) * 100 : 0;
        double whiteWidth = total > 0 ? (whiteScore / (double) total) * 100 : 0;

        Timeline blackTimeline = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(blackScoreBar.widthProperty(), blackWidth))
        );

        Timeline whiteTimeline = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(whiteScoreBar.widthProperty(), whiteWidth))
        );

        new ParallelTransition(blackTimeline, whiteTimeline).play();
    }

    /**
     * Applies the current theme's colors to UI elements such as the game board, labels, and buttons.
     */
    private void applyTheme() {
        rightPanel.setStyle(
                "-fx-background-color: " + colorToHex(currentTheme.getRightPanelColor()) +
                        "; -fx-border-color: black; -fx-border-width: 0 0 0 2;"
        );
        gameBoard.setStyle("-fx-background-color: " + colorToHex(currentTheme.getBackgroundColor()));
        turnLabel.setTextFill(currentTheme.getTextColor());

        if (guiBoard != null) {
            for (GUISpace[] row : guiBoard) {
                for (GUISpace space : row) {
                    if (space != null) {
                        space.setTheme(currentTheme);
                    }
                }
            }
        }
        // Update computer turn button style
        if (currentTheme instanceof LightTheme) {
            computerTurnBtn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #000000;");
        } else {
            computerTurnBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: #FFFFFF;");
        }
    }

    /**
     * Converts a JavaFX {@link Color} to its hexadecimal string representation.
     *
     * @param color the JavaFX Color to convert
     * @return a hex string (e.g., "#FFFFFF")
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    /**
     * Converts a JavaFX {@link Color} to a CSS-compatible RGBA background string.
     *
     * @param color the JavaFX Color to convert
     * @return a CSS rgba() string (e.g., "-fx-background-color: rgba(255, 255, 255, 1.0);")
     */
    private String colorToCss(Color color) {
        return String.format("-fx-background-color: rgba(%d, %d, %d, %.2f);",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                color.getOpacity());
    }

    /**
     * Displays the board initially, adding the GUI squares into the window.
     * Also adds the initial state of the board with black and white taking spaces at the center.
     */
    @FXML
    protected void displayBoard() {
        BoardSpace[][] board = og.getBoard();
        for (BoardSpace[] spaces : board) {
            for (BoardSpace space : spaces) {
                GUISpace guiSpace = new GUISpace(space.getX(), space.getY(), space.getType());
                //bug fix
                guiSpace.setTheme(currentTheme);
                Pane square = guiSpace.getSquare();
                gameBoard.getChildren().add(square);
                guiBoard[space.getX()][space.getY()] = guiSpace;
            }
        }
    }

    /**
     * Clears the board visually, called every time the board is redisplayed after the first time
     */
    @FXML
    protected void clearBoard() {
        BoardSpace[][] board = og.getBoard();
        for (BoardSpace[] spaces : board) {
            for (BoardSpace space : spaces) {
                GUISpace guiSpace = guiBoard[space.getX()][space.getY()];
                Pane square = guiSpace.getSquare();
                gameBoard.getChildren().remove(square);
            }
        }
    }

    /**
     * Sets the initial state of the Othello board
     */
    @FXML
    protected void initSpaces(){
        // Initial spaces
        guiBoard[3][3].addOrUpdateDisc(BoardSpace.SpaceType.WHITE);
        guiBoard[4][4].addOrUpdateDisc(BoardSpace.SpaceType.WHITE);
        guiBoard[3][4].addOrUpdateDisc(BoardSpace.SpaceType.BLACK);
        guiBoard[4][3].addOrUpdateDisc(BoardSpace.SpaceType.BLACK);
    }

    /**
     * Counts the number of board spaces that match the given type (BLACK or WHITE).
     *
     * @param board the current game board
     * @param type the {@link BoardSpace.SpaceType} to count
     * @return the number of matching board spaces
     */
    private int countColor(BoardSpace[][] board, BoardSpace.SpaceType type) {
        int count = 0;
        for (BoardSpace[] row : board) {
            for (BoardSpace space : row) {
                if (space.getType() == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Displays the score of the board and the current turn.
     */

    @FXML
    protected void turnText(Player player) {
        String humanOrCom = player instanceof HumanPlayer ? "(Human)\n" : "(Computer)\n";
        turnCircle.setFill(player.getColor().fill());
        turnLabel.setText(
                player.getColor() + "'s Turn\n" + humanOrCom + "Score: \n" +
                        og.getPlayerOne().getColor() + ": " + countColor(og.getBoard(), og.getPlayerOne().getColor()) + " - " +
                        og.getPlayerTwo().getColor() + ": " + countColor(og.getBoard(), og.getPlayerTwo().getColor()));
        turnLabel.setTextFill(currentTheme.getTextColor());
    }

    /**
     * Applies a quick visual animation to a button to simulate a press effect.
     *
     * @param button the button to animate
     */
    private void animateButtonPress(Button button) {
        String originalStyle = button.getStyle();
        Color animationColor = currentTheme.getButtonPressAnimationColor();
        String hexColor = String.format("#%02X%02X%02X",
                (int)(animationColor.getRed() * 255),
                (int)(animationColor.getGreen() * 255),
                (int)(animationColor.getBlue() * 255));

        button.setStyle(originalStyle + "-fx-effect: dropshadow(gaussian, " + hexColor + ", 20, 0.5, 0, 0);");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.opacityProperty(), 1.0)),
                new KeyFrame(currentTheme.getButtonPressAnimationDuration(),
                        new KeyValue(button.opacityProperty(), 0.7),
                        new KeyValue(button.styleProperty(), originalStyle))
        );
        timeline.play();
    }

    /**
     * Updates the turn label to indicate that the specified player had to skip their turn
     * due to having no available moves. Also displays the current score for both players.
     *
     * @param player the player who has no valid moves and must skip their turn
     */
    @FXML
    protected void skipTurnText(Player player) {
        turnLabel.setText(
                "Skipped " + player.getColor() + " \n due to no moves available! \n" + otherPlayer(player).getColor() + "'s Turn\n" +
                        og.getPlayerOne().getColor() + ": " + countColor(og.getBoard(), og.getPlayerOne().getColor()) + " - " +
                        og.getPlayerTwo().getColor() + ": " + countColor(og.getBoard(), og.getPlayerTwo().getColor()));
    }

    /**
     * Initiates a turn for the specified player.
     * For human players, highlights valid move locations.
     * For computer players, enables a button to make the automated move.
     *
     * @param player the player whose turn it is (can be human or computer)
     */
    @FXML
    protected void takeTurn(Player player) {
        if (player instanceof HumanPlayer human) {
            computerTurnBtn.setVisible(false);
            showMoves(human);
        } else if (player instanceof ComputerPlayer computer) {
            computerTurnBtn.setVisible(true);
            computerTurnBtn.setOnAction(actionEvent -> {
                computerDecision(computer);
            });
        }
    }

    /**
     * Shows the current moves possible for the current board configuration.
     * If availableMoves is null, the getAvailableMoves method is likely not implemented yet.
     * If availableMoves is empty (no moves found, or full board), the game ends.
     * @param player player to show moves for
     */
    @FXML
    protected void showMoves(HumanPlayer player) {
        Map<BoardSpace, List<BoardSpace>> availableMoves = og.getAvailableMoves(player);
        if (availableMoves == null) {
            turnLabel.setText("Null move found for \n" + player.getColor() + "! \n Please implement \ngetAvailableMoves()!");
        } else if (availableMoves.size() == 0) {
            if (countColor(og.getBoard(), og.getPlayerOne().getColor()) + countColor(og.getBoard(), og.getPlayerTwo().getColor()) != 64 && skippedTurns != 2) {
                skipTurnText(player);
                takeTurn(otherPlayer(player));
                skippedTurns++;
            } else if (skippedTurns == 2 || countColor(og.getBoard(), og.getPlayerOne().getColor()) + countColor(og.getBoard(), og.getPlayerTwo().getColor()) == 64){
                gameOver();
            }
        } else {
            skippedTurns = 0;
            for (BoardSpace destination : availableMoves.keySet()) {
                // Attach hover listener (Enter, Exit) to each Pane
                GUISpace guiSpace = guiBoard[destination.getX()][destination.getY()];
                Pane currPane = guiSpace.getSquare();
                guiSpace.setBgColor(Color.LIGHTYELLOW);
                EventHandler<MouseEvent> enter = event -> guiSpace.setBgColor(Color.LIME);
                EventHandler<MouseEvent> exit = event -> guiSpace.setBgColor(Color.LIGHTYELLOW);
                currPane.addEventHandler(MouseEvent.MOUSE_ENTERED, enter);
                currPane.addEventHandler(MouseEvent.MOUSE_EXITED, exit);
                // Click removes hovers
                currPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    currPane.removeEventHandler(MouseEvent.MOUSE_ENTERED, enter);
                    currPane.removeEventHandler(MouseEvent.MOUSE_EXITED, exit);
                    selectSpace(player, availableMoves, destination);
                });
            }
        }

    }

    /**
     * Gets the computer decision, then selects the space.
     * @param player a reference to the current computer player (could be player 1 or 2)
     */
    @FXML
    protected void computerDecision(ComputerPlayer player) {
        Map<BoardSpace, List<BoardSpace>> availableMoves = og.getAvailableMoves(player);
        if (availableMoves == null) {
            turnLabel.setText("Null move found for \n" + player.getColor() + "! \n Please implement \ncomputerDecision()!");
        } else if (availableMoves.size() == 0) {
            if (countColor(og.getBoard(), og.getPlayerOne().getColor()) + countColor(og.getBoard(), og.getPlayerTwo().getColor()) != 64 && skippedTurns != 2) {
                skipTurnText(player);
                takeTurn(otherPlayer(player));
                skippedTurns++;
            } else if (skippedTurns == 2 || countColor(og.getBoard(), og.getPlayerOne().getColor()) + countColor(og.getBoard(), og.getPlayerTwo().getColor()) == 64){
                gameOver();
            }

        } else {
            skippedTurns = 0;
//            BoardSpace selectedDestination = og.computerDecision(player);
            BoardSpace selectedDestination = player.chooseMove(og.getBoard(), player, otherPlayer(player));

            // From all origins, path to the destination and take spaces
            og.takeSpaces(player, otherPlayer(player), availableMoves, selectedDestination);
            updateGUIBoard(player, availableMoves, selectedDestination);

            // Redisplay the new board
            clearBoard();
            displayBoard();

            // Next opponent turn
            turnText(otherPlayer(player));
            takeTurn(otherPlayer(player));
        }
        updateScoreBoard();

    }

    /**
     * Handles what happens when a player chooses to select a certain space during their turn.
     * @param player current turn's player
     * @param availableMoves the available moves gotten from showMoves earlier
     * @param selectedDestination the selected destination space that was clicked on
     */
    @FXML
    protected void selectSpace(Player player, Map<BoardSpace, List<BoardSpace>> availableMoves, BoardSpace selectedDestination) {
        // Remove other handlers by reinitializing empty spaces where they are
        for (BoardSpace destination : availableMoves.keySet()) {
            GUISpace guiSpace = guiBoard[destination.getX()][destination.getY()];
            if (destination != selectedDestination) {
                // Reinit unselected spaces, to remove event handlers
                og.getBoard()[destination.getX()][destination.getY()] =
                        new BoardSpace(destination.getX(), destination.getY(), BoardSpace.SpaceType.EMPTY);
                gameBoard.getChildren().remove(guiSpace.getSquare());
                GUISpace newGuiSpace = new GUISpace(destination.getX(), destination.getY(), BoardSpace.SpaceType.EMPTY);
                Pane newSquare = newGuiSpace.getSquare();
                gameBoard.getChildren().add(newSquare);
                guiBoard[destination.getX()][destination.getY()] = guiSpace;
            } else {
                og.getBoard()[destination.getX()][destination.getY()] =
                        new BoardSpace(destination.getX(), destination.getY(), player.getColor());
                gameBoard.getChildren().remove(guiSpace.getSquare());
                GUISpace newGuiSpace = new GUISpace(destination.getX(), destination.getY(), player.getColor());
                Pane newSquare = newGuiSpace.getSquare();
                gameBoard.getChildren().add(newSquare);
                guiBoard[destination.getX()][destination.getY()] = guiSpace;
            }
        }

        // Recolor the bg of the destination
        GUISpace guiSpace = guiBoard[selectedDestination.getX()][selectedDestination.getY()];
        guiSpace.setBgColor(Color.LIMEGREEN);

        // From all origins, path to the destination and take spaces
        og.takeSpaces(player, otherPlayer(player), availableMoves, selectedDestination);
        updateGUIBoard(player, availableMoves, selectedDestination);

        // Redisplay the new board
        clearBoard();
        displayBoard();

        updateScoreBoard();

        // Next opponent turn
        turnText(otherPlayer(player));
        takeTurn(otherPlayer(player));
    }

    /**
     * Updates the GUI Board by adding or updating discs from all origins to a given destination
     * @param player player that is taking a turn
     * @param availableMoves the list of all available destinations and origins
     * @param selectedDestination the selected destination from either user input or computer decision
     */
    @FXML
    protected void updateGUIBoard(Player player, Map<BoardSpace, List<BoardSpace>> availableMoves, BoardSpace selectedDestination) {
        List<BoardSpace> selectedOrigins = availableMoves.get(selectedDestination);
        //👇🏻debug
        if (selectedOrigins == null) {
//            System.err.println("Error: selectedDestination is not in availableMoves!");
            return;
        }
        for (BoardSpace selectedOrigin : selectedOrigins) {
            int offsetX = selectedDestination.getX() - selectedOrigin.getX();
            int offsetY = selectedDestination.getY() - selectedOrigin.getY();
            // Intercardinals
            if (Math.abs(offsetX) > 0 && Math.abs(offsetY) > 0) {
                for (int i = 0; i < Math.abs(offsetX) + 1; i++) {
                    int x = (int) (selectedOrigin.getX() + (i * Math.signum((offsetX))));
                    int y = (int) (selectedOrigin.getY() + (i * Math.signum((offsetY))));
                    guiBoard[x][y].addOrUpdateDisc(player.getColor());
                }
            } else { // Cardinals
                // Origin -> Destination
                for (int i = 0; i < Math.abs(offsetX) + 1; i++) {
                    for (int j = 0; j < Math.abs(offsetY) + 1; j++) {
                        int x = (int) (selectedOrigin.getX() + (i * Math.signum((offsetX))));
                        int y = (int) (selectedOrigin.getY() + (j * Math.signum((offsetY))));
                        guiBoard[x][y].addOrUpdateDisc(player.getColor());
                    }
                }
            }
        }
    }

    /**
     * Returns the other player given one of the player fields
     */
    @FXML
    protected Player otherPlayer(Player player) {
        if (player == og.getPlayerOne()) {
            return og.getPlayerTwo();
        } else {
            return og.getPlayerOne();
        }
    }

    /**
     * Ends the game.
     */
    @FXML
    protected void gameOver() {
        boolean p1Victory = false;
        boolean tie = false;
        if (countColor(og.getBoard(), og.getPlayerOne().getColor()) > countColor(og.getBoard(), og.getPlayerTwo().getColor())) {
            p1Victory = true;
        } else if (countColor(og.getBoard(), og.getPlayerOne().getColor()) == countColor(og.getBoard(), og.getPlayerTwo().getColor())) {
            tie = true;
        }
        if (tie) {
            turnLabel.setText("🎉🎉GAME OVER!🎉🎉 \n Game Tied with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + countColor(og.getBoard(), og.getPlayerOne().getColor()) + " - " +
                    og.getPlayerTwo().getColor() + ": " + countColor(og.getBoard(), og.getPlayerTwo().getColor()));
        } else if (p1Victory) {
            turnLabel.setText("🎉🎉GAME OVER!🎉🎉 \n BLACK wins with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + countColor(og.getBoard(), og.getPlayerOne().getColor()) + " - " +
                    og.getPlayerTwo().getColor() + ": " + countColor(og.getBoard(), og.getPlayerTwo().getColor()));
        } else {
            turnLabel.setText("🎉🎉GAME OVER!🎉🎉 \n WHITE wins with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + countColor(og.getBoard(), og.getPlayerOne().getColor()) + " - " +
                    og.getPlayerTwo().getColor() + ": " + countColor(og.getBoard(), og.getPlayerTwo().getColor()));
        }
    }
}