package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.GeneratorRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class TreeGenerator implements Runnable {
    private static final int SINGLE_THREAD_DEPTH = 3;
    private BlockingDeque<Runnable> threadedQueue;
    private ExecutorService threadPool;
    private GameTreeNode rootNode;
    private int relativeOverallDepth;
    private Side ourSide;

    public TreeGenerator(final GameTreeNode rootNode, final int overallDepth, final Side ourSide) {
        this.rootNode = rootNode;
        this.relativeOverallDepth = overallDepth;
        this.ourSide = ourSide;

        this.threadedQueue = new LinkedBlockingDeque<>();
        final int logicalCores = Runtime.getRuntime().availableProcessors();
        // TODO : Do we want to reduce this keep alive time? its longer than the entire game timeout.
        // TODO : Should we catch this and deal with it?
        this.threadPool = new ThreadPoolExecutor(logicalCores, logicalCores, 300L, TimeUnit.SECONDS, this.threadedQueue);
    }

    public TreeGenerator() {
    }

    @Override
    public void run() {
        final int depthPerThread = this.relativeOverallDepth - SINGLE_THREAD_DEPTH;

        final ArrayList<Runnable> runnables = new ArrayList<>();
        final List<GameTreeNode> leafNodesToRunThreaded = new ArrayList<>();

        this.rootNode.generateChildren(2, this.ourSide);

        this.rootNode.getChildren().stream()
                .filter(Objects::nonNull)
                .forEach(childNode -> leafNodesToRunThreaded.addAll(childNode.getChildren()));

        leafNodesToRunThreaded.stream()
                .filter(Objects::nonNull)
                .forEach(node -> runnables.add(new GeneratorRunnable(node, depthPerThread, this.ourSide)));

        runnables.forEach(this.threadPool::submit);


        while (!this.threadedQueue.isEmpty()) {
        } // nasty but works

        this.threadPool.shutdown();
    }

    public GameTreeNode getRootNode() {
        return this.rootNode;
    }
}
