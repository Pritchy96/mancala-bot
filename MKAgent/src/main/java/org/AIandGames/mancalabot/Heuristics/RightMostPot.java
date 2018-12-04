package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Side;

@EqualsAndHashCode
@ToString
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
    public int getValue(Side ourSide) {
        return node.getHoleNumber();
    }
}
