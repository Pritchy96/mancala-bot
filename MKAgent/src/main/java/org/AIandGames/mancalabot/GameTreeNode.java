package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.TerminalState;
import org.AIandGames.mancalabot.Heutristics.HaveTheyGot15;
import org.AIandGames.mancalabot.Heutristics.HaveWeGot15;
import org.AIandGames.mancalabot.Heutristics.Heuristic;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"children"})
public class GameTreeNode {
    private Board board;
    private Map<Heuristics, Future<Long>> hValues;
    private List<GameTreeNode> children;
    private GameTreeNode parent;
    private TerminalState terminalState;
    private int depth;
    private boolean playersTurn;
    private Side currentSide;
    private long value;


    void generateHeuristicValues(ExecutorService threadPool) {
        this.hValues = new HashMap<>();

        HaveWeGot15 haveWeGot15 = new HaveWeGot15(this);
        HaveTheyGot15 haveTheyGot15 = new HaveTheyGot15(this);


        List<Heuristic> heuristics = Arrays.asList(haveWeGot15, haveTheyGot15);


        heuristics.forEach(h ->
            this.hValues.put(h.getKey(), threadPool.submit(h))
        );

        ListUtils.emptyIfNull(this.getChildren()).stream()
                .filter(Objects::nonNull)
                .forEach(child -> child.generateHeuristicValues(threadPool));
    }

    void generateChildren(int depth) throws CloneNotSupportedException {
        if (depth > 0) {
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
                } else {
                    this.children.add(null);
                }
            }

            final int newDepth = depth - 1;

            this.getChildren().stream()
                    .filter(Objects::nonNull)
                    .forEach(child -> {
                        try {
                            child.generateChildren(newDepth);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void makeMoveFromPot(Kalah kalah, int i) {
        Move move = Move.builder()
                .hole(i)
                .side(currentSide)
                .build();

        kalah.makeMove(move);
    }
}
