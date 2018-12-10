package org.AIandGames.mancalabot.Heuristics;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Kalah;

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
        final int northSideReturn = Side.NORTH.equals(ourSide) ? 1 : -1;
        final int southSideReturn = Side.SOUTH.equals(ourSide) ? 1 : -1;

        final Kalah kalah = new Kalah(this.node.getBoard());

        return MovesStaticList.MOVES_LIST.stream()
                .filter(kalah::isLegalMove)
                .mapToInt(move -> kalah.makeMove(move).equals(Side.NORTH) ? northSideReturn : southSideReturn)
                .reduce(0, (acc, point) -> acc + point);
    }
}
