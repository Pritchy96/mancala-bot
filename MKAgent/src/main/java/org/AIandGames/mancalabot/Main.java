package org.AIandGames.mancalabot;


import org.AIandGames.mancalabot.Heuristics.HeuristicWeightings;

public class Main {

    public static void main(final String[] args) {
        final HeuristicWeightings heuristicWeightings = HeuristicWeightings.getInstance();

        if (args.length == 6) {
            heuristicWeightings.init(Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3]),
                    Double.parseDouble(args[4]),
                    Double.parseDouble(args[5]));
        }
        final GameRunner gameRunner = new GameRunner();
        gameRunner.run();
    }

}
