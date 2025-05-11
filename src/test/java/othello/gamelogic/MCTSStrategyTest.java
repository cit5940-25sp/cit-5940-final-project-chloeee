package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link MCTSStrategy}, a Monte Carlo Tree Search implementation.
 * These tests validate MCTS logic including selection, expansion, simulation, and backpropagation.
 */
public class MCTSStrategyTest {
    private ComputerPlayer playerBlack;
    private HumanPlayer playerWhite;
    private BoardSpace[][] board;
    private MCTSNode root;
    private MCTSStrategy mctsStrategy;
    private static final int BOARD_SIZE = 8;
    /**
     * Initializes a simple board and players before each test.
     */
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
    /**
     * Tests that the MCTS strategy chooses a valid move that is:
     * - not null
     * - in the list of available moves
     * - an empty board space
     */
    @Test
    public void testChooseMove() {
        //1.test that computer are able to choose a move among the available moves
        BoardSpace chosenMove = playerBlack.chooseMove(board, playerBlack, playerWhite);
        System.out.println("Chosen move :" + chosenMove);
        Map<BoardSpace, List<BoardSpace>> availableMoves = playerBlack.getAvailableMoves(board);
        assertNotNull(chosenMove);
        assertTrue(availableMoves.containsKey(chosenMove));

        //2.the chosen move needs to be an empty space
        assertEquals(chosenMove.getType(), BoardSpace.SpaceType.EMPTY);
    }
    /**
     * Tests that {@code bestMove()} correctly returns the most promising child based on win rate.
     */

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

    /**
     * Tests that {@code findBestUCT()} returns the child with the best UCT score,
     * prioritizing unexplored children (visits = 0).
     */
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
        // System.out.println(child5.getVisits());
        // System.out.println(mctsStrategy.findBestUCT(parentNode).getMove());
        assertEquals(mctsStrategy.findBestUCT(parentNode), child5);
        //question: when parent visit == 0, randomly choose a child, is that a valid strategy?
    }
    /**
     * Tests that {@code expansion()} correctly adds children to the node
     * and returns a child with a valid move.
     */
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

    /**
     * Tests that {@code simulation()} returns a boolean and does not crash.
     * Also verifies the outcome with a clearly winning board state.
     */
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

        MCTSNode newRoot = new MCTSNode(newBoard, null, null);

        boolean result2 = mctsStrategy.simulation(newRoot, newBoard, playerBlack, playerWhite);
        assertTrue(result2);
    }

    /**
     * Ensures the simulation sometimes results in different outcomes,
     * indicating randomness in game play when the result is not deterministic.
     */
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

    /**
     * Verifies that {@code simulation()} does not alter the original board state.
     */
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


    /**
     * Tests that {@code isTerminal()} correctly detects a full board.
     */
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

    /**
     * Tests {@code backPropagation()} correctly increments wins and visits up the tree.
     */
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