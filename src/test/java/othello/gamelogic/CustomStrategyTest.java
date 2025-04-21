package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomStrategyTest {
    private Player player1a;
    private Player player1b;
    private Player player2;
    public static final int GAME_BOARD_SIZE = 8;
    BoardSpace[][] boardA;
    BoardSpace[][] boardB;
    private MinimaxStrategy minimaxStrategy;
    private CustomStrategy customStrategy;

    @Before
    public void setUp() {
        player1a = new ComputerPlayer("custom");
        player1b = new ComputerPlayer("minimax");
        player2 = new HumanPlayer();

        player1a.setColor(BoardSpace.SpaceType.BLACK);
        player1b.setColor(BoardSpace.SpaceType.BLACK);
        player2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame gameA = new OthelloGame(player1a, player2);
        OthelloGame gameB = new OthelloGame(player1b, player2);
        gameA.initBoard();
        gameB.initBoard();
        boardA = gameA.getBoard();
        boardB = gameA.getBoard();
        minimaxStrategy = new MinimaxStrategy();
        customStrategy = new CustomStrategy();

        // Set up initial board state
        boardA[3][3].setType(BoardSpace.SpaceType.WHITE);
        boardA[3][4].setType(BoardSpace.SpaceType.BLACK);
        boardA[4][3].setType(BoardSpace.SpaceType.BLACK);
        boardA[4][4].setType(BoardSpace.SpaceType.WHITE);

        boardB[3][3].setType(BoardSpace.SpaceType.WHITE);
        boardB[3][4].setType(BoardSpace.SpaceType.BLACK);
        boardB[4][3].setType(BoardSpace.SpaceType.BLACK);
        boardB[4][4].setType(BoardSpace.SpaceType.WHITE);
    }

    @Test
    public void testSelectMoveInitialState() {
        // Test initial board state
        BoardSpace moveA = customStrategy.selectMove(boardA, player1a, player2);
        BoardSpace moveB = minimaxStrategy.selectMove(boardB, player1b, player2);
        assertNotNull("Move should not be null", moveA);
        assertEquals(player1a.getAvailableMoves(boardA).containsKey(moveA),
                player1b.getAvailableMoves(boardB).containsKey(moveB));
    }

    @Test
    public void testNodesEvaluated() {
        BoardSpace moveA = customStrategy.selectMove(boardA, player1a, player2);
        BoardSpace moveB = minimaxStrategy.selectMove(boardB, player1b, player2);
        System.out.println(customStrategy.getNodesEvaluated());
        System.out.println(minimaxStrategy.getNodesEvaluated());
        assertTrue(customStrategy.getNodesEvaluated() < minimaxStrategy.getNodesEvaluated());
    }
}