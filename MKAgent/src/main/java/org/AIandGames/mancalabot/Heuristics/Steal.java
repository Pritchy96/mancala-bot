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
public class Steal implements Heuristic {

    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.STEAL;
    }

    @Override
    public int getValue(final Side ourSide) {
        final Board board = this.node.getBoard();
        return this.totalStealValueForSide(ourSide, board) - this.totalStealValueForSide(ourSide.opposite(), board);
    }

    private int totalStealValueForSide(final Side currentSide, final Board currentBoard) {
        int valueOfSteals = 0;

        for (int i = 1; i <= 7; i++) {
            if (this.thisHoleHasASteal(currentSide, currentBoard, i)) {
                for (int j = 1; j <= i; j++) {
                    if (currentBoard.getSeeds(currentSide, j) == i - j) {
                        valueOfSteals += currentBoard.getSeedsOp(currentSide, i);
                        break;
                    }
                }
            }
        }

        return valueOfSteals;
    }

    private boolean thisHoleHasASteal(final Side currentSide, final Board currentBoard, final int i) {
        return currentBoard.getSeeds(currentSide, i) == 0 && currentBoard.getSeedsOp(currentSide, i) > 0;
    }
}
