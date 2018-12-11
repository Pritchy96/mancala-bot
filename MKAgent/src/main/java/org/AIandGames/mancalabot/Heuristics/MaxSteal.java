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

        final Board board = this.node.getBoard();
        return this.maxStealValueForSide(this.node.getCurrentSide(), board) * isItOurTurnMultiplier;
    }

    private int maxStealValueForSide(final Side currentSide) {
        int maxStealValue = 0;
        final board currentBoard = this.node.getBoard();
        for (int i = 1; i <= 7; i++) {
            if (this.thisHoleHasASteal(currentSide, currentBoard, i)) {
                for (int j = 1; j < i; j++) {
                    if (currentBoard.getSeeds(currentSide, j) == i - j) {
                        int valueOfSteals = currentBoard.getSeedsOp(currentSide, i) + 1;
                        if (maxStealValue < valueOfSteals) {
                            maxStealValue = valueOfSteals;
                        }
                        break;
                    }
                }
            }
            if (this.thisHoleHasALoopedSteal(currentSide, i)) {
                for (int j = i; j <= 7; j++) {
                    if (currentBoard.getSeeds(currentSide, j) == i - j) {
                        int valueOfSteals = currentBoard.getSeedsOp(currentSide, i) + 1;
                        if (maxStealValue < valueOfSteals) {
                            maxStealValue = valueOfSteals;
                        }
                        break;
                    }
                }
            }
        }

        return maxStealValue;
    }

    private boolean thisHoleHasALoopedSteal(final Side currentSide, final int i) {
        return this.node.getBoard().getSeeds(currentSide, i) == 0;
    }

    private boolean thisHoleHasASteal(final Side currentSide, final int i) {
        return this.node.getBoard().getSeeds(currentSide, i) == 0 && currentBoard.getSeedsOp(currentSide, i) > 0;
    }
}
