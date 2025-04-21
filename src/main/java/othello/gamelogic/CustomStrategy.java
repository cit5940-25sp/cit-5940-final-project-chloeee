package othello.gamelogic;
import othello.Constants;
import java.util.List;
import java.util.Map;

public class CustomStrategy implements Strategy {
    private int MAX_DEPTH = 2;
    private int nodesEvaluated = 0;  // Counter for node evaluations
    
    // Getter for nodes evaluated
    public int getNodesEvaluated() {
        return nodesEvaluated;
    }
    
    // Reset counter
    public void resetNodesEvaluated() {
        nodesEvaluated = 0;
    }
    
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        resetNodesEvaluated();  // Reset counter before each move
        int maxScore = Integer.MIN_VALUE;
        BoardSpace move = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (BoardSpace futureMoves : player.getAvailableMoves(board).keySet()) {

            BoardSpace[][] copiedBoard = copyBoard(board);
            simulate(copiedBoard, player, futureMoves, player.getAvailableMoves(board).get(futureMoves));

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

    // Returns score
    private int minmaximizer(BoardSpace[][] board, Player player, Player opponent, int depth, boolean maximizing, int alpha, int beta) {
        nodesEvaluated++;  // to check the efficiency
        
        if (depth == 0) {
            return scoreBoard(board, player);
        }

        if (maximizing) {
            int maxScore = Integer.MIN_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                BoardSpace[][] copiedBoard = copyBoard(board);
                simulate(copiedBoard, player, move, availableMoves.get(move));
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
                //simulate move
                BoardSpace[][] copiedBoard = copyBoard(board);
                simulate(copiedBoard, opponent, move,availableMoves.get(move));
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

    private void simulate(BoardSpace[][] board, Player player, BoardSpace dest, List<BoardSpace> origins) {
        board[dest.getX()][dest.getY()].setType(player.getColor());

        // For every origin space in List, flip all pieces between origin and destination
        for (BoardSpace origin : origins) {
            int dx = Integer.compare(dest.getX() - origin.getX(), 0);
            int dy = Integer.compare(dest.getY() - origin.getY(), 0);

            int x = origin.getX() + dx;
            int y = origin.getY() + dy;

            while (x != dest.getX() || y != dest.getY()) {
                board[x][y].setType(player.getColor());
                x += dx;
                y += dy;
            }
        }
    }

    // Copy board for simulation
    private BoardSpace[][] copyBoard(BoardSpace[][] board) {
        BoardSpace[][] newBoard = new BoardSpace[board.length][board[0].length];
        for (int row = 0; row < board.length; row ++) {
            for (int col = 0; col < board[0].length; col++) {
                newBoard[row][col] = new BoardSpace(board[row][col].getX(), board[row][col].getY(), board[row][col].getType());
            }
        }
        return newBoard;
    }

    // Get the total score of the board for current player
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