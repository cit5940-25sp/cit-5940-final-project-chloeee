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
    public List<BoardSpace> getPlayerOwnedSpacesSpaces() {
        return playerOwnedSpaces;
    }

    private BoardSpace.SpaceType color;
    public void setColor(BoardSpace.SpaceType color) {
        this.color = color;
    }
    public BoardSpace.SpaceType getColor() {
        return color;
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Gets the available moves for this player given a certain board state.
     * This method will find destinations, empty spaces that are valid moves,
     * and map them to a list of origins that can traverse to those destinations.
     * @param board the board that will be evaluated for possible moves for this player
     * @return a map with a destination BoardSpace mapped to a List of origin BoardSpaces.
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

                        //debug👇🏻
//                        if (origins.isEmpty()) {
//                            System.out.println("empty origin");
////                            availableMoves.put(current, origins);
//                        }


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

//    private BoardSpace getSingleOrigin(int x, int y, int dx, int dy, BoardSpace[][] board) {
//        int nextX = x + dx;
//        int nextY = y + dy;
//
//        if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board[0].length) {
//            return null;
//        }
//
//        BoardSpace current = board[nextX][nextY];
//
//        if (current.getType() == BoardSpace.SpaceType.EMPTY || current.getType() == color) {
//            return null;
//        }
//
//        BoardSpace firstOpponent = current;
//
//        while (true) {
//            nextX += dx;
//            nextY += dy;
//
//            if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board[0].length) {
//                return null;
//            }
//
//            current = board[nextX][nextY];
//            if (current.getType() == BoardSpace.SpaceType.EMPTY) {
//                return null;
//            }
//
//            if (current.getType() == color) {
//                return firstOpponent;  // ✅ return the sandwiching opponent piece
//            }
//        }
//    }


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
