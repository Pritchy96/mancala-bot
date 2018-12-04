package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Enums.Side;

@EqualsAndHashCode
@ToString
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
    public int getValue(Side ourSide) {
        return node.getBoard().getSeedsInStore(ourSide) - node.getBoard().getSeedsInStore(ourSide.opposite());
    }
}
