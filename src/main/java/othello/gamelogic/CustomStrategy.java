package othello.gamelogic;
import othello.Constants;
import java.util.List;
import java.util.Map;

import static othello.gamelogic.OthelloGame.GAME_BOARD_SIZE;


/**
 * A custom strategy for the Othello game implementing the Strategy interface.
 * This strategy uses a depth-limited Minimax algorithm with alpha-beta pruning to select the best move.
 */
public class CustomStrategy implements Strategy {
    private int MAX_DEPTH = 2;
    private int nodesEvaluated = 0;  // Counter for node evaluations
    private int testing = 0;




    /**
     * Gets the number of nodes evaluated during the most recent move computation.
     *
     * @return the number of evaluated nodes
     */
    public int getNodesEvaluated() {
        return nodesEvaluated;
    }

    /**
     * Resets the counter tracking the number of evaluated nodes.
     */
    public void resetNodesEvaluated() {
        nodesEvaluated = 0;
    }

    /**
     * Selects the best move for the given player using the Minimax algorithm with alpha-beta pruning.
     *
     * @param board    the current board state
     * @param player   the current player
     * @param opponent the opposing player
     * @return the selected move as a {@code BoardSpace}
     */
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        resetNodesEvaluated();  // Reset counter before each move
        int maxScore = Integer.MIN_VALUE;
        BoardSpace move = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
        for (BoardSpace futureMoves : availableMoves.keySet()) {
            List<BoardSpace> origins = availableMoves.get(futureMoves);

            if (origins == null || origins.isEmpty()) {
                System.out.println("⚠️ Skipping move due to missing origins: " + futureMoves);
                continue;
            }

            BoardSpace[][] copiedBoard = copyBoard(board);
            simulate(copiedBoard, futureMoves, origins, player);

            int score = minmaximizer(copiedBoard, player, opponent, MAX_DEPTH, false, alpha, beta);
            if (score > maxScore) {
                maxScore = score;
                move = futureMoves;
            }
            alpha = Math.max(alpha, score);  // Update alpha with the actual score, not maxScore
            if (beta <= alpha) {
                break;
            }
        }
        return move;
    }

    /**
     * Recursive implementation of Minimax algorithm with alpha-beta pruning.
     *
     * @param board      the current board state
     * @param player     the maximizing player
     * @param opponent   the minimizing opponent
     * @param depth      the remaining search depth
     * @param maximizing whether the current layer is maximizing
     * @param alpha      current alpha value
     * @param beta       current beta value
     * @return the evaluated score
     */
    int minmaximizer(BoardSpace[][] board, Player player, Player opponent, int depth, boolean maximizing, int alpha, int beta) {
        nodesEvaluated++;  // to check the efficiency

        if (depth == 0) { //so this is the base case
            return scoreBoard(board, player, Constants.BOARD_WEIGHTS);
        }

        if (maximizing) {
            int maxScore = Integer.MIN_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                BoardSpace[][] copiedBoard = copyBoard(board);
                simulate(copiedBoard, move, availableMoves.get(move), player);
                int score = minmaximizer(copiedBoard, player, opponent, depth-1, false, alpha, beta);
                maxScore = Math.max(maxScore, score);
                // alpha-beta pruning
                alpha = Math.max(alpha, maxScore);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;
        }
        // Minimizer
        else {
            int minScore = Integer.MAX_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = opponent.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                BoardSpace[][] copiedBoard = copyBoard(board); //deep copy
                simulate(copiedBoard, move, availableMoves.get(move), opponent); //change copied board
                int score = minmaximizer(copiedBoard, player, opponent, depth-1, true, alpha, beta);
                minScore = Math.min(minScore, score);
                // alpha-beta pruning
                beta = Math.min(beta, minScore);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            return minScore;
        }
    }

    /**
     * Simulates a move by flipping all affected pieces between origins and destination on the board.
     *
     * @param board   the board to modify
     * @param dest    the destination of the move
     * @param origins the list of origin pieces to flip from
     * @param player  the current player making the move
     */

    private void simulate(BoardSpace[][] board, BoardSpace dest, List<BoardSpace> origins, Player player) {//change copied board

        if (dest.getX() < 0 || dest.getX() >= GAME_BOARD_SIZE || dest.getY() < 0 || dest.getY() >= GAME_BOARD_SIZE) {
            return;
        }

        board[dest.getX()][dest.getY()].setType(player.getColor());

        if(origins == null) return;

        for (BoardSpace origin : origins) {
            int dx = Integer.compare(dest.getX() - origin.getX(), 0);
            int dy = Integer.compare(dest.getY() - origin.getY(), 0);

            int x = origin.getX() + dx;
            int y = origin.getY() + dy;



            while (x != dest.getX() || y != dest.getY()) {//we could do this only because they are on the same line: vertically, horizontally, or diagonally
                if (x < 0 || x >= GAME_BOARD_SIZE || y < 0 || y >= GAME_BOARD_SIZE) {
                    return; // skip this direction
                }
                board[x][y].setType(player.getColor());
                x += dx;
                y += dy;
            }
        }
    }

    /**
     * Creates a deep copy of the provided board.
     *
     * @param board the original board to copy
     * @return a new board array with copied {@code BoardSpace} objects
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
     * Evaluates the board by calculating the weighted score for the specified player.
     * Opponent scores are subtracted from the player's score.
     *
     * @param board         the board to evaluate
     * @param player        the player whose score is being computed
     * @param boardWeights  the weights assigned to each position on the board
     * @return the computed score
     */
    private int scoreBoard(BoardSpace[][] board, Player player, int[][] boardWeights) {
        int score = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col =0; col < board[0].length; col++) {
                if (board[row][col].getType() == player.getColor()) {
                    score += boardWeights[row][col];
                } else if (board[row][col].getType() != BoardSpace.SpaceType.EMPTY) {
                    score -= boardWeights[row][col];
                }
            }
        }
        return score;
    }
}