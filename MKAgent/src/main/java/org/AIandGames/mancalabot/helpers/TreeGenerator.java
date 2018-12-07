package org.AIandGames.mancalabot.helpers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.GeneratorRunnable;

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

            Reader reader = new FileReader("Output.json"); 
            rootNode =  new GsonBuilder().create().fromJson(reader, GameTreeNode.class);

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameTreeNode getRootNode() {
        return rootNode;
    }
}
