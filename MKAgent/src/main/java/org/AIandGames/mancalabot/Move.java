package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Side;

/**
 * Represents a move (not a turn) in the org.AIandGames.mancalabot.Kalah game.
 */
@Builder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Move {
    /**
     * The side of the board the player making the move is playing on.
     */
    private final Side side;
    /**
     * The hole from which seeds are picked at the beginning of the move and
     * distributed. It has to be >= 1.
     */
    private final int hole;


    /**
     * @param side The side of the board the player making the move is playing
     *             on.
     * @param hole The hole from which seeds are picked at the beginning of
     *             the move and distributed. It has to be >= 1.
     * @throws IllegalArgumentException if the hole number is not >= 1.
     */
    public Move(final Side side, final int hole) throws IllegalArgumentException {
        if (hole < 1)
            throw new IllegalArgumentException("Hole numbers must be >= 1, but " + hole + " was given.");
        this.side = side;
        this.hole = hole;
    }

    /**
     * @return The side of the board the player making the move is playing on.
     */
    public Side getSide() {
        return this.side;
    }

    /**
     * @return The hole from which seeds are picked at the beginning of the
     * move and distributed. It will be >= 1.
     */
    public int getHole() {
        return this.hole;
    }

}
