package org.AIandGames.mancalabot.Heuristics;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SeedsOnSide implements Heuristic {

    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.SEEDS_ON_SIDE;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;

        int seedsOnCurrentSide = 0;
        int seedsOnOppositeSide = 0;
        int returnValue = 0;

        for (int i = 1; i <= 7; i++) {
            seedsOnCurrentSide += this.node.getBoard().getSeeds(this.node.getCurrentSide(), i);
            seedsOnOppositeSide += this.node.getBoard().getSeeds(this.node.getCurrentSide().opposite(), i);
        }

        returnValue = seedsOnCurrentSide - seedsOnOppositeSide;

        returnValue = weightByGameProgress(returnValue);

        return returnValue * isItOurTurnMultiplier;
    }

    private int weightByGameProgress(int unweightedReturnValue) {
        int totalPottedSeeds = this.node.getBoard().getSeedsInStore(this.node.getCurrentSide())
                +  this.node.getBoard().getSeedsInStore(this.node.getCurrentSide().opposite());

        float progressRatio = totalPottedSeeds / 98.0f;

        if (progressRatio > 0.5 ) {
            progressRatio = (progressRatio * 2) - 1;//map the value from {0.5-1} to {0-1}
            return Math.round(unweightedReturnValue * progressRatio);
        } else {
            return 0;
        }
    }
}