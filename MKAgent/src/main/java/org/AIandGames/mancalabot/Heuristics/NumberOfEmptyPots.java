package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Side;


@ToString
@EqualsAndHashCode
public class NumberOfEmptyPots implements Heuristic {

    private GameTreeNode node;

    public NumberOfEmptyPots(GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.NUMBER_OF_EMPTY_POTS;
    }

    @Override
    public int getValue(Side ourSide) {
        int numberOfPots = 0;
        for (int i = 1; i < 8; i++) {
            if (node.getBoard().getSeeds(ourSide, i) == 0) {
                numberOfPots++;
            }
        }
        return numberOfPots;
    }
}
