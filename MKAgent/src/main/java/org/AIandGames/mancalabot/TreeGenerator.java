package org.AIandGames.mancalabot;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

class TreeGenerator {
    private BlockingDeque<Runnable> threadedQueue;
    private ExecutorService threadPool;

    TreeGenerator() {
        threadedQueue = new LinkedBlockingDeque<>();
        threadPool = new ThreadPoolExecutor(9, 9, 10000L, TimeUnit.SECONDS, threadedQueue);

    }



    private static final int SINGLE_THREAD_DEPTH = 2;

    void generateTree(GameTreeNode rootNode, int overallDepth) {
        try {
            int depthPerThread = overallDepth - SINGLE_THREAD_DEPTH;

            ArrayList<Runnable> runnables = new ArrayList<>();
            final List<GameTreeNode> leafNodesToRunThreaded = new ArrayList<>();

            rootNode.generateChildren(2);


            rootNode.getChildren().forEach(childNode -> leafNodesToRunThreaded.addAll(childNode.getChildren()));

            leafNodesToRunThreaded.stream()
                    .filter(Objects::nonNull)
                    .forEach(node -> runnables.add(new GeneratorRunnable(node, depthPerThread)));

            runnables.forEach(threadPool::submit);


            while (threadedQueue.size() > 0) {} // nasty but works


            threadPool.shutdown();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

}
