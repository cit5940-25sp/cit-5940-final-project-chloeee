package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import othello.Constants;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomStrategyTest {
    private Player player1a;
    private Player player1b;
    private Player player2;
    BoardSpace[][] boardA;
    BoardSpace[][] boardB;
    private MinimaxStrategy minimaxStrategy;
    private CustomStrategy customStrategy;

    public static final int GAME_BOARD_SIZE = 8;


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

    @Test
    public void testSelectMove() {//test the selected move is within the available movs
        //1. set upp a new board
        BoardSpace[][] board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
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


        //.TEST the selected move is within the available moves
        Map<BoardSpace, List<BoardSpace>> availableMoves = player1a.getAvailableMoves(board);
        BoardSpace move = customStrategy.selectMove(board, player1a, player2);
//        System.out.println("available moves: " + availableMoves.keySet());
//        System.out.println("selected move: " + move);
        assertTrue(availableMoves.containsKey(move));

    }
    @Test
    public void testCornerSituation() {
        //1. set upp a new board
        BoardSpace[][] board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }



        board[1][0].setType(BoardSpace.SpaceType.WHITE);
        board[0][1].setType(BoardSpace.SpaceType.WHITE);
        board[1][1].setType(BoardSpace.SpaceType.WHITE);
        board[2][0].setType(BoardSpace.SpaceType.BLACK); // anchor for (1,0)
        board[0][2].setType(BoardSpace.SpaceType.BLACK); // anchor for (0,1)
        board[2][2].setType(BoardSpace.SpaceType.BLACK); // anchor for (1,1)




//        System.out.println("Available moves: " + player1a.getAvailableMoves(board));

        BoardSpace move = customStrategy.selectMove(board, player1a, player2);

        assertEquals(0, move.getX());
        assertEquals(0, move.getY());

    }


    @Test
    public void testMinimax() {
        //1. set upp a new board
        BoardSpace[][] board = new BoardSpace[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }


        //2.only allow one legal move
        board[0][0].setType(BoardSpace.SpaceType.BLACK);
        board[0][1].setType(BoardSpace.SpaceType.WHITE);





      //4.call minimax with depth of 1 and black's turn
        int score = customStrategy.minmaximizer(board, player1a, player2, 1, true, Integer.MAX_VALUE, Integer.MIN_VALUE);

        assertEquals(160, score);



    }




}