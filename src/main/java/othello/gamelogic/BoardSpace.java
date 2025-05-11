package othello.gamelogic;

import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Represents a logical space on the Othello Board.
 * Keeps track of coordinates and the type of the current space.
 */
public class BoardSpace {

    private final int x;
    private final int y;
    private SpaceType type;

    /**
     * Constructs a new BoardSpace with given coordinates and space type.
     *
     * @param x    the x-coordinate on the board
     * @param y    the y-coordinate on the board
     * @param type the initial type (EMPTY, BLACK, or WHITE)
     */
    public BoardSpace(int x, int y, SpaceType type) {
        this.x = x;
        this.y = y;
        setType(type);
    }

    /**
     * Copy constructor for BoardSpace.
     *
     * @param other the BoardSpace to copy
     */
    public BoardSpace(BoardSpace other) {
        this.x = other.x;
        this.y = other.y;
        this.type = other.type;
    }

    /**
     * Returns the x-coordinate of this space.
     *
     * @return the x value
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this space.
     *
     * @return the y value
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the type of this space.
     *
     * @return the space type (EMPTY, BLACK, or WHITE)
     */
    public SpaceType getType() {
        return type;
    }

    /**
     * Sets the type (piece color or empty) for this space.
     *
     * @param type the new type to assign
     */
    public void setType(SpaceType type) {
        this.type = type;
    }

    /**
     * Enum representing the type of a BoardSpace.
     * Includes the associated color for display in the GUI.
     */
    public enum SpaceType {
        EMPTY(Color.GRAY),
        BLACK(Color.web("#393E46")),
        WHITE(Color.web("#FAF6E9"));

        private final Color fill;
        /**
         * Constructor for SpaceType.
         *
         * @param fill the Color associated with the type
         */
        SpaceType(Color fill) {
            this.fill = fill;
        }
        /**
         * Constructor for SpaceType.
         *
         * @param fill the Color associated with the type
         */
        public Color fill() {
            return fill;
        }
    }

    /**
     * Returns a string representation of the BoardSpace, including coordinates and type.
     *
     * @return a string describing the space
     */
    @Override
    public String toString() {
        return "BoardSpace{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}