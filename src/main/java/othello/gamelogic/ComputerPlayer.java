package othello.gamelogic;

import java.util.List;
import java.util.Map;

/**
 * Represents a computer player that will make decisions autonomously during their turns.
 * Employs a specific computer strategy passed in through program arguments.
 */
public class ComputerPlayer extends Player{
    // Check with Nick -> create interface called Strategy through which 3 strategies implemented?
    // Check with Nick -> add this field to store the strategy per instance of computer Player?
    private Strategy strategy;

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
    // Check with Nick -> How to do?
}