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
                Kalah k = Kalah.newBuilder()
                        .withBoard(this.board)
                        .build();

                GameTreeNodeBuilder newChildNBuilder = this.toBuilder();

                Move m = Move.builder()
                        .hole(i)
                        .side(currentSide)
                        .build();

                k.makeMove(m);

                GameTreeNode newChildNode = newChildNBuilder
                        .board(k.getBoard())
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
    }
}
