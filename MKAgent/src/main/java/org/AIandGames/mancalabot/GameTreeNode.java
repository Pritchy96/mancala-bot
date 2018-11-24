package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.TerminalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"children"})
class GameTreeNode {
    private List<GameTreeNode> children;
    private Board board;
    private Map<Heuristics, Long> hValues;
    private GameTreeNode parent;
    private TerminalState terminalState;
    private int depth;
    private boolean playersTurn;
    private Side currentSide;
    private long value;


    void generateChildren(int depth) throws CloneNotSupportedException {
        if (depth == 8) {
            System.out.println("60606060");
        }

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
