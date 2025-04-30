package othello.gamelogic;
import othello.Constants;
import java.util.List;
import java.util.Map;

import static othello.gamelogic.OthelloGame.GAME_BOARD_SIZE;

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
        Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
        for (BoardSpace futureMoves : availableMoves.keySet()) {
//            System.out.println("Trying move: " + futureMoves);
//            System.out.println("Flipping origins: " + player.getAvailableMoves(board).get(futureMoves));
            List<BoardSpace> origins = availableMoves.get(futureMoves);

            if (origins == null || origins.isEmpty()) {
                System.out.println("⚠️ Skipping move due to missing origins: " + futureMoves);
                continue;
            }

            BoardSpace[][] copiedBoard = copyBoard(board);
            simulate(copiedBoard, futureMoves, origins, player);
//            for (BoardSpace[] boardSpaces : copiedBoard) {
//                for (BoardSpace boardSpace : boardSpaces) {
//                    System.out.println(boardSpace + " ");
//                }
//                System.out.println();
//            }

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
    int minmaximizer(BoardSpace[][] board, Player player, Player opponent, int depth, boolean maximizing, int alpha, int beta) {
//        System.out.println("I am called! depth : " + depth);
        nodesEvaluated++;  // to check the efficiency
        
        if (depth == 0) { //so this is the base case
//debug            for (BoardSpace[] boardSpaces : board) {
//                for (BoardSpace boardSpace : boardSpaces) {
//                    System.out.print(boardSpace + " ");
//                }
//                System.out.println();
//            }
            return scoreBoard(board, player, Constants.BOARD_WEIGHTS);
        }

        if (maximizing) {
            int maxScore = Integer.MIN_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
//                System.out.println("Available moves for player1a: " + player.getAvailableMoves(board).keySet());

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
//                System.out.println("Available moves for player2: " + player.getAvailableMoves(board).keySet());
                //simulate move
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

    private void simulate(BoardSpace[][] board, BoardSpace dest, List<BoardSpace> origins, Player player) {//change copied board
        //create a temporary game and call take spaces function, and change the copied board

        // check x and y are valid inputs
        if (dest.getX() < 0 || dest.getX() >= GAME_BOARD_SIZE || dest.getY() < 0 || dest.getY() >= GAME_BOARD_SIZE) {
            return;
        }

        board[dest.getX()][dest.getY()].setType(player.getColor());

        if(origins == null) return;

        // For every origin space in List, flip all pieces between origin and destination
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
    private int scoreBoard(BoardSpace[][] board, Player player, int[][] boardWeights) {
        int score = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col =0; col < board[0].length; col++) {
                if (board[row][col].getType() == player.getColor()) {
//                    System.out.println("Add score: " + boardWeights[row][col]);
                    score += boardWeights[row][col];
                } else if (board[row][col].getType() != BoardSpace.SpaceType.EMPTY) {
//                    System.out.println("Minus score: " + boardWeights[row][col]);
                    score -= boardWeights[row][col];
                }
            }
        }
        return score;
    }
}