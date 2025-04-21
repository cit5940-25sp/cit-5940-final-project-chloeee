package othello.gamelogic;
public class MCTSStrategy implements Strategy {
    // TO DO
    @Override
    public BoardSpace selectMove(BoardSpace[][] board, Player player, Player opponent) {
        //create root node
        MCTSNode root = new MCTSNode(board, player, opponent, null, null);


        //



        return null;
    }
}
