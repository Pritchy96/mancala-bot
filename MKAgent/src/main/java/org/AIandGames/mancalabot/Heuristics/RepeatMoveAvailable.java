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
public class RepeatMoveAvailable implements Heuristic {
    private final GameTreeNode node;

    @Override
    public Heuristics getName() {
        return Heuristics.REPEAT_MOVE_AVAILABLE;
    }

    @Override
    public int getValue(final Side ourSide) {

        final Side currentSide = this.node.getCurrentSide();

        final int isItOurTurnMultiplier = ourSide.equals(currentSide) ? 1 : -1;

        int numberOfRepeatMovesAvailable = 0;

        for (int i = 1; i <= 7; i++) {
            if (this.moveGivesAnotherMove(currentSide, i)) {
                numberOfRepeatMovesAvailable += 1;
            }
        }

        return numberOfRepeatMovesAvailable * isItOurTurnMultiplier;
    }

    //checks for repeat moves possible through up to 2 loops of the board
    private boolean moveGivesAnotherMove(final Side currentSide, final int holeNumber) {

        final int numberOfSeedsInPot = this.node.getBoard().getSeeds(currentSide, holeNumber);

        return (numberOfSeedsInPot + holeNumber == 8) || (numberOfSeedsInPot + holeNumber == 23);
    }

}
