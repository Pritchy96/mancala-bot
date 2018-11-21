package org.AIandGames.mancalabot.Heutristics;

import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;


public class HaveTheyGot15 implements Heuristic {
    private Long value;
    private GameTreeNode gameTreeNode;
    private Heuristics heuristic;

    public HaveTheyGot15(GameTreeNode gameTreeNode) {
        this.gameTreeNode = gameTreeNode;
        this.heuristic = Heuristics.DO_WE_HAVE_15;
        value = 0L;
    }

    @Override
    public Heuristics getKey() {
        return this.heuristic;
    }

    @Override
    public Long call(){
        for (int i = 1; i <=7 ; i++) {
            if (this.gameTreeNode.getBoard().getSeeds(gameTreeNode.getCurrentSide().opposite(),i) == 15)
            {
                value++;
            }
        }

        return value;
    }
}
