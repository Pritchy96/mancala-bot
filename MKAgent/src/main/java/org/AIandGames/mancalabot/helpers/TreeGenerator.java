package org.AIandGames.mancalabot.helpers;

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
    private int overallDepth;
    private boolean allowSwap;

    public TreeGenerator(GameTreeNode rootNode, int overallDepth, boolean allowSwap) {
        this.rootNode = rootNode;
        this.overallDepth = overallDepth;
        this.allowSwap = allowSwap;

        threadedQueue = new LinkedBlockingDeque<>();
        int logicalCores = Runtime.getRuntime().availableProcessors();
        threadPool = new ThreadPoolExecutor(logicalCores, logicalCores, 10000L, TimeUnit.SECONDS, threadedQueue);
    }

    public TreeGenerator() {
    }

    @Override
    public void run() {
        try {
            int depthPerThread = overallDepth - SINGLE_THREAD_DEPTH;

            ArrayList<Runnable> runnables = new ArrayList<>();
            final List<GameTreeNode> leafNodesToRunThreaded = new ArrayList<>();

            rootNode.generateChildren(2, allowSwap);



            rootNode.getChildren().stream()
                    .filter(Objects::nonNull)
                    .forEach(childNode -> leafNodesToRunThreaded.addAll(childNode.getChildren()));

            leafNodesToRunThreaded.stream()
                    .filter(Objects::nonNull)
                    .forEach(node -> runnables.add(new GeneratorRunnable(node, depthPerThread, allowSwap)));

            runnables.forEach(threadPool::submit);


            while (threadedQueue.size() > 0) {
            } // nasty but works

            threadPool.shutdown();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public GameTreeNode getRootNode() {
        return rootNode;
    }
}
