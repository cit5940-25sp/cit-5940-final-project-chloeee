package othello.gamelogic;

//Encapsulation: centralizes the decision-making about which type of player to create
//Abstraction: GameController doesn't need to know the specifics of how players are created
//Maintainability: If more player types are added later, only change this factory, not every place that creates players

/**
 * A factory class responsible for creating Player instances.
 * This class encapsulates the logic for player instantiation, abstracting away
 * the specific implementation details of different player types.
 * It promotes maintainability by centralizing player creation logic.
 */
 public class PlayerFactory {

    /**
     * Creates a Player instance based on the given player type.
     *
     * @param playerType a string indicating the type of player ("human", "minimax", "mcts", etc.)
     * @return a new Player instance (either HumanPlayer or ComputerPlayer)
     */
    public static Player createPlayer(String playerType) {
        if (playerType.equals("human")) {
            return new HumanPlayer();
        } else {
            return new ComputerPlayer(playerType); // handles "minimax", "mcts", "custom", etc.
        }
    }

    /**
     * Creates a ComputerPlayer with the specified strategy.
     *
     * @param strategyName the name of the AI strategy to use (e.g., "minimax", "mcts", "custom")
     * @return a new ComputerPlayer configured with the specified strategy
     */
    public static ComputerPlayer createComputerPlayer(String strategyName) {
        return new ComputerPlayer(strategyName);
    }

}