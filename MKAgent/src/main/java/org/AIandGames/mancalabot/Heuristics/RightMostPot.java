package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@EqualsAndHashCode
@ToString
public class RightMostPot implements Heuristic {

    private final GameTreeNode node;

    public RightMostPot(final GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.RIGHT_MOST_POT;
    }

    @Override
    public int getValue(final Side ourSide) {
        return this.node.getHoleNumber();
    }
}
