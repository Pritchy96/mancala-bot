package org.AIandGames.mancalabot.Heuristics;

import org.AIandGames.mancalabot.Enums.Heuristics;

public interface Heuristic {
    Heuristics getName();
    int getValue();
}
