package org.AIandGames.mancalabot.Heutristics;

import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Side;

public class test implements Heuristic {
    private Board board;
    private long value;


    public test(Board board) {
        this.board = board;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public void run() {
        final int seedsInNorthPot1 = this.board.getSeeds(Side.NORTH, 1);
        this.value = (long)seedsInNorthPot1;

    }
}
