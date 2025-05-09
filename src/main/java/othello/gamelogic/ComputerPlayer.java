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
     * Constructs a ComputerPlayer with a specified strategy name.
     * The strategy name determines which AI strategy the player will use.
     *
     * @param strategyName the name of the strategy ("minimax", "expectimax", "mcts", or others for custom)
     */
    public ComputerPlayer(String strategyName) {
        // PART 2
        // TODO: Use the strategyName input to create a specific strategy class for this computer
        // This input should match the ones specified in App.java!
        if (strategyName.equals("minimax")){
            this.strategy = new MinimaxStrategy();
        } else if (strategyName.equals("expectimax")){
            this.strategy = new ExpectimaxStrategy();
        } else if (strategyName.equals("mcts")){
            this.strategy = new MCTSStrategy();
        } else {
            this.strategy = new CustomStrategy();
        }
    }

    // PART 2
    // TODO: implement a method that returns a BoardSpace that a strategy selects
    // We already have the strategies implemented, so simply call it
    public BoardSpace chooseMove(BoardSpace[][] board, Player player, Player opponent) {
        return strategy.selectMove(board, player, opponent);
    }

}