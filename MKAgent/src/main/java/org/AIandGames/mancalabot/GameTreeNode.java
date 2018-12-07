package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Enums.TerminalState;
import org.AIandGames.mancalabot.Heuristics.*;

import java.util.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"children", "parent"})
public class GameTreeNode {
    private Board board;
    private Map<Heuristics, Integer> hValues;
    private List<GameTreeNode> children;
    private TerminalState terminalState;
    private Side currentSide;
    private int depth;
    private int holeNumber;

    private double getValue(final Side ourSide) {
        if (this.children.isEmpty()) { // Leaf node
            if (this.terminalState == TerminalState.WIN_TERMINAL) {
                return Integer.MAX_VALUE;
            } else if (this.terminalState == TerminalState.LOSE_TERMINAL) {
                return Integer.MIN_VALUE;
            } else {
                this.runHeuristics(ourSide);
                return HeuristicWeightings.applyWeightings(this.hValues, this, ourSide);
            }
        } else {

            double value = 0;

            GameTreeNode child = null;
            boolean encounteredNonNullChild = false;

            for (int i = 0; i < this.children.size(); i++) {
                child = this.children.get(i);
                if (child == null) {
                    continue;
                }

                if (!encounteredNonNullChild) {
                    encounteredNonNullChild = true;
                    value = child.getValue(ourSide);
                } else {
                    final double childVal = child.getValue(ourSide);
                    if ((child.getPlayersTurn(ourSide) && childVal > value) || (!child.getPlayersTurn(ourSide) && childVal < value)) {
                        value = childVal;
                    }
                }
            }
            return value;
        }
    }

    public boolean getPlayersTurn(final Side ourSide) {
        return this.currentSide.equals(ourSide);
    }

    public Move getBestMove(final Side ourSide) {
        GameTreeNode bestChild = null;
        double maxValue = Integer.MIN_VALUE;

        for (final GameTreeNode child : this.children) {
            if (child == null) {
                continue;
            }

            final double val = child.getValue(ourSide);
            if (val >= maxValue) {
                bestChild = child;
                maxValue = val;
            }
        }
        if (bestChild == null) {
            return null;
        }
        return new Move(ourSide, bestChild.holeNumber);
    }


    public void runHeuristics(final Side ourSide) {
        this.hValues = new HashMap<>();

        final ArrayList<Heuristic> hs = new ArrayList<>();
        hs.add(new MKPointDifference(this));
        hs.add(new RightMostPot(this));
        hs.add(new NumberOfEmptyPots(this));

        hs.forEach(h -> this.hValues.put(h.getName(), h.getValue(ourSide)));
    }

    public void generateChildren(final int depth, final boolean allowSwap) throws CloneNotSupportedException {
        if (depth == 0) {
            return;
        }

        if (this.children.isEmpty()) {
            this.generateUpTo7Children();
            this.addSwapNodeIfApplicable(allowSwap);
        }

        final int newDepth = depth - 1;

        this.getChildren().stream()
                .filter(Objects::nonNull)
                .forEach(child -> {
                    try {
                        child.generateChildren(newDepth, allowSwap);
                    } catch (final CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                });

    }

    private void generateUpTo7Children() throws CloneNotSupportedException {
        for (int i = 1; i <= 7; i++) {
            if (this.board.getSeeds(this.currentSide, i) > 0) {
                final GameTreeNodeBuilder newChildNBuilder = this.toBuilder();

                final Board newBoard = this.board.clone();
                final Side newSide = this.makeMove(newBoard, i, this.currentSide);

                final GameTreeNode newChildNode = newChildNBuilder
                        .board(newBoard)
                        .children(new ArrayList<>())
                        .terminalState(TerminalState.NON_TERMINAL)   // not always
                        .currentSide(newSide)
                        .holeNumber(i)
                        .depth(this.depth + 1)
                        .build();

                this.children.add(newChildNode);
            } else {
                this.children.add(null);
            }
        }
    }

    private void addSwapNodeIfApplicable(final boolean allowSwap) throws CloneNotSupportedException {
        if (this.isSwapPossible() && allowSwap) {
            final GameTreeNode swapNode = this.toBuilder()
                    .board(this.board.clone())
                    .children(new ArrayList<>())
                    .terminalState(TerminalState.NON_TERMINAL)
                    .currentSide(this.currentSide)
                    .depth(this.depth + 1)
                    .build();
            this.children.add(swapNode);
        }
    }

    private boolean isSwapPossible() {
        return this.getDepth() == 1;
    }


    private Side makeMove(final Board board, final int hole, final Side side) {
        /* from the documentation:
		  "1. The counters are lifted from this hole and sown in anti-clockwise direction, starting
		      with the next hole. The player's own kalahah is included in the sowing, but the
		      opponent's kalahah is skipped.
		   2. outcome:
		    	1. if the last counter is put into the player's kalahah, the player is allowed to
		    	   move ourTurn (such a move is called a org.AIandGames.mancalabot.Kalah-move);
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
        final int seedsToSow = board.getSeeds(side, hole);
        board.setSeeds(side, hole, 0);

        final int holes = board.getNoOfHoles();
        final int receivingPits = 2 * holes + 1;  // sow into: all holes + 1 store
        final int rounds = seedsToSow / receivingPits;  // sowing rounds
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
        if (this.holesEmpty(board, side))
            finishedSide = side;
        else if (this.holesEmpty(board, side.opposite()))
            finishedSide = side.opposite();
    		/* note: it is possible that both sides are finished, but then
    		   there are no seeds to collect anyway */
        if (finishedSide != null) {
            // collect the remaining seeds:
            int seeds = 0;
            final Side collectingSide = finishedSide.opposite();
            for (int hole1 = 1; hole1 <= holes; hole1++) {
                seeds += board.getSeeds(collectingSide, hole1);
                board.setSeeds(collectingSide, hole1, 0);
            }
            board.addSeedsToStore(collectingSide, seeds);
        }

        // who's turn is it?
        if (sowHole == 0)  // the store (implies (sowSide == move.getSide()))
            return side;  // move ourTurn
        else
            return side.opposite();
    }

    /**
     * Checks whether all holes on a given side are empty.
     *
     * @param board The board to check.
     * @param side  The side to check.
     * @return "true" iff all holes on side "side" are empty.
     */
    private boolean holesEmpty(final Board board, final Side side) {
        for (int hole = 1; hole <= board.getNoOfHoles(); hole++)
            if (board.getSeeds(side, hole) != 0)
                return false;
        return true;
    }

    @Override
    public String toString() {
        return "Depth: " + this.getDepth() + " sizeOfChildren: " + this.getChildren().size();
    }
}

