package model;

import java.io.*;
import java.util.Random;

/** Represents a Tetris Model for Tetris.  
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisModel implements Serializable {

    public static final int WIDTH = 10; //size of the board in blocks
    public static final int HEIGHT = 20; //height of the board in blocks
    public static final int BUFFERZONE = 4; //space at the top

    protected TetrisBoard board;  // Board data structure
    protected TetrisPiece[] pieces; // Pieces to be places on the board
    protected TetrisPiece currentPiece; //Piece we are currently placing
    protected TetrisPiece newPiece; //next piece to be placed
    protected int count;		 // how many pieces played so far
    protected int score; //the player's score

    protected int currentX, newX;
    protected int currentY, newY;

    // State of the game
    protected boolean gameOn;	// true if we are playing
    protected Random random;	 // the random generator for new pieces

    private boolean autoPilotMode; //are we in autopilot mode?
    protected TetrisPilot pilot;

    public enum MoveType {
        ROTATE,
        LEFT,
        RIGHT,
        DROP,
        DOWN
    }

    /**
     * Constructor for a tetris model
     */
    public TetrisModel() {
        board = new TetrisBoard(WIDTH, HEIGHT + BUFFERZONE);
        pieces = TetrisPiece.getPieces(); //initialize board and pieces
        autoPilotMode = false;
        gameOn = false;
        pilot = new AutoPilot();
    }


    /**
     * Start new game
     */
    public void startGame() { //start game
        random = new Random();
        addNewPiece();
        gameOn = true;
        score = 0;
        count = 0;
    }

    /**
     * Board getter
     *
     * @return  board
     */
    public TetrisBoard getBoard() {
        return this.board;
    }

    /**
     * Compute New Position of piece in play based on move type
     * 
     * @param verb type of move to account for
     */
    public void computeNewPosition(MoveType verb) {

        // As a starting point, the new position is the same as the old
        newPiece = currentPiece;
        newX = currentX;
        newY = currentY;

        // Make changes based on the verb
        switch (verb) {
            case LEFT: newX--; break; //move left

            case RIGHT: newX++; break; //move right

            case ROTATE: //rotate
                newPiece = newPiece.fastRotation();
                newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
                newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
                break;

            case DOWN: //down
                newY--;
                break;

            case DROP: //drop
                newY = board.placementHeight(newPiece, newX);
                if (newY > currentY) { //piece can't move up!
                    newY = currentY;
                }
                break;

            default: //doh!
                throw new RuntimeException("Bad movement!");
        }

    }

    /**
     * Put new piece in play on board 
     */
    public void addNewPiece() {
        count++;
        score++;

        // commit things the way they are
        board.commit();
        currentPiece = null;

        TetrisPiece piece = pickNextPiece();

        // Center it up at the top
        int px = (board.getWidth() - piece.getWidth())/2;
        int py = board.getHeight() - piece.getHeight();

        int result = setCurrent(piece, px, py);

        if (result > TetrisBoard.ADD_ROW_FILLED) {
            stopGame(); //oops, we lost.
        }

    }

    /**
     * Pick next piece to put in play on board 
     */
    private TetrisPiece pickNextPiece() {
        int pieceNum;
        pieceNum = (int) (pieces.length * random.nextDouble());
        TetrisPiece piece	 = pieces[pieceNum];
        return(piece);
    }

    /**
     * Attempt to set the piece at a given board position
     * 
     * @param piece piece to place
     * @param x placement position, x
     * @param y placement position, y
     * 
     * @return integer defining if placement is OK or not (see Board.java)
     */
    public int setCurrent(TetrisPiece piece, int x, int y) {
        int result = board.placePiece(piece, x, y);

        if (result <= TetrisBoard.ADD_ROW_FILLED) { // SUCCESS
            this.currentPiece = piece;
            this.currentX = x;
            this.currentY = y;
        } else {
            board.undo();
        }

        return(result);
    }

    /**
     * pause game
     */
    public void stopGame() {
        gameOn = false;
    }

    /**
     * Get width
     * 
     * @return width 
     */
    public double getWidth() {
        return WIDTH;
    }

    /**
     * Get width
     * 
     * @return height (with buffer at top accounted for) 
     */
    public double getHeight() {
        return HEIGHT + BUFFERZONE;
    }

    /**
     * Get width
     * 
     * @return score of game
     */
    public int getScore() {
        return score;
    }

    /**
     * Get width
     * 
     * @return number of pieces placed
     */
    public int getCount() {
        return count;
    }

    /**
     * Set autopilot mode to true.
     */
    public void setAutoPilotMode() {
        autoPilotMode = true;
    }

    /**
     * Set autopilot mode to false.
     */
    public void setHumanPilotMode() {
        autoPilotMode = false;
    }

    /**
     * Advance the game one tick forward
     * Each tick is associated with a move of some kind!
     * Put the move in play by executing this.
     */
    public void modelTick(MoveType verb) {

        if (!gameOn) return;

        executeMove(verb);

        if (autoPilotMode && gameOn) { //if it's an automated game, get an automated move.
            computerMove();
        }
    }

    /**
     * Get the best move that is automatically generated by a computer
     * Then execute it.
     */
    private void computerMove() {
        MoveType verb = pilot.bestMove(board,currentPiece,currentX,currentY); //which move is best?
        executeMove(verb);
    }

    /**
     * Execute a given move.  This will compute the new position of the active piece, 
     * set the piece to this location if possible.  If lines are completed
     * as a result of the move, the lines will be cleared from the board,
     * and the board will be updated.  Scores will be added to the player's
     * total based on the number of rows cleared.
     * 
     * @param verb the type of move to execute
     */
    private void executeMove(MoveType verb) {

        if (currentPiece != null) {
            board.undo();	// remove the piece from its old position
        }

        computeNewPosition(verb);

        // try out the new position (and roll it back if it doesn't work)
        int result = setCurrent(newPiece, newX, newY);

        boolean failed = (result >= TetrisBoard.ADD_OUT_BOUNDS);

        // if it didn't work, put it back the way it was
        if (failed) {
            if (currentPiece != null) board.placePiece(currentPiece, currentX, currentY);
        }

        if (failed && verb==MoveType.DOWN){	// if it's out of bounds due to falling
            int cleared = board.clearRows();
            if (cleared > 0) {
                // scores go up by 5, 10, 20, 40 as more rows are cleared
                switch (cleared) {
                    case 1: score += 5;	 break;
                    case 2: score += 10;  break;
                    case 3: score += 20;  break;
                    case 4: score += 40;  break;
                    default: score += 50;
                }
            }

            // if the board is too tall, we've lost!
            if (board.getMaxHeight() > board.getHeight() - BUFFERZONE) {
                stopGame();
            }

            // Otherwise, add a new piece and keep playing
            else {
                addNewPiece();
            }
        }

    }

    /**
     * Start a new game
     */
    public void newGame() {
        this.board.newGame();
        startGame();
    }

    /**
     * Save the current state of the game to a file
     * 
     * @param file pointer to file to write to
     */
    public void saveModel(File file) {
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for autopilot state
     */
    public boolean getAutoPilotMode() {
        return this.autoPilotMode;
    }
}



