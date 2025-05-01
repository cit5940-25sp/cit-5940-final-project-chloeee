package othello.gamelogic;

//Encapsulation: centralizes the decision-making about which type of player to create
//Abstraction: GameController doesn't need to know the specifics of how players are created
//Maintainability: If more player types are added later, only change this factory, not every place that creates players
public class PlayerFactory {
    public static Player createPlayer(String playerType) {
        if (playerType.equals("human")) {
            return new HumanPlayer();
        } else {
            return new ComputerPlayer(playerType); // handles "minimax", "mcts", "custom", etc.
        }
    }

    // Possible to add diff player types like NetworkPlayer for online play,
    public static ComputerPlayer createComputerPlayer(String strategyName) {
        return new ComputerPlayer(strategyName);
    }

}