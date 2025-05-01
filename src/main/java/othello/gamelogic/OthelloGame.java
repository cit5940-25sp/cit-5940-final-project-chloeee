package othello.gamelogic;

import java.util.*;

/**
 * Models a board of Othello.
 * Includes methods to get available moves and take spaces.
 */
public class OthelloGame {
    public static final int GAME_BOARD_SIZE = 8;

    private BoardSpace[][] board;
    private final Player playerOne;
    private final Player playerTwo;

    public OthelloGame(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        initBoard();
    }

    public void setBoard(BoardSpace[][] board) {
        this.board = board;
    }

    public BoardSpace[][] getBoard() {
        return board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return  playerTwo;
    }

    /**
     * Returns the available moves for a player.
     * Used by the GUI to get available moves each turn.
     * @param player player to get moves for
     * @return the map of available moves,that maps destination to list of origins
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(Player player) {
        return player.getAvailableMoves(board);
    }

    /**
     * Initializes the board at the start of the game with all EMPTY spaces.
     */
    public void initBoard() {
        board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }

        //standard Othello configuration - fix bug 1: ensure we update both GUI and initial board
        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Claims the specified space for the acting player.
     * Should also check if the space being taken is already owned by the acting player,
     * should not claim anything if acting player already owns space at (x,y)
     * @param actingPlayer the player that will claim the space at (x,y)
     * @param opponent the opposing player, will lose a space if their space is at (x,y)
     * @param x the x-coordinate of the space to claim
     * @param y the y-coordinate of the space to claim
     */
    public void takeSpace(Player actingPlayer, Player opponent, int x, int y) {
        // check x and y are valid inputs
        if (x < 0 || x >= GAME_BOARD_SIZE || y < 0 || y >= GAME_BOARD_SIZE) {
            return;
        }
        // No need - check actingPlayer, opponent valid
        // Construct BoardSpace for this x-y space
        BoardSpace boardSpace = board[x][y];

        // First check for current player bc if already owned by them, do nothing
        if (boardSpace.getType() == actingPlayer.getColor()) {
            return;
        }
        // If NOT already owned by current player, it could be empty or owned by opponent
        // either way this might change, so need to record current state before changing
        BoardSpace.SpaceType previousType = boardSpace.getType(); // type is an ENUM represents STATE: empty, black, white

        // Flow of game in GameController and getAvailableMoves() means being able call
        // takeSpace on a board space owned by opponent is legal and results in a 'win' flip of opponent's color

        // Update the lists based on previous state
        // Remove this x-y space from opponent's list of board spaces
        if (previousType == opponent.getColor()) {
            opponent.getPlayerOwnedSpacesSpaces().remove(boardSpace);
        }

        // Change the space type (COLOR) to current player's color (already checked this is possible)
        boardSpace.setType(actingPlayer.getColor());

        // Add this x-y space to acting player's list of board spaces
        actingPlayer.getPlayerOwnedSpacesSpaces().add(boardSpace);

        // check if space already taken by actingPlayer (which has getter of List<BoardSpace> playerOwnedSpaces )
        actingPlayer.getPlayerOwnedSpacesSpaces();
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Claims spaces from all origins that lead to a specified destination.
     * This is called when a player, human or computer, selects a valid destination.
     * @param actingPlayer the player that will claim spaces
     * @param opponent the opposing player, that may lose spaces
     * @param availableMoves map of the available moves, that maps destination to list of origins
     * @param selectedDestination the specific destination that a HUMAN player selected
     */
    // Whereas getAvailableMoves identifies where a player can legally place a piece and which existing pieces form valid "sandwich" lines
    // takeSpaces actually executes a move by placing a piece and flipping all sandwiched pieces
    public void takeSpaces(Player actingPlayer, Player opponent, Map<BoardSpace, List<BoardSpace>> availableMoves, BoardSpace selectedDestination) {
        // Claim the specified space (selectedDestination) for the current player; rest of method only activates after this is DONE
        takeSpace(actingPlayer, opponent, selectedDestination.getX(), selectedDestination.getY());

        // availablesMoves is a map with a destination BoardSpace mapped to a List of origin BoardSpaces.
        // Keys: Valid destination spaces where a player can place a new piece
        // Values: Lists of "origin" spaces (those player already owns) that form valid sandwiching lines with that destination
        // Get all origins (values: List) for this selected destination (keys) from Map <BoardSpace, List<BoardSpace>>
        List<BoardSpace> origins = availableMoves.get(selectedDestination);

        // For every origin space in List, flip all pieces between origin and destination
        if(origins == null) return; //debug/testing //error: origin完全有可能是null，比如你
        for (BoardSpace origin : origins) {
            // Indicate -1, 0, or 1 for the direction to take by comparing x-y coordinates of selected and origin spaces
            int dx = Integer.compare(selectedDestination.getX() - origin.getX(), 0);
            int dy = Integer.compare(selectedDestination.getY() - origin.getY(), 0);

            // Start from the space NEXT to origin (which is already occupied, so you don't start from origin itself)
            int x = origin.getX() + dx;
            int y = origin.getY() + dy;

            // Because availableMoves map already ensured every piece in that line between origin and destination is flippable
            // We can keep calling takeSpace on every space until we get to destination x-y
            while (x != selectedDestination.getX() || y != selectedDestination.getY()) {
                takeSpace(actingPlayer, opponent, x, y);
                x += dx;
                y += dy;
            }
        }
    }
    /**
     * PART 2
     * TODO: Implement this method
     * Gets the computer decision for its turn.
     * Should call a method within the ComputerPlayer class that returns a BoardSpace using a specific strategy.
     * @param computer computer player that is deciding their move for their turn
     * @return the BoardSpace that was decided upon
     */
    public BoardSpace computerDecision(ComputerPlayer computer) {
        return computer.chooseMove(board,playerOne,playerTwo);
    }

}
