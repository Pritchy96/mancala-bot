package org.AIandGames.mancalabot.Heutristics;

import org.AIandGames.mancalabot.Enums.Heuristics;

import java.util.concurrent.Callable;

public interface Heuristic extends Callable<Long> {

    public Heuristics getKey();
}
