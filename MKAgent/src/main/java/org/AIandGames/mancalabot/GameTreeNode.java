package org.AIandGames.mancalabot;

import lombok.*;
import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.TerminalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    void generateChildren() {
        for (int i = 1; i <= 7; i++) {
            if (this.board.getSeeds(currentSide, i) > 0) {
                GameTreeNodeBuilder newChildNBuilder = this.toBuilder();
                Kalah kalah = Kalah.newBuilder().withBoard(this.board).build();

                makeMoveFromPot(kalah, i);

                GameTreeNode newChildNode = newChildNBuilder
                        .board(kalah.getBoard())
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
    }

    private void makeMoveFromPot(Kalah kalah, int i) {
        Move move = Move.builder()
                .hole(i)
                .side(currentSide)
                .build();

        kalah.makeMove(move);
    }
}
