package org.AIandGames.mancalabot.Heuristics;

import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;

public interface Heuristic {
    Heuristics getName();
    int getValue(Side ourSide);
}
