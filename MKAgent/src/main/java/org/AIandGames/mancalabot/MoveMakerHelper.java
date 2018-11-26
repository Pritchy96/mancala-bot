package org.AIandGames.mancalabot;

public class MoveMakerHelper {

    static void makeMove(Board board, int hole, Side side) {
        /* from the documentation:
		  "1. The counters are lifted from this hole and sown in anti-clockwise direction, starting
		      with the next hole. The player's own kalahah is included in the sowing, but the
		      opponent's kalahah is skipped.
		   2. outcome:
		    	1. if the last counter is put into the player's kalahah, the player is allowed to
		    	   move again (such a move is called a org.AIandGames.mancalabot.Kalah-move);
		    	2. if the last counter is put in an empty hole on the player's side of the board
		    	   and the opposite hole is non-empty,
		    	   a capture takes place: all stones in the opposite opponents pit and the last
		    	   stone of the sowing are put into the player's store and the turn is over;
		    	3. if the last counter is put anywhere else, the turn is over directly.
		   3. game end:
		    	The game ends whenever a move leaves no counters on one player's side, in
		    	which case the other player captures all remaining counters. The player who
		    	collects the most counters is the winner."
		*/


        // pick seeds:
        int seedsToSow = board.getSeeds(side, hole);
        board.setSeeds(side, hole, 0);

        int holes = board.getNoOfHoles();
        int receivingPits = 2 * holes + 1;  // sow into: all holes + 1 store
        int rounds = seedsToSow / receivingPits;  // sowing rounds
        int extra = seedsToSow % receivingPits;  // seeds for the last partial round
    	/* the first "extra" number of holes get "rounds"+1 seeds, the
    	   remaining ones get "rounds" seeds */

        // sow the seeds of the full rounds (if any):
        if (rounds != 0) {
            for (int hole1 = 1; hole1 <= holes; hole1++) {
                board.addSeeds(Side.NORTH, hole1, rounds);
                board.addSeeds(Side.SOUTH, hole1, rounds);
            }
            board.addSeedsToStore(side, rounds);
        }

        // sow the extra seeds (last round):
        Side sowSide = side;
        int sowHole = hole;  // 0 means store
        for (; extra > 0; extra--) {
            // go to next pit:
            sowHole++;
            if (sowHole == 1)  // last pit was a store
                sowSide = sowSide.opposite();
            if (sowHole > holes) {
                if (sowSide == side) {
                    sowHole = 0;  // sow to the store now
                    board.addSeedsToStore(sowSide, 1);
                    continue;
                } else {
                    sowSide = sowSide.opposite();
                    sowHole = 1;
                }
            }
            // sow to hole:
            board.addSeeds(sowSide, sowHole, 1);
        }

        // capture:
        if ((sowSide == side)  // last seed was sown on the moving player's side ...
                && (sowHole > 0)  // ... not into the store ...
                && (board.getSeeds(sowSide, sowHole) == 1)  // ... but into an empty hole (so now there's 1 seed) ...
                && (board.getSeedsOp(sowSide, sowHole) > 0))  // ... and the opposite hole is non-empty
        {
            board.addSeedsToStore(side, 1 + board.getSeedsOp(side, sowHole));
            board.setSeeds(side, sowHole, 0);
            board.setSeedsOp(side, sowHole, 0);
        }

        // game over?
        Side finishedSide = null;
        if (holesEmpty(board, side))
            finishedSide = side;
        else if (holesEmpty(board, side.opposite()))
            finishedSide = side.opposite();
    		/* note: it is possible that both sides are finished, but then
    		   there are no seeds to collect anyway */
        if (finishedSide != null) {
            // collect the remaining seeds:
            int seeds = 0;
            Side collectingSide = finishedSide.opposite();
            for (int hole1 = 1; hole1 <= holes; hole1++) {
                seeds += board.getSeeds(collectingSide, hole1);
                board.setSeeds(collectingSide, hole1, 0);
            }
            board.addSeedsToStore(collectingSide, seeds);
        }
    }

    /**
     * Checks whether all holes on a given side are empty.
     *
     * @param board The board to check.
     * @param side  The side to check.
     * @return "true" iff all holes on side "side" are empty.
     */
    private static boolean holesEmpty(Board board, Side side) {
        for (int hole = 1; hole <= board.getNoOfHoles(); hole++)
            if (board.getSeeds(side, hole) != 0)
                return false;
        return true;
    }
}