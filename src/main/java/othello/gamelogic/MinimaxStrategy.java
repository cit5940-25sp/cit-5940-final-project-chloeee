package othello.gamelogic;

import othello.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinimaxStrategy implements Strategy {

    // TO DO
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        //get potential move

    }

    // Returns score
    private int minmaximizer(BoardSpace[][] board, Player player, Player opponent, int depth, boolean maximizing) {
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
                // Recursive call
                minmaximizer(board, player, opponent, depth-1, false);
                int resultScore = simulate(copiedBoard, player, move,availableMoves.get(move));
                maxScore = Math.max(resultScore, maxScore);
            }
        }
        // Minimizer
        else {
            int minScore = Integer.MAX_VALUE;
            Map<BoardSpace, List<BoardSpace>> availableMoves = opponent.getAvailableMoves(board);
            for (BoardSpace move : availableMoves.keySet()) {
                //simulate move
                BoardSpace[][] copiedBoard = copyBoard(board);
                int resultScore = simulate(copiedBoard, player, move,availableMoves.get(move));
                minScore = Math.min(resultScore, minScore);
            }
        }

        return 0;
    }

    private int simulate(BoardSpace[][] board, Player player, BoardSpace dest, List<BoardSpace> origins) {
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

    // Copy board for simulation
    private BoardSpace[][] copyBoard(BoardSpace[][] board) {
        BoardSpace[][] newBoard = new BoardSpace[board.length][board[0].length];
        for (int row = 0; row < board.length; row ++) {
            for (int col = 0; col < board[0].length; col++) {
                newBoard[row][col] = board[row][col];
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
                } else if ((board[row][col].getType() != BoardSpace.SpaceType.EMPTY) {
                    score -= Constants.BOARD_WEIGHTS[row][col];
                }
            }
        }
        return score;
    }



}