package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;

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
    public int getValue() {
        return node.getBoard().getSeedsInStore(node.getOurSide()) - node.getBoard().getSeedsInStore(node.getOurSide().opposite());
    }
}
