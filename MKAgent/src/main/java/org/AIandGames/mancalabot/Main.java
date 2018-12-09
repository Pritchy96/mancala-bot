package org.AIandGames.mancalabot;


import lombok.extern.java.Log;
import org.AIandGames.mancalabot.exceptions.InvalidMessageException;

import java.io.IOException;
import org.AIandGames.mancalabot.Heuristics.HeuristicWeightings;

@Log
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

       final GameRunner gameRunner = new GameRunner();

        try {
           gameRunner.run();
        } catch (final IOException | InvalidMessageException | InterruptedException | CloneNotSupportedException e) {
           log.severe(e.getMessage());
        }
    }

}
