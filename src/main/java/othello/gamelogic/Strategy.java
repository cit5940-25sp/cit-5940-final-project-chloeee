package othello.gamelogic;

public interface Strategy {
    BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent);


}