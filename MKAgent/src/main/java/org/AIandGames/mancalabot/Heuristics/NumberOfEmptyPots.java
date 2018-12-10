package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;


@ToString
@EqualsAndHashCode
public class NumberOfEmptyPots implements Heuristic {

    private final GameTreeNode node;

    public NumberOfEmptyPots(final GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.NUMBER_OF_EMPTY_POTS;
    }

    @Override
    public int getValue(final Side ourSide) {
        int numberOfPots = 0;
        for (int i = 1; i < 8; i++) {
            if (this.node.getBoard().getSeeds(ourSide, i) == 0) {
                numberOfPots++;
            }
            if (this.node.getBoard().getSeeds(ourSide.opposite(), i) == 0) {
                numberOfPots--;
            }
        }
        return numberOfPots;
    }
}
