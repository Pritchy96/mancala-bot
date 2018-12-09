package org.AIandGames.mancalabot.Enums;

public enum Heuristics {
    MK_POINT_DIFFERENCE("MK_POINT_DIFFERENCE"),
    RIGHT_MOST_POT("RIGHT_MOST_POT"),
    NUMBER_OF_EMPTY_POTS("NUMBER_OF_EMPTY_POTS"),
    REPEAT_MOVE_AVAILABLE("REPEAT_MOVE_AVAILABLE");

    Heuristics(final String heuristic) {
    }
}
