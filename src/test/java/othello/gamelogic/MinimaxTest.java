package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MinimaxTest {
    private Player player1;
    private Player player2;
    public static final int GAME_BOARD_SIZE = 8;
    BoardSpace[][] board;
    private MinimaxStrategy minimaxStrategy;

    @Before
    public void setUp() {
        player1 = new ComputerPlayer("minimax");
        player2 = new HumanPlayer();
        player1.setColor(BoardSpace.SpaceType.BLACK);
        player2.setColor(BoardSpace.SpaceType.WHITE);
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();
        board = game.getBoard();
        minimaxStrategy = new MinimaxStrategy();

        // Set up initial board state
        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);
    }

    @Test
    public void testSelectMoveInitialState() {
        // Test initial board state
        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
        assertNotNull("Move should not be null", move);
        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
    }

    @Test
    public void testSelectMoveCornerAvailable() {
       

        // Set up board state for bottom-left corner move (7,0)
        // Need to create a line of white pieces that can be flipped
        board[6][0].setType(BoardSpace.SpaceType.WHITE);  // Adjacent to corner
        board[5][0].setType(BoardSpace.SpaceType.WHITE);  // Middle piece
        board[4][0].setType(BoardSpace.SpaceType.WHITE);  // End of line
        board[7][1].setType(BoardSpace.SpaceType.WHITE);  // Adjacent to corner
        board[7][2].setType(BoardSpace.SpaceType.WHITE);  // Middle piece
        board[7][3].setType(BoardSpace.SpaceType.WHITE);  // End of line
        
        board[3][0].setType(BoardSpace.SpaceType.BLACK); 
        board[7][4].setType(BoardSpace.SpaceType.BLACK); 
        // Print available moves to debug
        Map<BoardSpace, List<BoardSpace>> availableMoves = player1.getAvailableMoves(board);
        System.out.println("Available moves:");
        for (BoardSpace move : availableMoves.keySet()) {
            System.out.println("Move: (" + move.getX() + "," + move.getY() + ")");
        }
        
        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
        System.out.println("Selected move: (" + move.getX() + "," + move.getY() + ")");
        
        assertNotNull("Move should not be null", move);
        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
        // Should select bottom-left corner (7,0) due to highest weight
        assertEquals("Should select bottom-left corner", 7, move.getX());
        assertEquals("Should select bottom-left corner", 0, move.getY());
    }

//    @Test
//    public void testSelectMoveTopRightCorner() {
//        // Set up board state for top-right corner move (0,7)
//        board[0][6].setType(BoardSpace.SpaceType.WHITE);  // Adjacent to corner
//        board[0][5].setType(BoardSpace.SpaceType.WHITE);  // Middle piece
//        board[0][4].setType(BoardSpace.SpaceType.WHITE);  // End of line
//        board[1][7].setType(BoardSpace.SpaceType.WHITE);  // Adjacent to corner
//        board[2][7].setType(BoardSpace.SpaceType.WHITE);  // Middle piece
//        board[3][7].setType(BoardSpace.SpaceType.WHITE);  // End of line
//
//        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
//
//        assertNotNull("Move should not be null", move);
//        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
//        // Should select top-right corner (0,7) due to highest weight
//        assertEquals("Should select top-right corner", 0, move.getX());
//        assertEquals("Should select top-right corner", 7, move.getY());
//    }
//
//    @Test
//    public void testSelectMoveEdgeAvailable() {
//        // Set up board state where edge moves are available
//        board[1][0].setType(BoardSpace.SpaceType.WHITE);
//        board[2][0].setType(BoardSpace.SpaceType.WHITE);
//        board[3][0].setType(BoardSpace.SpaceType.WHITE);
//
//        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
//        assertNotNull("Move should not be null", move);
//        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
//        // Should select an edge move
//        assertTrue("Should select edge position",
//            move.getX() == 0 || move.getX() == 7 || move.getY() == 0 || move.getY() == 7);
//    }
//
//    @Test
//    public void testSelectMoveBlockOpponent() {
//        // Set up board state where opponent has a potential corner move
//        board[0][1].setType(BoardSpace.SpaceType.BLACK);
//        board[1][0].setType(BoardSpace.SpaceType.BLACK);
//        board[1][1].setType(BoardSpace.SpaceType.BLACK);
//
//        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
//        assertNotNull("Move should not be null", move);
//        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
//        // Should block opponent's corner
//        assertTrue("Should block opponent's corner",
//            (move.getX() == 0 && move.getY() == 0) ||
//            (move.getX() == 0 && move.getY() == 7) ||
//            (move.getX() == 7 && move.getY() == 0) ||
//            (move.getX() == 7 && move.getY() == 7));
//    }

    @Test
    public void testSelectMoveMultipleOptions() {
        // Set up complex board state with multiple valid moves
        board[0][1].setType(BoardSpace.SpaceType.WHITE);
        board[1][0].setType(BoardSpace.SpaceType.WHITE);
        board[1][1].setType(BoardSpace.SpaceType.WHITE);
        board[6][6].setType(BoardSpace.SpaceType.WHITE);
        board[6][7].setType(BoardSpace.SpaceType.WHITE);
        board[7][6].setType(BoardSpace.SpaceType.WHITE);
        
        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
        assertNotNull("Move should not be null", move);
        assertTrue("Move should be a valid move", player1.getAvailableMoves(board).containsKey(move));
    }

    @Test
    public void testSelectMoveNoValidMoves() {
        // Fill the board completely
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                if (board[i][j].getType() == BoardSpace.SpaceType.EMPTY) {
                    board[i][j].setType(BoardSpace.SpaceType.WHITE);
                }
            }
        }
        
        BoardSpace move = minimaxStrategy.selectMove(board, player1, player2);
        assertNull("Should return null when no moves available", move);
    }
}