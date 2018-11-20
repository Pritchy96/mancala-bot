package org.AIandGames.mancalabot.Heutristics;

import org.AIandGames.mancalabot.Board;

public class HaveWeGot15 implements Heuristic {
    private Board board;
    private long value;

    public HaveWeGot15(Board board) {
        this.board = board;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public void run() {
        // this.board get someting
        this.value = 1L;
    }
}
