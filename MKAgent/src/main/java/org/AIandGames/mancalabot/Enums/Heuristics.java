package org.AIandGames.mancalabot.Enums;

public enum Heuristics {
    MK_POINT_DIFFERENCE("MK_POINT_DIFFERENCE"),
    CUMULATIVE_STEAL("CUMULATIVE_STEAL"),
    FIFTEEN_IN_POT("FIFTEEN_IN_POT"),
    REPEAT_MOVE_AVAILABLE("REPEAT_MOVE_AVAILABLE"),
    MAX_STEAL("MAX_STEAL"),
    NUMBER_OF_EMPTY_POTS("NUMBER_OF_EMPTY_POTS");

    Heuristics(final String heuristic) {
    }
}
