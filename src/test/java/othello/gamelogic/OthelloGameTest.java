package othello.gamelogic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
/**
 * Unit tests for the {@link OthelloGame} class.
 * This test suite verifies the correctness of core game mechanics including board setup,
 * move validation, move execution, flipping logic, computer decisions, and end game detection.
 */

public class OthelloGameTest {
    private Player player1 ;
    private Player player2;
    public static final int GAME_BOARD_SIZE = 8;
    BoardSpace[][] board;

    /**
     * Initializes a fresh game board and sets the initial 4 central pieces before each test.
     */
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

    /**
     * Tests whether {@link OthelloGame#initBoard()} correctly initializes the board with 4 center discs
     * and the rest as empty spaces.
     */
    @Test
    public void testInitBoard() {
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();
        BoardSpace[][] board = game.getBoard();

        // Check board dimensions
        assertEquals(GAME_BOARD_SIZE, board.length);
        assertEquals(GAME_BOARD_SIZE, board[0].length);

        // Check initial setup (center pieces)
        assertEquals(BoardSpace.SpaceType.WHITE, board[3][3].getType());
        assertEquals(BoardSpace.SpaceType.BLACK, board[3][4].getType());
        assertEquals(BoardSpace.SpaceType.BLACK, board[4][3].getType());
        assertEquals(BoardSpace.SpaceType.WHITE, board[4][4].getType());

        // Check all other spaces are empty
        int emptyCount = 0;
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                if ((i == 3 && j == 3) || (i == 4 && j == 4) || (i == 3 && j == 4) || (i == 4 && j == 3))
                    continue;
                assertEquals(BoardSpace.SpaceType.EMPTY, board[i][j].getType());
                emptyCount++;
            }
        }
        assertEquals(60, emptyCount); // 8x8 - 4 initial pieces
    }

    /**
     * Tests whether {@link Player#getAvailableMoves(BoardSpace[][])} returns the correct destinations and origins
     * for the default board setup.
     */
    @Test
    public void testAvailableSpace() {

        // These blocks define the 4 valid moves BLACK would have at the start of the game
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

        // Get actual results from the method being tested
        Map<BoardSpace, List<BoardSpace>> actualMoves = player1.getAvailableMoves(board);

        // Verify the number of available moves matches
        assertEquals(expected.size(), actualMoves.size());

        // Verify each expected move is in the actual moves
        for (BoardSpace destination : expected.keySet()) {
            assertTrue("Expected move at (" + destination.getX() + "," + destination.getY() + ") should be available",
                    hasMatchingDestination(actualMoves, destination));

            // Get the matching destination from actual moves
            BoardSpace actualDestination = getMatchingDestination(actualMoves, destination);
            assertNotNull("Should find matching destination", actualDestination);

            // Verify origins for this destination
            List<BoardSpace> expectedOrigins = expected.get(destination);
            List<BoardSpace> actualOrigins = actualMoves.get(actualDestination);

            // Verify number of origins
            assertEquals(expectedOrigins.size(), actualOrigins.size(),
                    "Number of origins for destination (" + destination.getX() + "," +
                            destination.getY() + ") should match");
            // Verify each origin
            for (BoardSpace expectedOriginSpace : expectedOrigins) {
                assertTrue("Expected origin at (" + expectedOriginSpace.getX() + "," +
                                expectedOriginSpace.getY() + ") should be in actual origins",
                        hasMatchingOrigin(actualOrigins, expectedOriginSpace));
            }
        }
    }

    // Helper method to find a matching destination by coordinates
    private boolean hasMatchingDestination(Map<BoardSpace, List<BoardSpace>> moves, BoardSpace target) {
        for (BoardSpace space : moves.keySet()) {
            if (space.getX() == target.getX() && space.getY() == target.getY()) {
                return true;
            }
        }
        return false;
    }

    // Helper method to get a matching destination by coordinates
    private BoardSpace getMatchingDestination(Map<BoardSpace, List<BoardSpace>> moves, BoardSpace target) {
        for (BoardSpace space : moves.keySet()) {
            if (space.getX() == target.getX() && space.getY() == target.getY()) {
                return space;
            }
        }
        return null;
    }

    // Helper method to find a matching origin by coordinates
    private boolean hasMatchingOrigin(List<BoardSpace> origins, BoardSpace target) {
        for (BoardSpace origin : origins) {
            if (origin.getX() == target.getX() && origin.getY() == target.getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests the {@link OthelloGame#takeSpace(Player, Player, int, int)} method for valid, invalid, and edge-case coordinates.
     */
    @Test
    public void testTakeSpace() {
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();
        BoardSpace[][] board = game.getBoard();

        // Test taking an empty space
        game.takeSpace(player1, player2, 2, 3);
        assertEquals(BoardSpace.SpaceType.BLACK, board[2][3].getType());

        // Test taking opponent's space
        game.takeSpace(player1, player2, 3, 3); // Originally WHITE
        assertEquals(BoardSpace.SpaceType.BLACK, board[3][3].getType());

        // Test taking own space (should not change)
        game.takeSpace(player1, player2, 2, 3); // Now BLACK from previous test
        assertEquals(BoardSpace.SpaceType.BLACK, board[2][3].getType());

        // Test invalid coordinates
        game.takeSpace(player1, player2, -1, 3);
        game.takeSpace(player1, player2, 8, 3);
        game.takeSpace(player1, player2, 3, -1);
        game.takeSpace(player1, player2, 3, 8);
        // Board should remain unchanged for invalid coordinates
    }
    /**
     * Tests that {@link OthelloGame#takeSpaces(Player, Player, Map, BoardSpace)} flips opponent discs appropriately.
     */
    @Test
    public void testTakeSpaces() {
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();
        BoardSpace[][] board = game.getBoard();

        // Setup specific board state
        board[3][2].setType(BoardSpace.SpaceType.WHITE);
        board[4][2].setType(BoardSpace.SpaceType.BLACK);

        // Create map for takeSpaces
        Map<BoardSpace, List<BoardSpace>> availableMoves = player1.getAvailableMoves(board);
        BoardSpace selectedMove = null;

        // Find the move at position (2,2) if available
        for (BoardSpace move : availableMoves.keySet()) {
            if (move.getX() == 2 && move.getY() == 2) {
                selectedMove = move;
                break;
            }
        }

        if (selectedMove != null) {
            game.takeSpaces(player1, player2, availableMoves, selectedMove);

            // Verify that the selected position is now BLACK
            assertEquals(BoardSpace.SpaceType.BLACK, board[2][2].getType());

            // Verify that pieces were flipped in the right direction
            assertEquals(BoardSpace.SpaceType.BLACK, board[3][2].getType());
        } else {
            fail("Move at (2,2) not found in available moves");
        }
    }
    /**
     * Tests the correctness of {@link OthelloGame#getAvailableMoves(Player)} on multiple board states.
     */
    @Test
    public void testGetAvailableMovesComprehensive() {
        // Test with different board configurations
        OthelloGame game = new OthelloGame(player1, player2);
        game.initBoard();

        // Initial board state should have 4 moves for BLACK
        Map<BoardSpace, List<BoardSpace>> availableMoves = game.getAvailableMoves(player1);
        assertEquals(4, availableMoves.size());

        // Create a board with no moves for BLACK
        BoardSpace[][] board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.WHITE);
            }
        }
        game.setBoard(board);
        availableMoves = game.getAvailableMoves(player1);
        assertEquals(0, availableMoves.size());

        // Create a board with specific moves available
        game.initBoard();
        board = game.getBoard();
        // Set up a specific pattern to test directional capture
        board[2][2].setType(BoardSpace.SpaceType.WHITE);
        board[2][3].setType(BoardSpace.SpaceType.WHITE);
        board[2][4].setType(BoardSpace.SpaceType.BLACK);

        availableMoves = game.getAvailableMoves(player1);
        boolean moveFound = false;
        for (BoardSpace move : availableMoves.keySet()) {
            if (move.getX() == 2 && move.getY() == 1) {
                moveFound = true;
                break;
            }
        }
        assertTrue("Should find a move at (2,1)", moveFound);
    }
    /**
     * Tests that the {@link OthelloGame#computerDecision(ComputerPlayer)} method returns a valid move.
     */
    @Test
    public void testComputerDecision() {
        ComputerPlayer computerPlayer = new ComputerPlayer("minimax");
        computerPlayer.setColor(BoardSpace.SpaceType.BLACK);
        HumanPlayer humanPlayer = new HumanPlayer();
        humanPlayer.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame game = new OthelloGame(computerPlayer, humanPlayer);
        game.initBoard();

        BoardSpace move = game.computerDecision(computerPlayer);
        assertNotNull("Computer should choose a move", move);

        // Verify the move is in available moves
        Map<BoardSpace, List<BoardSpace>> availableMoves = game.getAvailableMoves(computerPlayer);
        boolean validMove = false;
        for (BoardSpace availableMove : availableMoves.keySet()) {
            if (availableMove.getX() == move.getX() && availableMove.getY() == move.getY()) {
                validMove = true;
                break;
            }
        }
        assertTrue("Move should be valid", validMove);
    }
    /**
     * Tests the end-game condition and scoring logic by simulating a full board.
     */
    @Test
    public void testEndGameCondition() {
        OthelloGame game = new OthelloGame(player1, player2);

        // Create a full board
        BoardSpace[][] board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.BLACK);
                } else {
                    board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.WHITE);
                }
            }
        }
        game.setBoard(board);

        // Both players should have no moves
        Map<BoardSpace, List<BoardSpace>> moves1 = game.getAvailableMoves(player1);
        Map<BoardSpace, List<BoardSpace>> moves2 = game.getAvailableMoves(player2);

        assertEquals(0, moves1.size());
        assertEquals(0, moves2.size());

        // Count the pieces to determine winner
        int blackCount = 0;
        int whiteCount = 0;
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                if (board[i][j].getType() == BoardSpace.SpaceType.BLACK) {
                    blackCount++;
                } else if (board[i][j].getType() == BoardSpace.SpaceType.WHITE) {
                    whiteCount++;
                }
            }
        }
        assertEquals(32, blackCount);
        assertEquals(32, whiteCount);
    }
}