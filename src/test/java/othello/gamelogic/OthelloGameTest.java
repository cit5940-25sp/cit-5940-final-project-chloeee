package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OthelloGameTest {
    private Player player1 ;
    private Player player2;
    public static final int GAME_BOARD_SIZE = 8;
    BoardSpace[][] board;

    @Before
    public void setUp() {
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();
        player1.setColor(BoardSpace.SpaceType.BLACK);
        player2.setColor(BoardSpace.SpaceType.WHITE);
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();
        board = game.getBoard();


        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);
    }

    @Test
    public void testAvailableSpace() {
        List<BoardSpace> expectedOrigin = new ArrayList<>();
        expectedOrigin.add(board[4][3]);
        Map<BoardSpace, List<BoardSpace>> expected = new HashMap<>();
        expected.put(board[2][3], expectedOrigin);

        List<BoardSpace> expectedOrigin2 = new ArrayList<>();
        expectedOrigin2.add(board[3][4]);
        expected.put(board[3][2], expectedOrigin2);

        List<BoardSpace> expectedOrigin3 = new ArrayList<>();
        expectedOrigin3.add(board[4][3]);
        expected.put(board[4][5], expectedOrigin3);

        List<BoardSpace> expectedOrigin4 = new ArrayList<>();
        expectedOrigin4.add(board[3][4]);
        expected.put(board[5][4], expectedOrigin4);
    }

}