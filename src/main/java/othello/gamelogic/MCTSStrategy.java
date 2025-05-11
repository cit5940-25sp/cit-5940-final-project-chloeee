package othello.gamelogic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import othello.Constants;

/**
 * Implements a Monte Carlo Tree Search (MCTS) strategy for selecting the best move in an Othello game.
 */
public class MCTSStrategy implements Strategy{
//    private static final double EXPLORATION_PARAM = Math.sqrt(2);
    private static final double NUM_ITERATION = 100;
    private static final Random rand = new Random();

    /**
     * Selects the next move using Monte Carlo Tree Search (MCTS).
     *
     * @param board The current board state.
     * @param player The player making the move.
     * @param opponent The opponent player.
     * @return The selected move (BoardSpace) based on simulation results.
     */
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        //make a rootNode
        MCTSNode root = new MCTSNode(board, null, null);
        //1.iterations: training part
            for (int i = 0; i < NUM_ITERATION; i++){
                MCTSNode selectedNode = root.getChildren().isEmpty()? root : findBestUCT(root);
                MCTSNode expandNode = expansion(selectedNode, board, player, opponent);
                boolean win = simulation(expandNode, board, player, opponent);
                backPropagation(win, expandNode);
            }
        //2. use a for loop to loop through all the children and find the one with the best UCT score
       return bestMove(root);
    }

    /**
     * Chooses the best move from the children of a node based on the highest win rate.
     *
     * @param node The root node whose children will be evaluated.
     * @return The BoardSpace move with the highest win rate.
     */
     BoardSpace bestMove(MCTSNode node){  //error 1.was using UCT score to do the final move selection
        double highestWinRate = Double.NEGATIVE_INFINITY;
        MCTSNode bestNode = null;

        for (MCTSNode child : node.getChildren()) {
            if(child.getVisits() == 0){  //1.skip the ones that are not visited
                continue;
            }
            double winRate = child.getWins() / (double)child.getVisits();
            if(winRate > highestWinRate){
                highestWinRate = winRate;
                bestNode = child;
            }
        }
        return bestNode.getMove();
    }

    /**
     * Finds the best child node using the Upper Confidence Bound for Trees (UCT) formula.
     *
     * @param node The node whose children will be evaluated.
     * @return The child node with the highest UCT value.
     */
   MCTSNode findBestUCT(MCTSNode node) {
        MCTSNode bestNode = null;  //1.more conform to convention if we set node to the first child
        double bestUCT = Double.NEGATIVE_INFINITY;

        for (MCTSNode child : node.getChildren()) {
            double cur_UCT;
            //question: when parent visit == 0, randomly choose a child, is that a valid strategy?
            if(child.getVisits() == 0 || node.getVisits() == 0){ //1.debug: node has a visit of 0, which results in cur_UCT being NaN，the whole thing crash
                cur_UCT = Double.POSITIVE_INFINITY; //error：when visits is 0, make its uct infinity，since we want to prioritize exploring unvisited spaces
            } else {
            cur_UCT = (child.getWins() / (double)child.getVisits()) + Constants.EXPLORATION_PARAM * Math.sqrt(Math.log(node.getVisits())/(double)child.getVisits());
            }
            if(cur_UCT > bestUCT || bestNode == null){
                bestUCT = cur_UCT;
                bestNode = child;
            }
        }
        return bestNode == null? node:bestNode;
    }

    /**
     * Expands the given node by generating all valid moves for the current player.
     *
     * @param node The node to expand.
     * @param board The current game board.
     * @param player The player making the move.
     * @param opponent The opponent player.
     * @return A randomly selected child node from the newly created children.
     */
    MCTSNode expansion(MCTSNode node, BoardSpace[][] board, Player player, Player opponent) {
        //expansion
        List<BoardSpace> availableMove = getAvailableMove(board, player);
        //I need a helper function to create a deep copy of the board
        for (BoardSpace move : availableMove) {
            BoardSpace[][] newBoard = copyBoard(board);
            makeMove(newBoard, move, player, opponent);
            MCTSNode child = new MCTSNode(newBoard, node, move);
            node.addChild(child);
        }

        if(node.getChildren().isEmpty()){
            return node;
        }
        return  node.getChildren().get(rand.nextInt(node.getChildren().size()));
    }

    /**
     * Retrieves the list of available moves for the given player.
     *
     * @param board The current board state.
     * @param player The player whose moves are being evaluated.
     * @return A list of available BoardSpace positions.
     */
    private List<BoardSpace> getAvailableMove(BoardSpace[][] board, Player player){
       return new ArrayList<>(player.getAvailableMoves(board).keySet());
    }

    /**
     * Simulates a random playout from the given node to a terminal game state.
     *
     * @param node The node from which simulation starts.
     * @param board The current board state.
     * @param player The player being simulated.
     * @param opponent The opponent player.
     * @return True if the simulation results in a win for the player; false otherwise.
     */
    boolean simulation(MCTSNode node, BoardSpace[][] board, Player player, Player opponent){
        Player current_player = player;
        Player opponent_player = opponent;
        BoardSpace[][] newBoard = copyBoard(board);

        while(!isTerminal(newBoard, current_player, opponent_player)){ //1.error: using board instead of newBoard
            //1.current player randomly choose a move
            List<BoardSpace> moveList = getAvailableMove(newBoard, current_player);

            if(moveList.isEmpty()){ //2.if the current players has no available moves, swap turn
                Player temp = current_player;
                current_player = opponent_player;
                opponent_player = temp;
                continue;
            }

            //3.if there are available moves, go ahead and choose one
            BoardSpace randomMove = moveList.get(rand.nextInt(moveList.size()));
            makeMove(newBoard,randomMove,current_player, opponent_player);

            //4.after you make a move, swap turns
            Player temp = current_player;
            current_player = opponent_player;
            opponent_player = temp;
        }
        //5.returning whether we win, so we need a helper function here
       return evaluateBoard(newBoard, player, opponent);
    }

    /**
     * Propagates the simulation result back up the tree by updating visit and win counts.
     *
     * @param win True if the simulation was a win for the original player.
     * @param expandNode The node from which propagation starts.
     */
    void backPropagation(boolean win, MCTSNode expandNode) {
        MCTSNode node = expandNode;

        while (node != null) {
            node.incrementVisits();
            if (win) {  //1.optimization: dry code
                node.incrementWins();
            }
            node = node.getParent();
        }
    }

    /**
     * Evaluates the board and determines whether the current player has a higher score.
     *
     * @param board The board to evaluate.
     * @param player The player to evaluate score for.
     * @param opponent The opponent player.
     * @return True if the player's score is higher than the opponent's; false otherwise.
     */
    private boolean evaluateBoard(BoardSpace[][] board, Player player, Player opponent){
        int playerScore = 0;
        int opponentScore = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j].getType() == player.getColor()){
                    playerScore += Constants.BOARD_WEIGHTS[i][j];
                } else if (board[i][j].getType() == opponent.getColor()) {
                    opponentScore += Constants.BOARD_WEIGHTS[i][j];
                }

            }
        }
//👇🏻 used for debugging
//        System.out.println("player score: " + playerScore);
//        System.out.println("opponentScore score: " + opponentScore);
        return playerScore > opponentScore;
    }

    /**
     * Checks whether the game has reached a terminal state (no more valid moves).
     *
     * @param board The current board state.
     * @param player The current player.
     * @param opponent The opponent player.
     * @return True if the game is over; false otherwise.
     */
   boolean isTerminal(BoardSpace[][] board, Player player, Player opponent){
        //1.check whether the board is full
        boolean hasEmptySpot = false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j].getType() == BoardSpace.SpaceType.EMPTY){
                    hasEmptySpot = true;
                }
            }
        }
        //1.optimization: return as soon as possible
        if(!hasEmptySpot){
            return true;
        }

        //2.both don't have available moves
        Map<BoardSpace, List<BoardSpace>> playerAvailableMoves = player.getAvailableMoves(board);
        Map<BoardSpace, List<BoardSpace>> opponentAvailableMoves = opponent.getAvailableMoves(board);

        return (playerAvailableMoves.isEmpty() && opponentAvailableMoves.isEmpty());
    }

    /**
     * Creates a deep copy of the current board to simulate moves without altering the original state.
     *
     * @param board The original game board.
     * @return A new deep-copied board.
     */
    private BoardSpace[][] copyBoard(BoardSpace[][] board){ //create a deep copy of board
        int length = board.length;
        BoardSpace[][] newBoard = new BoardSpace[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                newBoard[i][j] = new BoardSpace(board[i][j].getX(), board[i][j].getY(), board[i][j].getType());
            }
        }

        return newBoard;
    }

    /**
     * Executes a move on the board for the player and applies the game rules.
     *
     * @param board The board to apply the move on.
     * @param move The move to apply.
     * @param player The player making the move.
     * @param opponent The opponent player.
     */
    private void makeMove(BoardSpace[][] board, BoardSpace move, Player player, Player opponent){
        OthelloGame tempGame = new OthelloGame(player, opponent);
        tempGame.setBoard(copyBoard(board));

        Map<BoardSpace, List<BoardSpace>> availableMoves = tempGame.getAvailableMoves(player);
        tempGame.takeSpaces(player, opponent, availableMoves, move);
        BoardSpace[][] tempGameBoard = tempGame.getBoard();

        int length = board.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                board[i][j] = new BoardSpace(tempGameBoard[i][j].getX(), tempGameBoard[i][j].getY(), tempGameBoard[i][j].getType());//error:需要深拷贝
            }
        }
    }
}
