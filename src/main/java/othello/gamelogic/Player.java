package othello.gamelogic;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static othello.gamelogic.OthelloGame.GAME_BOARD_SIZE;

/**
 * Abstract Player class for representing a player within the game.
 * All types of Players have a color and a set of owned spaces on the game board.
 */
public abstract class Player {
    private final List<BoardSpace> playerOwnedSpaces = new ArrayList<>();
    /**
     * Returns the list of spaces currently owned by this player.
     *
     * @return a list of BoardSpace objects owned by the player
     */

    public List<BoardSpace> getPlayerOwnedSpacesSpaces() {
        return playerOwnedSpaces;
    }
    private BoardSpace.SpaceType color;
    /**
     * Sets the color (piece type) of this player.
     *
     * @param color the color (BLACK or WHITE) to assign to the player
     */
    public void setColor(BoardSpace.SpaceType color) {
        this.color = color;
    }
    /**
     * Retrieves the current color (piece type) of this player.
     *
     * @return the color assigned to the player
     */
    public BoardSpace.SpaceType getColor() {
        return color;
    }

     /**
     * Determines all valid moves available for this player given the current board state.
     * A valid move is represented as a destination (empty space) and a list of origin pieces
     * that can traverse in a straight line to flip opponent pieces.
     *
     * @param board the current state of the Othello game board
     * @return a map where each key is a valid destination BoardSpace, and its value is a list
     *         of origin BoardSpaces that justify the move in specific directions
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(BoardSpace[][] board) {
        //👇🏻debug
//        System.out.println("Checking moves for color: " + color);

        Map<BoardSpace, List<BoardSpace>> availableMoves = new HashMap<>();

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                BoardSpace current = board[i][j];
                if (current.getType() == BoardSpace.SpaceType.EMPTY) {
                    List<BoardSpace> origins = new ArrayList<>();

                    for (int[] dir : directions) {
                        int dx = dir[0];
                        int dy = dir[1];
                        BoardSpace origin = getSingleOrigin(i, j, dx, dy, board);
                        if (origin != null) {
                            origins.add(origin);
                        }
                    }
                    if (!origins.isEmpty()) {
                        availableMoves.put(current, origins);
                    }
                }
            }
        }


        return availableMoves;
    }


    /**
     * Helper method that checks a specific direction from an empty board cell to determine
     * if there exists a valid move path starting from an opponent's piece and ending on one of the player's pieces.
     *
     * @param x     the x-coordinate (row) of the potential destination space
     * @param y     the y-coordinate (column) of the potential destination space
     * @param dx    the delta in x-direction to check
     * @param dy    the delta in y-direction to check
     * @param board the current game board
     * @return the BoardSpace of the first opponent piece encountered in a valid sandwich,
     *         or null if no valid path exists in the given direction
     */
    private BoardSpace getSingleOrigin(int x, int y, int dx, int dy, BoardSpace[][] board) {
        int nextX = x + dx;
        int nextY = y + dy;

        // Check if we're still within board bounds
        if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board.length) {
            return null;
        }

        BoardSpace current = board[nextX][nextY];

        // If the adjacent space is empty or same color, not a valid move
        if (current.getType() == BoardSpace.SpaceType.EMPTY || current.getType() == color) {
            return null;
        }

        // Keep moving in the direction until we find our color or hit an empty space
        while (true) {
            nextX += dx;
            nextY += dy;

            // Check bounds
            if (nextX < 0 || nextX >= GAME_BOARD_SIZE || nextY < 0 || nextY >= GAME_BOARD_SIZE) {
                return null;
            }

            current = board[nextX][nextY];
            if (current.getType() == BoardSpace.SpaceType.EMPTY) {
                return null;
            }
            if (current.getType() == color) {
                return board[nextX][nextY];
            }
        }
    }
}
