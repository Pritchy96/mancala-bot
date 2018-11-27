package org.AIandGames.mancalabot.Heuristics;

import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;

public class MKPointDifference implements Heuristic {
    private GameTreeNode node;

    public MKPointDifference(GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.MK_POINT_DIFFERENCE;
    }

    @Override
    public int getValue() {
        return node.getBoard().getSeedsInStore(node.getCurrentSide()) - node.getBoard().getSeedsInStore(node.getCurrentSide().opposite());
    }
}
