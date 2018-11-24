package org.AIandGames.mancalabot;

import java.util.concurrent.Callable;

public class NodeGeneratorCallable implements Callable<GameTreeNode> {
    GameTreeNode node;
    int depth;

    public NodeGeneratorCallable(GameTreeNode node, int depth) {
        this.node = node;
        this.depth = depth;
    }

    @Override
    public GameTreeNode call() throws Exception {
        this.node.generateChildren(depth);
        return node;
    }
}
