package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@EqualsAndHashCode
@ToString
public class MKPointDifference implements Heuristic {
    private final GameTreeNode node;


    public MKPointDifference(final GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.MK_POINT_DIFFERENCE;
    }

    @Override
    public int getValue(final Side ourSide) {
        return this.node.getBoard().getSeedsInStore(ourSide) - this.node.getBoard().getSeedsInStore(ourSide.opposite());
    }
}
