package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Kalah;
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
    }

    public Side printStartMessage(final Boolean wePlayFirst) {
        final Side ourSide;
        if (wePlayFirst) {
            ourSide = Side.SOUTH;
        } else {
            ourSide = Side.NORTH;
        }
        System.err.println("||--------------GAME START-------------||");
        System.err.println("Us to go first :: " + wePlayFirst);
        System.err.println("We are :: " + ourSide);
        System.err.println("||-------------------------------------||\n");

        return ourSide;
    }
}
