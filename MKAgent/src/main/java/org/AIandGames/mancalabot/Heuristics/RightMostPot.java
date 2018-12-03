package org.AIandGames.mancalabot.Heuristics;

import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;

public class RightMostPot implements Heuristic {

    private GameTreeNode node;

    public RightMostPot(GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.RIGHT_MOST_POT;
    }

    @Override
    public int getValue() {
        return node.getHoleNumber();
    }
}
