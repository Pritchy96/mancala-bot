package org.AIandGames.mancalabot.helpers;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.GsonBuilder;

import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Enums.Side;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TreeHelper {
    private final int overallDepth;

    public GameTreeNode generateRootNode(final Side ourSide, final Board board) throws CloneNotSupportedException {

        try {
            final Reader reader = new FileReader("tree.json"); 
            final GameTreeNode root = new GsonBuilder().create().fromJson(reader, GameTreeNode.class);
            reader.close();
            return root;
        } catch (IOException fileException) {
            return GameTreeNode.builder()
                    .board(board)
                    .children(new ArrayList<>())
                    .currentSide(ourSide.opposite())
                    .build();
        }
    }

    public GameTreeNode updateRootNode(final Board board, final GameTreeNode tree, final Side ourSide) throws CloneNotSupportedException { // BFS
        final Queue<GameTreeNode> nodesToVisit = new LinkedBlockingQueue<>();
        final HashSet<GameTreeNode> visitedNodes = new HashSet<>();

        nodesToVisit.add(tree);

        while (!nodesToVisit.isEmpty()) {
            final GameTreeNode visitingNode = nodesToVisit.remove();

            if (visitingNode.getBoard().equals(board) && !visitingNode.equals(tree)) {
                return visitingNode;
            }

            try {
                visitingNode.getChildren().stream()
                        .filter(Objects::nonNull)
                        .filter(child -> !visitedNodes.contains(child))
                        .forEach(nodesToVisit::add);
            } catch (final Exception e) {
                System.err.println("Err");
            }

            visitedNodes.add(visitingNode);
        }

        // the node was not found at a depth > 0. Return the current root if the move was in the past else null.
        if (!this.moveWasInThePast(board, tree)) {
            return this.generateRootNode(ourSide, board);
        }
        return tree;
    }

    private boolean moveWasInThePast(final Board board, final GameTreeNode tree) {
        return tree.getBoard().getSeedsInStore(Side.SOUTH) < board.getSeedsInStore(Side.SOUTH)
                && tree.getBoard().getSeedsInStore(Side.NORTH) < board.getSeedsInStore(Side.NORTH);
    }

    public int getMaxDepthOfTree(final List<GameTreeNode> tree) {

        final List<GameTreeNode> nodeList = new ArrayList<>();
        if (tree.isEmpty())
            return 0;
        else {
            tree.stream().filter(Objects::nonNull).forEach(n -> nodeList.addAll(n.getChildren()));
            return 1 + this.getMaxDepthOfTree(nodeList);
        }
    }

    public UpdateReturnable updateGameTree(final Board board, GameTreeNode tree, final Side ourSide) {
        try {
            final Thread thread;
            tree = this.updateRootNode(board, tree, ourSide);

            final Runnable createTreeRunner = new TreeGenerator(tree, this.overallDepth - 1, false, ourSide);
            thread = new Thread(createTreeRunner);
            thread.start();
            return new UpdateReturnable(tree, thread);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return new UpdateReturnable(tree, null);
    }
}
