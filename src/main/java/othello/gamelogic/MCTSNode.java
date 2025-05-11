package othello.gamelogic;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a node in the Monte Carlo Tree Search (MCTS) algorithm.
 * Each node holds a snapshot of the board state, its parent and children,
 * the move that led to this state, and statistics for backpropagation.
 */

public class MCTSNode {
    /** The board state associated with this node. */
    private BoardSpace[][] board;
    /** Number of simulation wins for this node. */
    private int wins;
    /** Number of times this node has been visited. */
    private int visits;
    /** List of child nodes representing possible future game states. */
    private List<MCTSNode> children;
    /** The parent node of this node. */
    private MCTSNode parent;
    /** The move that was taken to arrive at this node from its parent. */
    private BoardSpace move; // The move that led to this node

    /**
     * Constructs a new MCTSNode with the given board state, parent, and move.
     *
     * @param board the board state at this node
     * @param parent the parent node (null if this is the root)
     * @param move the move that led to this node (null if this is the root)
     */
    public MCTSNode(BoardSpace[][] board, MCTSNode parent, BoardSpace move) {
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.wins = 0;
        this.visits = 0;
        this.children = new ArrayList<>();
    }
    /**
     * Adds a child node to this node.
     *
     * @param child the child node to add
     */

    public void addChild(MCTSNode child) {
        children.add(child);
    }
    /**
     * Returns the list of child nodes.
     *
     * @return a list of children of this node
     */

    public List<MCTSNode> getChildren() {
        return children;
    }
    /**
     * Returns the number of wins at this node.
     *
     * @return number of wins
     */

    public int getWins() {
        return wins;
    }
    /**
     * Returns the number of times this node has been visited.
     *
     * @return number of visits
     */
    public int getVisits() {
        return visits;
    }
    /**
     * Increments the number of wins by 1.
     */

    public void incrementWins() {
        wins++;
    }
    /**
     * Increments the number of visits by 1.
     */
    public void incrementVisits() {
        visits++;
    }
    /**
     * Returns the board state at this node.
     *
     * @return a 2D array representing the board
     */
    public BoardSpace[][] getBoard() {
        return board;
    }
    /**
     * Returns the parent of this node.
     *
     * @return the parent MCTSNode
     */
    public MCTSNode getParent() {
        return parent;
    }
    /**
     * Returns the move that led to this node from its parent.
     *
     * @return the BoardSpace representing the move
     */
    public BoardSpace getMove() {
        return move;
    }
}
