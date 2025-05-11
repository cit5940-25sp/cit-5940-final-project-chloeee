package othello.gamelogic;

/**
 * Represents a computer player that will make decisions autonomously during their turns.
 * Employs a specific computer strategy passed in through program arguments.
 */
public class ComputerPlayer extends Player{
    /**
     * The strategy used by this computer player to select moves.
     */
    private Strategy strategy;
    /**
     * Constructs a {@code ComputerPlayer} with a specified strategy name.
     * The strategy name determines which AI strategy this player will use.
     *
     * @param strategyName the name of the strategy to use; should be one of
     *                     "minimax", "expectimax", "mcts", or another valid custom strategy.
     */
    public ComputerPlayer(String strategyName) {
        // PART 2
        // TODO: Use the strategyName input to create a specific strategy class for this computer
        // This input should match the ones specified in App.java!
        if (strategyName.equals("minimax")){
            this.strategy = new MinimaxStrategy();
        } else if (strategyName.equals("mcts")){
            this.strategy = new MCTSStrategy();
        } else {
            this.strategy = new CustomStrategy();
        }
    }

    /**
     * Selects a move using the associated strategy.
     *
     * @param board the current game board.
     * @param player the computer player making the move.
     * @param opponent the opposing player.
     * @return the {@code BoardSpace} chosen as the next move.
     */
    public BoardSpace chooseMove(BoardSpace[][] board, Player player, Player opponent) {
        return strategy.selectMove(board, player, opponent);
    }

}