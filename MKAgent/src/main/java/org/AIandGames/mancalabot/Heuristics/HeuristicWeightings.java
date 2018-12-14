package org.AIandGames.mancalabot.Heuristics;

import lombok.Getter;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HeuristicWeightings {
    private static Map<Heuristics, Double> weightings;
    private static HeuristicWeightings heuristicWeightings = new HeuristicWeightings();

    public void init(final Double pointDifferenceWeight,
                     final Double maxSteal,
                     final Double nEmptyPotsWeight,
                     final Double cumulativeSteal,
                     final Double repeatSteal,
                     final Double seedsAdvantage){
        weightings = new HashMap<>();
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, pointDifferenceWeight);
        weightings.put(Heuristics.MAX_STEAL, maxSteal);
        weightings.put(Heuristics.NUMBER_OF_EMPTY_POTS, nEmptyPotsWeight);
        weightings.put(Heuristics.CUMULATIVE_STEAL, cumulativeSteal);
        weightings.put(Heuristics.REPEAT_MOVE_AVAILABLE, repeatSteal);
        weightings.put(Heuristics.SEEDS_ON_SIDE, seedsAdvantage);

    }

    public static HeuristicWeightings getInstance(){
        return heuristicWeightings;
    }

    private static void setupTempMap() {
        weightings = new HashMap<>();
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, 3.1);
        weightings.put(Heuristics.MAX_STEAL, 1.0);
        weightings.put(Heuristics.NUMBER_OF_EMPTY_POTS, 1.0);
        weightings.put(Heuristics.CUMULATIVE_STEAL, 0.5);
        weightings.put(Heuristics.REPEAT_MOVE_AVAILABLE, 1.0);
        weightings.put(Heuristics.SEEDS_ON_SIDE, 0.8);

    }

    public double applyWeightings(final Map<Heuristics, Integer> hValues, final GameTreeNode node, final Side ourSide) {
        // for each heuristic

        if (weightings == null) {
            // TODO read from file ?
            System.err.println("No weightings map found - using default");
            setupTempMap();
        }

        double overallValue = 0;
        for (final Heuristics key : hValues.keySet()) {
            final double value1 = hValues.getOrDefault(key, 0);
            final double value2 = weightings.getOrDefault(key, 0.0);
            // TODO: Check what happens when null, should auto to 0

            overallValue += value1 * value2;

        }
        return overallValue;
    }

}
