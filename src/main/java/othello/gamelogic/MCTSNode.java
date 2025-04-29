package othello.gamelogic;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MCTSNode {
    private BoardSpace[][] board;
    private int wins;
    private int visits;
    private List<MCTSNode> children;
    private MCTSNode parent;
    private BoardSpace move; // The move that led to this node

    public MCTSNode(BoardSpace[][] board, MCTSNode parent, BoardSpace move) {
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.wins = 0;
        this.visits = 0;
        this.children = new ArrayList<>();
    }

    public void addChild(MCTSNode child) {
        children.add(child);
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public int getWins() {
        return wins;
    }

    public int getVisits() {
        return visits;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementVisits() {
        visits++;
    }

    public BoardSpace[][] getBoard() {
        return board;
    }

    public MCTSNode getParent() {
        return parent;
    }

    public BoardSpace getMove() {
        return move;
    }
}
