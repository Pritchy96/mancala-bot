package org.AIandGames.mancalabot.Enums;

public enum Heuristics {
    MK_POINT_DIFFERENCE("MK_POINT_DIFFERENCE"),
    CUMULATIVE_STEAL("CUMULATIVE_STEAL"),
    REPEAT_MOVE_AVAILABLE("REPEAT_MOVE_AVAILABLE"),
    MAX_STEAL("MAX_STEAL"),
    NUMBER_OF_EMPTY_POTS("NUMBER_OF_EMPTY_POTS"),
    SEEDS_ON_SIDE("SEEDS_ON_SIDE");

    Heuristics(final String heuristic) {
    }
}
