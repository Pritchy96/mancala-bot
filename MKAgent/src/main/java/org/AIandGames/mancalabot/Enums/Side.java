package org.AIandGames.mancalabot.Enums;

/**
 * The side of the org.AIandGames.mancalabot.Kalah board a player can choose.
 */
public enum Side {
    NORTH, SOUTH;

    /**
     * @return the side opposite to this one.
     */
    public Side opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            default:
                return NORTH;  // dummy
        }
    }
}
