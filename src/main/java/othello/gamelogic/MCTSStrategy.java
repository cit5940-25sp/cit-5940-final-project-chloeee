package othello.gamelogic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import othello.Constants;

public class MCTSStrategy implements Strategy{

    private static final double EXPLORATION_PARAM = Math.sqrt(2);
    private static final double NUM_ITERATION = 100;


    private static final Random rand = new Random();



    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        //make a rootNode
        MCTSNode root = new MCTSNode(board, null, null);  //question: why copy board in the beginning



        //1.iterations: training part
            for (int i = 0; i < NUM_ITERATION; i++){
                MCTSNode selectedNode = root.getChildren().isEmpty()? root : findBestUCT(root);
                MCTSNode expandNode = expansion(selectedNode, board, player, opponent);
                boolean win = simulation(expandNode, board, player, opponent);
                backPropagation(win, expandNode);
            }



        //2. use a for loop to loop through all the children and find the one with the best UCT score
       return bestMove(root);//question：boardspace never visited before would be infinity. I think Im just worried

    }

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
//
//        // fallback: no visited children, just pick a random child
//        if (bestNode == null && !node.getChildren().isEmpty()) {
//            bestNode = node.getChildren().get(new Random().nextInt(node.getChildren().size()));
//        }
//
//        // still null? (shouldn't happen, but guard anyway)
//        if (bestNode == null) {
//            return null;
//        }


        return bestNode.getMove();

    }


   MCTSNode findBestUCT(MCTSNode node) {
        MCTSNode bestNode = null;  //1.more conform to convention if we set node to the first child
        double bestUCT = Double.NEGATIVE_INFINITY;


        for (MCTSNode child : node.getChildren()) {
            double cur_UCT;
            //question: when parent visit == 0, randomly choose a child, is that a valid strategy?
            if(child.getVisits() == 0 || node.getVisits() == 0){ //1.debug: node has a visit of 0, which results in cur_UCT being NaN，the whole thing crash
                cur_UCT = Double.POSITIVE_INFINITY; //error：when visits is 0, make its uct infinity，since we want to prioritize exploring unvisited spaces
            } else {
            cur_UCT = (child.getWins() / (double)child.getVisits()) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.getVisits())/(double)child.getVisits());
            }
            if(cur_UCT > bestUCT || bestNode == null){
                bestUCT = cur_UCT;
                bestNode = child;
            }
        }

        //question:我这样行不行?这样我就不用做判断了,初始值是node

        return bestNode == null? node:bestNode;

    }


    private MCTSNode expansion(MCTSNode node, BoardSpace[][] board, Player player, Player opponent) {
        //expansion就是长出节点
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

        return  node.getChildren().get(rand.nextInt(node.getChildren().size())); //不知道这个表达式能不能对
    }


    private List<BoardSpace> getAvailableMove(BoardSpace[][] board, Player player){
       return new ArrayList<>(player.getAvailableMoves(board).keySet());
    }


    private boolean simulation(MCTSNode node, BoardSpace[][] board, Player player, Player opponent){
        Player current_player = player;
        Player opponent_player = opponent;
        BoardSpace[][] newBoard = copyBoard(board); //question: do i need to make a newbaord here?

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


            //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓totally not necessary 😂 review the definition for backpropagation
            //3.1 create a new node and add child each time you're able to make a move
//            MCTSNode newNode = new MCTSNode(newBoard, parentNode, randomMove);
//            parentNode.addChild(newNode);
//            parentNode = newNode;
            ///↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

            //4.after you make a move, swap turns
            Player temp = current_player;
            current_player = opponent_player;
            opponent_player = temp;


        }

        //5.returning whether we win, so we need a helper function here
       return evaluateBoard(newBoard, player, opponent);

    }


    private void backPropagation(boolean win, MCTSNode expandNode) {
        //question:expandNode我是不是也需要increment？
//        expandNode.incrementWins();
//        expandNode.incrementWins();
//
        MCTSNode node = expandNode;

        while (node != null) {
            node.incrementVisits();
            if (win) {  //1.optimization: dry code
                node.incrementWins();
            }
            node = node.getParent();
        }
    }

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

        return playerScore > opponentScore; //question: > or >=?


    }


    private boolean isTerminal(BoardSpace[][] board, Player player, Player opponent){
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


    private BoardSpace[][] copyBoard(BoardSpace[][] board){ //create a deep copy of board
        int length = board.length;
        BoardSpace[][] newBoard = new BoardSpace[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                newBoard[i][j] = new BoardSpace(board[i][j].getX(), board[i][j].getY(), board[i][j].getType());//error:需要深拷贝
            }
        }

        return newBoard;
    }

    private void makeMove(BoardSpace[][] board, BoardSpace move, Player player, Player opponent){
        //make a move and change on the original board
//        Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
//        List<BoardSpace> origins = availableMoves.get(move);
        //question：我当时就有一个疑惑，我这么没有othello game，我用不了那些我写好的function，结果人家建了一个othello game
        OthelloGame tempGame = new OthelloGame(player, opponent);
        tempGame.setBoard(copyBoard(board));

        Map<BoardSpace, List<BoardSpace>> availableMoves = tempGame.getAvailableMoves(player);
//        if(!availableMoves.containsKey(move)) {
//            return;
//        } // debug: deal with orgin == null
        tempGame.takeSpaces(player, opponent, availableMoves, move);
        BoardSpace[][] tempGameBoard = tempGame.getBoard();

//        board = tempGame.getBoard(); //error: board 在这个函数内部指向另一个board对象，原来的board没有改变
        int length = board.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                board[i][j] = new BoardSpace(tempGameBoard[i][j].getX(), tempGameBoard[i][j].getY(), tempGameBoard[i][j].getType());//error:需要深拷贝
            }
        }

    }





}
