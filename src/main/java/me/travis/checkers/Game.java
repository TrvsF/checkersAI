package me.travis.checkers;

import me.travis.checkers.board.Board;
import me.travis.checkers.logic.Misc;
import me.travis.checkers.logic.Moves;
import me.travis.checkers.util.Tuple;

import java.util.List;

public class Game {

    private int turn;

    private int[] selectedMan;

    private final boolean whiteAI;
    private final boolean blackAI;
    private boolean gameOver;

    /**
     * sets up the start of the game
     * the turn is set to black
     * if mode is 0 it is player vs player
     * if mode is 1 it is player vs ai
     * if mode is 2 it is ai vs ai
     * @param mode what mode the game is going to be played in
     */
    public Game(int mode) {
        this.turn = 1;
        this.selectedMan = null;

        this.whiteAI = (mode == 2);
        this.blackAI = (mode == 1 || mode == 2);
        this.gameOver = false;

        Board.resetBoard();
    }

    /**
     * @return if the game is over
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * @return who's turn it is
     */
    public int getTurnID() {
        return this.turn;
    }

    /**
     * handles when a piece is clicked on the board
     * @param x of what piece has been clicked
     * @param y of what piece has been clicked
     * @param team of what piece has been clicked
     */
    public void handleClick(int x, int y, int team) {
        // clears the currently drawn highlights
        Checkers.getWindow().clearHighlights(true);

        // get the coords of the board (flipped bc nested arrays are backward)
        int[] relative = Misc.guiToBoard(x, y);

        // if a highlight is selected
        if (team >= 9 && this.selectedMan != null) {

            // move the pieces
            Moves.movePieces(relative[1], relative[0], selectedMan[0], selectedMan[1], team == 10);

            // refresh the window
            Checkers.getWindow().refresh();

            // move to the next turn
            this.nextTurn();

            return;
        }

        // if it isnt the turn of the team clicked
        if (team != this.turn ) {
            return;
        }

        List<Tuple<Integer, Integer, Boolean>> moves = Moves.getMoves(relative[1], relative[0]);

        // if there are no moves we don't care about that piece
        if (moves.isEmpty()) {
            return;
        }

        // if there is, store the given position so we can move it later
        this.selectedMan = new int[]{relative[1], relative[0]};

        boolean shouldRenderNonDeadly = true;

        // for each new place to highlight
        for (Tuple<Integer, Integer, Boolean> tuple : moves) {
            // update the board to display the new highlights
            if (tuple.getElement3()) {
                Board.BOARD[tuple.getElement1()][tuple.getElement2()].makeDeadlyHighlight();
                // clears of the normal highlights as you HAVE to jump if given the option
                Checkers.getWindow().clearHighlights(false);
                shouldRenderNonDeadly = false;
            } else if (shouldRenderNonDeadly) {
                Board.BOARD[tuple.getElement1()][tuple.getElement2()].makeHighlight();
            }
        }

        // refresh the GUI
        Checkers.getWindow().refresh();

        Board.printDebugBoard();
    }

    /**
     * changes who's turn it is
     */
    private void nextTurn() {

        this.boardCheck();

        if (this.gameOver) {
            System.out.println("GAME OVER");
        }

        this.turn *= -1;

        if (this.turn == 1 && whiteAI) {
            // do white AI turn
        }

        if (this.turn == -1 && blackAI) {
            // do black AI turn
        }
    }

    /**
     * ensures the board is ready for the next turn
     */
    private void boardCheck() {
        Board.checkKings();
        Board.clearAllHighlights();
        gameOver = Board.shouldGameFinish();
    }

    /**
     * clears the selected move
     */
    private void clearSelection() {
        this.selectedMan = null;
    }

}
