package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Enums.Heuristics;
import org.AIandGames.mancalabot.Enums.TerminalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameTreeNode {
    private Board board;
    private Map<Heuristics, Long> hValues;
    private List<GameTreeNode> children;
    private GameTreeNode parent;
    private TerminalState terminalState;
    private int depth;
    private boolean playersTurn;
    private Side currentSide;
    private long value;

    GameTreeNode(Board board, List<GameTreeNode> children, GameTreeNode parent, Integer depth, boolean playersTurn, Side currentSide) {
        this.board = board;
        this.children = children;
        this.parent = parent;
        this.depth = depth;
        this.playersTurn = playersTurn;
        this.currentSide = currentSide;
    }

    private GameTreeNode(Builder builder) {
        setBoard(builder.board);
        sethValues(builder.hValues);
        setChildren(builder.children);
        setParent(builder.parent);
        setTerminalState(builder.terminalState);
        setDepth(builder.depth);
        setPlayersTurn(builder.playersTurn);
        setCurrentSide(builder.currentSide);
        setValue(builder.value);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(GameTreeNode copy) {
        Builder builder = new Builder();
        builder.board = copy.getBoard();
        builder.hValues = copy.getHValues();
        builder.children = copy.getChildren();
        builder.parent = copy.getParent();
        builder.terminalState = copy.getTerminalState();
        builder.depth = copy.getDepth();
        builder.playersTurn = copy.getPlayersTurn();
        builder.currentSide = copy.getCurrentSide();
        builder.value = copy.getValue();
        return builder;
    }

    public void generateChildren() {
        for (int i = 1; i <= 7; i++) {
            if (this.board.getSeeds(currentSide, i) > 0) {
                Kalah k = Kalah.newBuilder()
                        .withBoard(this.board)
                        .build();

                Builder newChildNBuilder = GameTreeNode.newBuilder(this);

                Move m = Move.newBuilder()
                        .withHole(i)
                        .withSide(currentSide)
                        .build();

                k.makeMove(m);

                GameTreeNode newChildNode = newChildNBuilder
                        .withBoard(k.getBoard())
                        .withDepth(this.depth + 1)
                        .withParent(this)
                        .withChildren(new ArrayList<>())
                        .withTerminalState(TerminalState.NON_TERMINAL)
                        .withCurrentSide(this.currentSide.opposite())
                        .withPlayersTurn(!this.playersTurn)
                        .build();

                this.children.add(newChildNode);
            } else {
                this.children.add(null);
            }
        }
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Map<Heuristics, Long> getHValues() {
        return hValues;
    }

    public void sethValues(Map<Heuristics, Long> hValues) {
        this.hValues = hValues;
    }

    public List<GameTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<GameTreeNode> children) {
        this.children = children;
    }

    public GameTreeNode getParent() {
        return parent;
    }

    public void setParent(GameTreeNode parent) {
        this.parent = parent;
    }

    public TerminalState getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(TerminalState terminalState) {
        this.terminalState = terminalState;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean getPlayersTurn() {
        return playersTurn;
    }

    public void setPlayersTurn(boolean playersTurn) {
        this.playersTurn = playersTurn;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Side getCurrentSide() {
        return currentSide;
    }

    public void setCurrentSide(Side currentSide) {
        this.currentSide = currentSide;
    }

    @Override
    public String toString() {
        return "GameTreeNode{" +
                "board=" + board +
//                ", hValues=" + hValues +
//                ", children=" + children +
                ", parent=" + parent +
                ", terminalState=" + terminalState +
                ", depth=" + depth +
                ", playersTurn=" + playersTurn +
                ", currentSide=" + currentSide +
                ", value=" + value +
                '}';
    }

    public static final class Builder {
        private Board board;
        private Map<Heuristics, Long> hValues;
        private List<GameTreeNode> children;
        private GameTreeNode parent;
        private TerminalState terminalState;
        private int depth;
        private boolean playersTurn;
        private Side currentSide;
        private long value;

        private Builder() {
        }

        public Builder withBoard(Board val) {
            board = val;
            return this;
        }

        public Builder withHValues(Map<Heuristics, Long> val) {
            hValues = val;
            return this;
        }

        public Builder withChildren(List<GameTreeNode> val) {
            children = val;
            return this;
        }

        public Builder withParent(GameTreeNode val) {
            parent = val;
            return this;
        }

        public Builder withTerminalState(TerminalState val) {
            terminalState = val;
            return this;
        }

        public Builder withDepth(int val) {
            depth = val;
            return this;
        }

        public Builder withPlayersTurn(boolean val) {
            playersTurn = val;
            return this;
        }

        public Builder withCurrentSide(Side val) {
            currentSide = val;
            return this;
        }

        public Builder withValue(long val) {
            value = val;
            return this;
        }

        public GameTreeNode build() {
            return new GameTreeNode(this);
        }
    }
}
