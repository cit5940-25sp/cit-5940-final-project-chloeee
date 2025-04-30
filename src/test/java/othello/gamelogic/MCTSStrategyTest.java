package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MCTSStrategyTest {
    private ComputerPlayer playerBlack;
    private HumanPlayer playerWhite;
    private BoardSpace[][] board;

    private MCTSNode root;

    private MCTSStrategy mctsStrategy;

    private static final int BOARD_SIZE = 8;

    @Before
    public void setUp() {
        //we need 2 players and a board
        //1.initialize players
        playerBlack = new ComputerPlayer("mcts");
        playerBlack.setColor(BoardSpace.SpaceType.BLACK);

        playerWhite = new HumanPlayer();
        playerWhite.setColor(BoardSpace.SpaceType.WHITE);


        //2. initialize board
        board = new BoardSpace[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }


        //3.set up the initial layout for the board
        board[3][3] = new BoardSpace(2, 3, BoardSpace.SpaceType.BLACK);
        board[4][3] = new BoardSpace(3, 3, BoardSpace.SpaceType.BLACK);
        board[4][4] = new BoardSpace(3, 4, BoardSpace.SpaceType.BLACK);
        board[5][2] = new BoardSpace(4, 2, BoardSpace.SpaceType.WHITE);
        board[5][3] = new BoardSpace(4, 3, BoardSpace.SpaceType.WHITE);
        board[5][4] = new BoardSpace(4, 4, BoardSpace.SpaceType.WHITE);


        //4. initialize strategy
        mctsStrategy = new MCTSStrategy();


        //5.initialize root
        root = new MCTSNode(board, null, null);

    }

    @Test
    public void testChooseMove() {
//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            for (int j = 0; j < BOARD_SIZE; j++) {
//                System.out.print(board[i][j]);
//            }
//        }
//        System.out.println();
//        Map<BoardSpace, List<BoardSpace>> availableMoves = playerBlack.getAvailableMoves(board);
//        for (BoardSpace boardSpace : availableMoves.keySet()) {
//            System.out.println(boardSpace);
//        }
//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ debugging

        //1.test that computer are able to choose a move among the available moves
        BoardSpace chosenMove = playerBlack.chooseMove(board, playerBlack, playerWhite);
        System.out.println("Chosen move :" + chosenMove);
        Map<BoardSpace, List<BoardSpace>> availableMoves = playerBlack.getAvailableMoves(board);
        assertNotNull(chosenMove);
        assertTrue(availableMoves.containsKey(chosenMove));


        //2.the chosen move needs to be an empty space
        assertEquals(chosenMove.getType(), BoardSpace.SpaceType.EMPTY);


//        // Use MCTS to choose a move for the black player
//        BoardSpace chosenMove = mctsStrategy.selectMove(board, playerBlack, playerWhite);
//
//        // Verify that the chosen move is valid
//        assertNotNull("MCTS should return a valid move", chosenMove);
//        assertTrue("Chosen move should be an empty space",
//            board[chosenMove.getX()][chosenMove.getY()].getType() == BoardSpace.SpaceType.EMPTY);
//
//        // Optionally, verify that the move is one of the available moves
//        Map<BoardSpace, List<BoardSpace>> availableMoves = playerBlack.getAvailableMoves(board);
//        assertTrue("Chosen move should be in the list of available moves",
//            availableMoves.containsKey(chosenMove));
    }

    @Test
    public void testBestMove() {
        //1.fake the parent child situation
        MCTSNode parentNode = new MCTSNode(board, null, null);
        MCTSNode child1 = new MCTSNode(board, parentNode, new BoardSpace(1, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child2 = new MCTSNode(board, parentNode, new BoardSpace(2, 3, BoardSpace.SpaceType.WHITE));
        MCTSNode child3 = new MCTSNode(board, parentNode, new BoardSpace(4, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child4 = new MCTSNode(board, parentNode, new BoardSpace(5, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child5 = new MCTSNode(board, parentNode, new BoardSpace(5, 5, BoardSpace.SpaceType.WHITE));


        parentNode.addChild(child1);
        parentNode.addChild(child2);
        parentNode.addChild(child3);
        parentNode.addChild(child4);
        parentNode.addChild(child5);


        for (int i = 0; i < 5; i++) {
            child1.incrementVisits();
            child2.incrementVisits();
            child3.incrementVisits();
            child4.incrementVisits();
            child1.incrementWins();
        }


        for (int i = 0; i < 2; i++) {
            child2.incrementWins();
            child3.incrementWins();
            child4.incrementWins();
        }

        //2.test whether we choose the first child. Child 5 has a visit of 0, and it needs to be skipped
        System.out.println(mctsStrategy.bestMove(parentNode));
        assertNotNull(mctsStrategy.bestMove(parentNode));
        assertEquals(mctsStrategy.bestMove(parentNode), child1.getMove());

    }


    @Test
    public void testFindBestUCT() {

        //1.fake the parent child situation
        MCTSNode parentNode = new MCTSNode(board, null, null);
        MCTSNode child1 = new MCTSNode(board, parentNode, new BoardSpace(1, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child2 = new MCTSNode(board, parentNode, new BoardSpace(2, 3, BoardSpace.SpaceType.WHITE));
        MCTSNode child3 = new MCTSNode(board, parentNode, new BoardSpace(4, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child4 = new MCTSNode(board, parentNode, new BoardSpace(5, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode child5 = new MCTSNode(board, parentNode, new BoardSpace(5, 5, BoardSpace.SpaceType.WHITE));


        parentNode.addChild(child1);
        parentNode.addChild(child2);
        parentNode.addChild(child3);
        parentNode.addChild(child4);
        parentNode.addChild(child5);


        for (int i = 0; i < 5; i++) {
            child1.incrementVisits();
            child2.incrementVisits();
            child3.incrementVisits();
            child4.incrementVisits();
            child1.incrementWins();
            parentNode.incrementVisits();//debug: increment parent node also, so we are choosing the right children
        }


        for (int i = 0; i < 2; i++) {
            child2.incrementWins();
            child3.incrementWins();
            child4.incrementWins();
        }

        System.out.println(child1.getVisits());

        //2.test that in this case, we choose child5--the not explored one
//        System.out.println(child5.getVisits());
//        System.out.println(mctsStrategy.findBestUCT(parentNode).getMove());
        assertEquals(mctsStrategy.findBestUCT(parentNode), child5);

        //question: when parent visit == 0, randomly choose a child, is that a valid strategy?


    }

    @Test
    public void testExpansion() {

        //1.test whether the randomly returned child is among the avilable moves
        MCTSNode randomChild = mctsStrategy.expansion(root, board, playerBlack, playerWhite);
        //👇🏻 for debug. each time it gives me a different child
//        System.out.println(randomChild.getMove());
        Map<BoardSpace, List<BoardSpace>> availableMoves = playerBlack.getAvailableMoves(board);
        //👇🏻 for debug.
//        Set<BoardSpace> moves = availableMoves.keySet();
//        for (BoardSpace move : moves) {
//            System.out.print(move + " ");
//        }

        assertTrue(availableMoves.containsKey(randomChild.getMove()));
        assertEquals(randomChild.getParent(), root);


    }


    @Test
    public void testSimulation() {
        //1. since simulation is going until the end, let's just test whether it returns a value
        boolean result = mctsStrategy.simulation(root, board, playerBlack, playerWhite);
        assertTrue(result == true || result == false);


        //2.or we can manipulate the result by faking a situation where black is obvious more than white
        BoardSpace[][] newBoard = new BoardSpace[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoard[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.BLACK);
            }
        }

//        newBoard[0][0] = new BoardSpace(0, 0, BoardSpace.SpaceType.BLACK);
//        newBoard[0][7] = new BoardSpace(0, 1, BoardSpace.SpaceType.BLACK);
//        newBoard[0][7] = new BoardSpace(7, 7, BoardSpace.SpaceType.BLACK);
//        newBoard[0][7] = new BoardSpace(7, 0, BoardSpace.SpaceType.BLACK);

        MCTSNode newRoot = new MCTSNode(newBoard, null, null);



        boolean result2 = mctsStrategy.simulation(newRoot, newBoard, playerBlack, playerWhite);
        assertTrue(result2);


    }


    @Test
    public void testSimulationDifferentResult() {

//1.the initial board didn't give black or white obvious advantage. Therefore, there should be different result
        Set<Boolean> results = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            boolean result = mctsStrategy.simulation(root, board, playerBlack, playerWhite);
            results.add(result);
        }

        assertEquals(results.size(), 2);
    }


    @Test
    public void testSimulationNotChangeBoard() {
        //1.make a copy of the original board
        BoardSpace[][] originalBoard = new BoardSpace[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                originalBoard[i][j] = new BoardSpace(board[i][j]);
            }
        }


        //2. run simulation on the
        for (int i = 0; i < 20; i++) {
            mctsStrategy.simulation(root, board, playerBlack, playerWhite);
        }


        //3.see whether the board changed
        boolean hasChanged = false;
        outer:
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(originalBoard[i][j].getType() != board[i][j].getType()){
                    hasChanged = true;
                    break outer;
                }
            }
        }

        assertFalse(hasChanged);



    }




    @Test
    public void testIsTerminal() {
        BoardSpace[][] newBoard = new BoardSpace[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoard[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.BLACK);
            }
        }
        boolean terminal = mctsStrategy.isTerminal(newBoard, playerBlack, playerWhite);
        assertTrue(terminal);

    }


    @Test
    public void testBackPropagation() {
        //1.fake the parent child situation
        MCTSNode generation0 = new MCTSNode(board, null, null);
        MCTSNode generation1 = new MCTSNode(board, generation0, new BoardSpace(1, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode generation2 = new MCTSNode(board, generation1, new BoardSpace(2, 3, BoardSpace.SpaceType.WHITE));
        MCTSNode generation3 = new MCTSNode(board, generation2, new BoardSpace(4, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode generation4 = new MCTSNode(board, generation3, new BoardSpace(5, 2, BoardSpace.SpaceType.WHITE));
        MCTSNode generation5 = new MCTSNode(board, generation4, new BoardSpace(5, 5, BoardSpace.SpaceType.WHITE));


        generation0.addChild(generation1);
        generation1.addChild(generation2);
        generation2.addChild(generation3);
        generation3.addChild(generation4);
        generation4.addChild(generation5);

        assertEquals(generation0.getVisits(), 0);
        assertEquals(generation0.getWins(), 0);

        assertEquals(generation2.getVisits(), 0);
        assertEquals(generation2.getWins(), 0);

        assertEquals(generation5.getVisits(), 0);
        assertEquals(generation5.getWins(), 0);


        mctsStrategy.backPropagation(true, generation5);//so before is all 0 👆, and after is all 1


        assertEquals(generation0.getVisits(), 1);
        assertEquals(generation0.getWins(), 1);

        assertEquals(generation2.getVisits(), 1);
        assertEquals(generation2.getWins(), 1);

        assertEquals(generation5.getVisits(), 1);
        assertEquals(generation5.getWins(), 1);



    }

}