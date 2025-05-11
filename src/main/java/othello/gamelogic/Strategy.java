package othello.gamelogic;

/**
 * The {@code Strategy} interface defines the contract for implementing
 * different move selection strategies in an Othello game.
 *
 * <p>Implementations of this interface can define how a player chooses a move
 * based on the current state of the board and the players involved.
 */
public interface Strategy {

    /**
     * Selects the next move for the given player based on the current game board state.
     *
     * @param board    A 2D array of {@link BoardSpace} representing the current state of the game board.
     * @param player   The current player for whom the move is being selected.
     * @param opponent The opposing player.
     * @return A {@link BoardSpace} representing the selected move, or {@code null} if no valid moves are available.
     */
    BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent);
}