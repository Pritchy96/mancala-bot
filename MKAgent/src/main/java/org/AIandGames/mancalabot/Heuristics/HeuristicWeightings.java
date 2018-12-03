package org.AIandGames.mancalabot.Heuristics;

import lombok.Getter;
import org.AIandGames.mancalabot.Enums.Heuristics;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HeuristicWeightings {
    private static Map<Heuristics, Double> weightings;

    private static void setupTempMap() {
        weightings = new HashMap<>();
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, 1.0);
        weightings.put(Heuristics.RIGHT_MOST_POT, 0.00001);
    }

    public static double applyWeightings(Map<Heuristics, Integer> hValues) {
        // for each heuristic

        if (weightings == null) {
            // TODO read from file ?
            System.err.println("No weightings map found - using default");
            setupTempMap();
        }

        double overallValue = 0;
        for (Heuristics key : hValues.keySet()) {
            double value1 = hValues.get(key);
            double value2 = weightings.get(key);
            // TODO: Check what happens when null, should auto to 0

            overallValue += value1 * value2;
        }
        return overallValue;
    }

}
