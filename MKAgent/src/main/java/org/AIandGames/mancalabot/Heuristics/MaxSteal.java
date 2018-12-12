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
public class MaxSteal implements Heuristic {

    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.MAX_STEAL;
    }

    @Override
    public int getValue(final Side ourSide) {
        final int isItOurTurnMultiplier = ourSide.equals(this.node.getCurrentSide()) ? 1 : -1;
        return this.maxStealValueForSide(this.node.getCurrentSide()) * isItOurTurnMultiplier;
    }

    private int maxStealValueForSide(final Side currentSide) {
        int maxStealValue = 0;
        final Board board = this.node.getBoard();

        for (int i = 1; i <= 7; i++) {
            if (board.getSeeds(currentSide, i) == 0) {  //If hole empty
                if (board.getSeedsOp(currentSide, i) > 0) { //If opposing hole has seeds
                    for (int j = 1; j < i; j++) {
                        if (this.node.getBoard().getSeeds(currentSide, j) == i - j) {
                            final int valueOfSteal = board.getSeedsOp(currentSide, i) + 1;
                            
                            if (maxStealValue < valueOfSteal) {
                                maxStealValue = valueOfSteal;
                            }
                        }
                    }
                }
                
                for (int j = i+1; j <= 7; j++) {
                    if (this.node.getBoard().getSeeds(currentSide, j) == 15 + i - j) { // Looped steal
                        final int valueOfSteal = board.getSeedsOp(currentSide, i) + 3;
                        if (maxStealValue < valueOfSteal) {
                            maxStealValue = valueOfSteal;
                        }
                    }
                }
            } else if (board.getSeeds(currentSide, i) == 15) {  //We can loop to steal same pot
                final int valueOfSteal = board.getSeedsOp(currentSide, i) + 3;
                if (maxStealValue < valueOfSteal) {
                    maxStealValue = valueOfSteal;
                }       
            }
        }
        return maxStealValue;
    }
}
