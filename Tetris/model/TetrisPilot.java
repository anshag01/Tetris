package model;

public interface TetrisPilot {

    /**
     * Given a piece and a board, return a move object that represents
     * the best play for that piece, or returns null if no play is possible.
     * The board should be in the committed state when this is called.
     *
     * @param board       The current board configuration
     * @param limitHeight The height of the lower part of the board that piece
     * @param currentY
     * @return The best move, based on search for good moves
     */
    public TetrisModel.MoveType bestMove(TetrisBoard board, TetrisPiece piece, int limitHeight, int currentY);
}