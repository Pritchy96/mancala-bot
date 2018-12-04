package org.AIandGames.mancalabot.Heuristics;

import lombok.Getter;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Side;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HeuristicWeightings {
    private static Map<Heuristics, Double> weightings;

    private static void setupTempMap() {
        weightings = new HashMap<>();
        weightings.put(Heuristics.MK_POINT_DIFFERENCE, 1.0);
        weightings.put(Heuristics.RIGHT_MOST_POT, 1.00001);
        weightings.put(Heuristics.NUMBER_OF_EMPTY_POTS, 0.2);
    }

    public static double applyWeightings(Map<Heuristics, Integer> hValues, GameTreeNode node, Side ourSide) {
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


            if (key == Heuristics.RIGHT_MOST_POT) {
                overallValue += value1 * value2 * getProgressBasedWeighting(node, ourSide);
            } else {
                overallValue += value1 * value2;
            }
        }
        return overallValue;
    }

    //A measure of how close either player is to winning.
    public static int getProgressBasedWeighting(GameTreeNode node, Side ourSide) {
        return node.getBoard().getSeedsInStore(ourSide)
            + node.getBoard().getSeedsInStore(ourSide.opposite())/98;
    }

    //A ratio of how close we are winning compared to the opponent.
    public static int getWinBasedWeighting(GameTreeNode node, Side ourSide) {
        return node.getBoard().getSeedsInStore(ourSide)
            / node.getBoard().getSeedsInStore(ourSide.opposite());
    }

}
