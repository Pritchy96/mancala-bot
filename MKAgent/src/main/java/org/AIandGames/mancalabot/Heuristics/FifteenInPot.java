package org.AIandGames.mancalabot.Heuristics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@EqualsAndHashCode
@ToString
public class FifteenInPot implements Heuristic {

    private final GameTreeNode node;

    public FifteenInPot(final GameTreeNode node) {
        this.node = node;
    }

    @Override
    public Heuristics getName() {
        return Heuristics.FIFTEEN_IN_POT;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;

        int returnVal = 0;

        for (int i = 1; i <= 7; i++) {
            returnVal += (this.node.getBoard().getSeeds(this.node.getCurrentSide(), i) == 15 ? 1 : 0);
        }

        return returnVal * isItOurTurnMultiplier;
    }
}
