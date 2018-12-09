package org.AIandGames.mancalabot.Heuristics;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class NumberOfEmptyPots implements Heuristic {

    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.NUMBER_OF_EMPTY_POTS;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;

        int numberOfPots = 0;
        for (int i = 1; i < 8; i++) {
            if (this.node.getBoard().getSeeds(this.node.getCurrentSide(), i) == 0) {
                numberOfPots++;
            }
            if (this.node.getBoard().getSeeds(this.node.getCurrentSide().opposite(), i) == 0) {
                numberOfPots--;
            }
        }
        return numberOfPots * isItOurTurnMultiplier;
    }
}
