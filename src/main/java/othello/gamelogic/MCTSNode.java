package othello.gamelogic;

import java.util.List;

public class MCTSNode {
    //wins, visits, coords
    private int wins;
    private int visits;
    private BoardSpace move;
    private MCTSNode parent;
    private List<MCTSNode> children;

    public MCTSNode(BoardSpace[][] board, ) {

    }
}
