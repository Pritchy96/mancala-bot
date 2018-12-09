package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Kalah;
import org.AIandGames.mancalabot.Move;
import org.AIandGames.mancalabot.Protocol.MoveTurn;

public class StatePrinter {

    public void printCurrentState(final Board board, final Boolean opponentWentLast, final Long ourMoveCount, final MoveTurn moveTurn) {
        System.err.println("||----------------STATE----------------||");
        if (Kalah.gameWon(board))
            System.err.println("We've already reached a terminal node!");
        if (opponentWentLast)
            System.err.println("Opponent played last with hole :: " + moveTurn.move
                    + "\nnumber of moves we have made: " + ourMoveCount);
        else
            System.err.println("We played last with hole :: " + moveTurn.move
                    + "\nnumber of moves we have made: " + ourMoveCount);
        System.err.println("The board ::\n " + board);

        System.err.println("Not our turn - continuing to make tree");
        System.err.println("||-------------------------------------||\n");
    }

    public Side printStartMessage(final Boolean wePlayFirst, final Side ourSide) {
        System.err.println("||--------------GAME START-------------||");
        System.err.println("Us to go first :: " + wePlayFirst);
        System.err.println("We are :: " + ourSide);
        System.err.println("||-------------------------------------||\n");

        return ourSide;
    }

    public void printBestGuessError() {
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("OUR BEST GUESS IS NOT LEGAL! Big Problem! - Playing right most pot");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void printPerformedSwap(final Side ourSide) {
        System.err.println("We swapped to :: " + ourSide);
        System.err.println("||-------------------------------------||\n");
    }

    public void printBestMoveGuess(final Move bestGuess) {
        System.err.println("Our best guess is :: " + bestGuess);

    }

    public void printLegalMoveMade(final Move move) {
        System.err.println("We Made this move :: " + move);
    }

    public void printEndState() {
        System.err.println("The end.");
    }
}
