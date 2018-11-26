package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.TerminalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"children"})
class GameTreeNode {
    private Board board;
    private Map<Heuristics, Long> hValues;
    private List<GameTreeNode> children;
    private GameTreeNode parent;
    private TerminalState terminalState;
    private int depth;
    private boolean playersTurn;
    private Side currentSide;
    private long value;



    // Doesnt need to use this long, currently in for testing and debugging purposes.
    long generateChildren(int desiredDepth) throws CloneNotSupportedException {
        AtomicLong childGeneratedCount = new AtomicLong();
        if (desiredDepth > 0) {
            if (this.children.isEmpty()) {
                for (int i = 1; i <= 7; i++) {
                    if (this.board.getSeeds(currentSide, i) > 0) {
                        GameTreeNodeBuilder newChildNBuilder = this.toBuilder();
                        Kalah kalah = Kalah.newBuilder().withBoard(this.board.clone()).build();

                        makeMoveFromPot(kalah, i);

                        GameTreeNode newChildNode = newChildNBuilder
                                .board(kalah.getBoard().clone())
                                .depth(this.depth + 1)
                                .parent(this)
                                .children(new ArrayList<>())
                                .terminalState(TerminalState.NON_TERMINAL)
                                .currentSide(this.currentSide.opposite())
                                .playersTurn(!this.playersTurn)
                                .build();

                        this.children.add(newChildNode);
                        childGeneratedCount.getAndIncrement();
                    } else {
                        this.children.add(null);
                    }
                }
            }
            final int newDepth = desiredDepth - 1;

            this.getChildren().stream()
                    .filter(Objects::nonNull)
                    .forEach(child -> {
                        try {
                            childGeneratedCount.addAndGet(child.generateChildren(newDepth));
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return childGeneratedCount.get();
    }

    private void makeMoveFromPot(Kalah kalah, int i) {
        Move move = Move.builder()
                .hole(i)
                .side(currentSide)
                .build();

        kalah.makeMove(move);
    }

    public static void main(String[] args) {
        GameTreeNode root = GameTreeNode.builder()
                .terminalState(TerminalState.NON_TERMINAL)
                .currentSide(Side.SOUTH)
                .parent(null)
                .depth(0)
                .board(new Board(7,7))
                .children(new ArrayList<>())
                .hValues(null)
                .playersTurn(true)
                .value(0)
                .build();

        try {
            System.err.println(root.generateChildren(5));
            System.err.println(root.generateChildren(6));
            System.err.println(root.generateChildren(7));
            System.err.println(root.generateChildren(8));
            System.err.println(root.generateChildren(9));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
