package othello.gamelogic;

import org.junit.Before;
import org.junit.Test;

public class MinimaxTest {
    private Player player1 ;
    private Player player2;
    public static final int GAME_BOARD_SIZE = 8;
    BoardSpace[][] board;

    @Before
    public void setUp() {
        player1 = new ComputerPlayer("minimax");
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

}
