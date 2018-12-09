package org.AIandGames.mancalabot.Heuristics;

import lombok.Getter;
import lombok.extern.java.Log;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Log
public class HeuristicWeightings {
    private static Map<Heuristics, Double> weightings;
    private static HeuristicWeightings heuristicWeightings = new HeuristicWeightings();

    public void init(final Double pointDifferenceWeight,
                     final Double maxSteal,
                     final Double nEmptyPotsWeight,
                     final Double cumulativeSteal,
                     final Double repeatSteal){
        weightings = new HashMap<>();
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, pointDifferenceWeight);
        weightings.put(Heuristics.MAX_STEAL, maxSteal);
        weightings.put(Heuristics.NUMBER_OF_EMPTY_POTS, nEmptyPotsWeight);
        weightings.put(Heuristics.CUMULATIVE_STEAL, cumulativeSteal);
        weightings.put(Heuristics.REPEAT_MOVE_AVAILABLE, repeatSteal);

    }

    public static HeuristicWeightings getInstance(){
        return heuristicWeightings;
    }

    private static void setupTempMap() {
        weightings = new EnumMap<>(Heuristics.class);
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, 1.0);
        weightings.put(Heuristics.MAX_STEAL, 0.4);
        weightings.put(Heuristics.NUMBER_OF_EMPTY_POTS, 0.2);
        weightings.put(Heuristics.CUMULATIVE_STEAL, 0.6);
        weightings.put(Heuristics.REPEAT_MOVE_AVAILABLE, 0.1);
    }

    public double applyWeightings(final Map<Heuristics, Integer> hValues, final GameTreeNode node, final Side ourSide) {
        // for each heuristic

        if (weightings == null) {
            // TODO read from file ?
            log.info("No weightings map found - using default");
            setupTempMap();
        }

        double overallValue = 0;
        for (final Heuristics key : hValues.keySet()) {
            final double value1 = hValues.getOrDefault(key, 0);
            final double value2 = weightings.getOrDefault(key, 0.0);

            overallValue += value1 * value2;

        }
        return overallValue;
    }

    //A measure of how close either player is to winning.
    public static int getProgressBasedWeighting(final GameTreeNode node, final Side ourSide) {
        return node.getBoard().getSeedsInStore(ourSide)
                + node.getBoard().getSeedsInStore(ourSide.opposite()) / 98;
    }

    //A ratio of how close we are winning compared to the opponent.
    public static int getWinBasedWeighting(final GameTreeNode node, final Side ourSide) {
        return node.getBoard().getSeedsInStore(ourSide)
                / node.getBoard().getSeedsInStore(ourSide.opposite());
    }

}
