package org.AIandGames.mancalabot;


import org.AIandGames.mancalabot.Heuristics.HeuristicWeightings;

public class Main {

    public static void main(final String[] args) {
        final HeuristicWeightings heuristicWeightings = HeuristicWeightings.getInstance();

        if (args.length == 5) {
            heuristicWeightings.init(Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3]),
                    Double.parseDouble(args[4]));
        }
        final GameRunner gr = new GameRunner();
        gr.run();
    }

}
