package othello.gamelogic;
import othello.Constants;
import java.util.List;
import java.util.Map;

/**
 * Implements a Minimax strategy for Othello game AI.
 * Evaluates future board states recursively up to a certain depth and selects the optimal move.
 */
public class MinimaxStrategy implements Strategy {
    private int MAX_DEPTH = 2;
    private int nodesEvaluated = 0;  // Counter for node evaluations

    /**
     * Returns the number of nodes evaluated in the last Minimax run.
     *
     * @return Number of nodes evaluated.
     */
    public int getNodesEvaluated() {
        return nodesEvaluated;
    }

    /**
     * Resets the internal counter for the number of nodes evaluated.
     */
    // Reset counter
    public void resetNodesEvaluated() {
        nodesEvaluated = 0;
    }

    /**
     * Selects the best move for the current player using the Minimax algorithm.
     *
     * @param board    The current game board.
     * @param player   The player making the move.
     * @param opponent The opposing player.
     * @return The optimal {@link BoardSpace} to play.
     */
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        resetNodesEvaluated();  // Reset counter before each move
        //get potential move
        int maxScore = Integer.MIN_VALUE;
        BoardSpace move = null;
        for (BoardSpace futureMoves : player.getAvailableMoves(board).keySet()) {
            //simulate move
            BoardSpace[][] copiedBoard = copyBoard(board);
            simulate(copiedBoard, player, futureMoves, player.getAvailableMoves(board).get(futureMoves));
            //recursive call
            int score = minmaximizer(copiedBoard, player, opponent, MAX_DEPTH, false);
            if (score > maxScore) {
                maxScore = score;
                move = futureMoves; 
            }
        }
        return move;
    }

    /**
     * Recursive Minimax evaluation function.
     *
     * @param board      Current board state.
     * @param player     The AI player.
     * @param opponent   The opposing player.
     * @param depth      Current depth of recursion.
     * @param maximizing True if this node is a maximizing node, false if minimizing.
     * @return The evaluated score of the board.
     */
    private int minmaximizer(BoardSpace[][] board, Player player, Player opponent, int depth, boolean maximizing) {
        nodesEvaluated++;  // Increment counter for each node evaluated (check efficiency)
        
        if (depth == 0) {
            return scoreBoard(board, player);
        }
        // We need potential moves player can do
        if (maximizing) {
            int maxScore = Integer.MIN_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                //simulate move
                BoardSpace[][] copiedBoard = copyBoard(board);
                // Immediate result board after my single move, 
                // changing the board to pass into the recursive call
                simulate(copiedBoard, player, move, availableMoves.get(move));
                // This is the result of my move (opponent's turn)
                int score = minmaximizer(copiedBoard, player, opponent, depth-1, false);
                maxScore = Math.max(maxScore, score);
            }
            return maxScore;
        }
        // Minimizer
        else {
            int minScore = Integer.MAX_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = opponent.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                //simulate move
                BoardSpace[][] copiedBoard = copyBoard(board);
                simulate(copiedBoard, opponent, move,availableMoves.get(move));
                int score = minmaximizer(copiedBoard, player, opponent, depth-1, true);
                minScore = Math.min(minScore, score);
            }
            return minScore;
        }
    }

    /**
     * Simulates placing a piece on the board and flipping the opponent's pieces accordingly.
     *
     * @param board   The board on which to simulate.
     * @param player  The player making the move.
     * @param dest    The destination {@link BoardSpace} where the piece is placed.
     * @param origins The list of origin {@link BoardSpace}s indicating directions to flip.
     */
    private void simulate(BoardSpace[][] board, Player player, BoardSpace dest, List<BoardSpace> origins) {
        // Change the destination color
        board[dest.getX()][dest.getY()].setType(player.getColor());

        // For every origin space in List, flip all pieces between origin and destination
        for (BoardSpace origin : origins) {
            // Indicate -1, 0, or 1 for the direction to take by comparing x-y coordinates of selected and origin spaces
            int dx = Integer.compare(dest.getX() - origin.getX(), 0);
            int dy = Integer.compare(dest.getY() - origin.getY(), 0);

            // Start from the space NEXT to origin (which is already occupied, so you don't start from origin itself)
            int x = origin.getX() + dx;
            int y = origin.getY() + dy;

            // Because availableMoves map already ensured every piece in that line between origin and destination is flippable
            // We can keep calling takeSpace on every space until we get to destination x-y
            while (x != dest.getX() || y != dest.getY()) {
                board[x][y].setType(player.getColor());
                x += dx;
                y += dy;
            }
        }
    }

    /**
     * Creates a deep copy of the current board state.
     *
     * @param board The board to copy.
     * @return A new deep-copied board.
     */
    private BoardSpace[][] copyBoard(BoardSpace[][] board) {
        BoardSpace[][] newBoard = new BoardSpace[board.length][board[0].length];
        for (int row = 0; row < board.length; row ++) {
            for (int col = 0; col < board[0].length; col++) {
                newBoard[row][col] = new BoardSpace(board[row][col].getX(), board[row][col].getY(), board[row][col].getType());
            }
        }
        return newBoard;
    }

    /**
     * Scores the board from the perspective of the given player using static positional weights.
     *
     * @param board  The current board state.
     * @param player The player whose perspective is used for scoring.
     * @return The board score (positive is good for player, negative is bad).
     */
    private int scoreBoard(BoardSpace[][] board, Player player) {
        int score = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col =0; col < board[0].length; col++) {
                if (board[row][col].getType() == player.getColor()) {
                    score += Constants.BOARD_WEIGHTS[row][col];
                } else if (board[row][col].getType() != BoardSpace.SpaceType.EMPTY) {
                    score -= Constants.BOARD_WEIGHTS[row][col];
                }
            }
        }
        return score;
    }
}