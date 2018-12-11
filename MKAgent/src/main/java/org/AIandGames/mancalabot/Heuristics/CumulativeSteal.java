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
public class CumulativeSteal implements Heuristic {

    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.CUMULATIVE_STEAL;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;

        return this.totalStealValueForSide(this.node.getCurrentSide()) * isItOurTurnMultiplier;
    }

    private int totalStealValueForSide(final Side currentSide) {
        int valueOfSteals = 0;

        for (int i = 1; i <= 7; i++) {
            if (this.thisHoleHasASteal(currentSide, i)) {
                for (int j = 1; j < i; j++) {
                    if (this.node.getBoard().getSeeds(currentSide, j) == i - j) {
                        valueOfSteals += this.node.getBoard().getSeedsOp(currentSide, i) + 1;
                    }
                }
            }
            if (this.thisHoleHasALoopedSteal(currentSide, i)) {
                for (int j = i; j <= 7; j++) {
                    if (this.node.getBoard().getSeeds(currentSide, j) == 15 + i - j) { // LOOPED STEAL
                        valueOfSteals += this.node.getBoard().getSeedsOp(currentSide, i) + 2;
                    }
                }
            }
        }

        return valueOfSteals;
    }

    private boolean thisHoleHasALoopedSteal(final Side currentSide, final int i) {
        return this.node.getBoard().getSeeds(currentSide, i) == 0;
    }

    private boolean thisHoleHasASteal(final Side currentSide, final int i) {
        return this.node.getBoard().getSeeds(currentSide, i) == 0 && this.node.getBoard().getSeedsOp(currentSide, i) > 0;
    }
}
