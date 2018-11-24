package org.AIandGames.mancalabot;

import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TreeCreatorHelper {
    private ExecutorService threadPool = Executors.newFixedThreadPool(9);
    private GameTreeNode node;

    public TreeCreatorHelper(GameTreeNode node) {
        this.node = node;
    }

    void generateTree(int depth) throws CloneNotSupportedException {
        this.node.generateChildren(1);

        this.node.getChildren().stream()
                .filter(Objects::nonNull)
                .forEach(n -> {
                    NodeGeneratorCallable ng = new NodeGeneratorCallable(n, depth - 1);
                    threadPool.submit(ng);
                });
    }
}
