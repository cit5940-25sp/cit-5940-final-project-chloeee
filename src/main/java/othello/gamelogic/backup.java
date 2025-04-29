//package othello.gamelogic;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Map;
//import othello.Constants;
//public class backup implements Strategy {
//    private static final int ITERATIONS = 1000;
//    private static final double EXPLORATION_PARAM = Math.sqrt(2);
//
//    @Override
//    public BoardSpace selectMove(BoardSpace[][] board, Player computerPlayer, Player opponent) {
//        MCTSNode root = new MCTSNode(copyBoard(board), null, null);
//
//        for (int i = 0; i < ITERATIONS; i++) {
//            MCTSNode selectedNode = selection(root);
//            MCTSNode expandedNode = expansion(selectedNode, computerPlayer);
//            boolean win = simulation(expandedNode, computerPlayer, opponent);
//            backpropagation(expandedNode, win);
//        }
//
//        return bestMove(root);
//    }
//
//
//    //*************************************************************************************************************
//    //those 2 are totally replace by the function findBestUCT
//    private MCTSNode selection(MCTSNode node) {
//        while (!node.getChildren().isEmpty()) {
//            node = bestUCT(node);
//        }
//        return node;
//    }
//
//    private MCTSNode bestUCT(MCTSNode node) {
//        MCTSNode bestNode = null;
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        for (MCTSNode child : node.getChildren()) {
//            double uctValue = (child.getWins() / (double) child.getVisits()) +
//                    EXPLORATION_PARAM * Math.sqrt(Math.log(node.getVisits()) / (double) child.getVisits());
//            if (uctValue > bestValue) {
//                bestValue = uctValue;
//                bestNode = child;
//            }
//        }
//
//        return bestNode;
//    }
//    //*************************************************************************************************************
//
//    //error for expansion:你这里opponent不能null，你的make move里面用takes spaces，需要真正的opponent
//    //👇🏻*************************************************************************************************************
//    private MCTSNode expansion(MCTSNode node, Player player) {
//        List<BoardSpace> moves = getAvailableMoves(node.getBoard(), player);
//        for (BoardSpace move : moves) {
//            BoardSpace[][] newBoard = copyBoard(node.getBoard());
//            makeMove(newBoard, move, player, null); // Implement makeMove logic
//            MCTSNode childNode = new MCTSNode(newBoard, node, move);
//            node.addChild(childNode);
//        }
//        if (node.getChildren().isEmpty()){
//            return node;
//        }
//        return node.getChildren().get(new Random().nextInt(node.getChildren().size()));
//    }
//
//    //👆🏻*************************************************************************************************************
//
//
//    private boolean simulation(MCTSNode node, Player player, Player opponent) {
//        BoardSpace[][] board = copyBoard(node.getBoard());
//        Player currentPlayer = player;
//        Player currentOpponent = opponent;
//
//        while (!isTerminal(board, currentPlayer, currentOpponent)) {
//            List<BoardSpace> moves = getAvailableMoves(board, currentPlayer);
//            if (moves.isEmpty()) {
//                Player temp = currentPlayer;
//                currentPlayer = currentOpponent;
//                currentOpponent = temp;
//                continue;
//            }
//            BoardSpace move = moves.get(new Random().nextInt(moves.size()));
//            makeMove(board, move, currentPlayer, currentOpponent);
//            Player temp = currentPlayer;
//            currentPlayer = currentOpponent;
//            currentOpponent = temp;
//        }
//
//        return evaluateWinner(board, player, opponent); // Implement evaluateWinner logic
//    }
//
//    private void backpropagation(MCTSNode node, boolean win) {
//        while (node != null) {
//            node.incrementVisits();
//            if (win) {
//                node.incrementWins();
//            }
//            node = node.getParent();
//        }
//    }
//
//    private BoardSpace bestMove(MCTSNode root) {
//        MCTSNode bestNode = null;
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        for (MCTSNode child : root.getChildren()) {
//            double winRate = child.getWins() / (double) child.getVisits();
//            if (winRate > bestValue) {
//                bestValue = winRate;
//                bestNode = child;
//            }
//        }
//
//        return bestNode.getMove();
//    }
//    //This function is not implemented yet. That's why/
//    //there's some value in wrapping this function since we could just return ArrayList here
//    //and we use list a lot to get random move
//    // *************************************************************************************************************
//
//    private List<BoardSpace> getAvailableMoves(BoardSpace[][] board, Player player) {
//        // Implement logic to get available moves for the player
//        return new ArrayList<>();
//    }
//    //*************************************************************************************************************
//
//
//    //*************************************************************************************************************
//    //helper function for expansion
//    private void makeMove(BoardSpace[][] board, BoardSpace move, Player player, Player opponent) {
//        // Implement logic to make a move on the board
//        // Create a temporary OthelloGame instance to use the existing methods
//        OthelloGame tempGame = new OthelloGame(player, opponent);
//        tempGame.initBoard(); // Initialize the board
//
//        // Copy the current board state to the temporary game
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board[i].length; j++) {
//                tempGame.getBoard()[i][j] = new BoardSpace(board[i][j]);
//            }
//        }
//
//
//        // Get available moves for the player
//        Map<BoardSpace, List<BoardSpace>> availableMoves = tempGame.getAvailableMoves(player);
//
//        // Use takeSpaces to make the move and flip the pieces
//        tempGame.takeSpaces(player, opponent, availableMoves, move);
//
//        // Update the original board with the new state
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board[i].length; j++) {
//                board[i][j] = tempGame.getBoard()[i][j];
//            }
//        }
//    }
//
//    //*************************************************************************************************************
//
//
//    private boolean isTerminal(BoardSpace[][] board,  Player playerOne, Player playerTwo) {
//         // Check if the board is completely filled
//        boolean isBoardFull = true;
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board[i].length; j++) {
//                if (board[i][j].getType() == BoardSpace.SpaceType.EMPTY) {
//                    isBoardFull = false;
//                    break;
//                }
//            }
//            if (!isBoardFull) {
//                break;
//            }
//        }
//
//        // If the board is full, the game is terminal
//        if (isBoardFull) {
//            return true;
//        }
//
//        // Check if either player has any legal moves
//        boolean playerOneHasMoves = !playerOne.getAvailableMoves(board).isEmpty();
//        boolean playerTwoHasMoves = !playerTwo.getAvailableMoves(board).isEmpty();
//
//        // If neither player has moves, the game is terminal
//        return !playerOneHasMoves && !playerTwoHasMoves;
//    }
//
//    private boolean evaluateWinner(BoardSpace[][] board, Player player, Player opponent) {
//        // Implement logic to evaluate if the player is the winner
//        int playerScore = 0;
//        int opponentScore = 0;
//
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board[i].length; j++) {
//                if (board[i][j].getType() == player.getColor()) {
//                    playerScore += Constants.BOARD_WEIGHTS[i][j];
//                } else if (board[i][j].getType() == opponent.getColor()) {
//                    opponentScore += Constants.BOARD_WEIGHTS[i][j];
//                }
//            }
//        }
//
//        return (playerScore - opponentScore > 0) ;
//    }
//
//    private BoardSpace[][] copyBoard(BoardSpace[][] board) {
//        BoardSpace[][] newBoard = new BoardSpace[board.length][board[0].length];
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board[i].length; j++) {
//                newBoard[i][j] = new BoardSpace(board[i][j]);
//            }
//        }
//        return newBoard;
//    }
//}