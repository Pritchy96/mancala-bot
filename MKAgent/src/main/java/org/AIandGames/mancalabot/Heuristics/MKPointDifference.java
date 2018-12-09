package org.AIandGames.mancalabot.Heuristics;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MKPointDifference implements Heuristic {
    private final GameTreeNode node;


    @Override
    public Heuristics getName() {
        return Heuristics.MK_POINT_DIFFERENCE;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;

        int returnVal = getPotDifferenceForCurrentSide();

        return returnVal * isItOurTurnMultiplier;
    }

    private int getPotDifferenceForCurrentSide() {
        return this.node.getBoard().getSeedsInStore(this.node.getCurrentSide())
                - this.node.getBoard().getSeedsInStore(this.node.getCurrentSide().opposite());
    }
}
