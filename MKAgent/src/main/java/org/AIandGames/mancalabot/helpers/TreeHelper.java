package org.AIandGames.mancalabot.helpers;

import lombok.AllArgsConstructor;
import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class TreeHelper {
    private int overallDepth;

    public GameTreeNode generateRootNode(Side ourSide, Boolean wePlayFirst) throws CloneNotSupportedException {
        Board boardInit = new Board(7, 7);

        return GameTreeNode.builder()
                .board(boardInit.clone())
                .children(new ArrayList<>())
                .currentSide(ourSide.opposite())
                .playersTurn(wePlayFirst)
                .build();
    }

    public GameTreeNode updateRootNode(Board board, GameTreeNode tree) { // BFS
        final Queue<GameTreeNode> nodesToVisit = new LinkedBlockingQueue<>();
        final HashSet<GameTreeNode> visitedNodes = new HashSet<>();

        nodesToVisit.add(tree);

        while (!nodesToVisit.isEmpty()) {
            final GameTreeNode visitingNode = nodesToVisit.remove();

            if (visitingNode.getBoard().equals(board)  && !visitingNode.equals(tree)) {
                return visitingNode;
            }

            visitingNode.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(child -> !visitedNodes.contains(child))
                    .forEach(nodesToVisit::add);

            visitedNodes.add(visitingNode);
        }
        return tree;
    }
    public int getMaxDepthOfTree(List<GameTreeNode> tree) {

        List<GameTreeNode> nodeList = new ArrayList<>();
        if ( tree.isEmpty() )
            return 0;
        else {
            tree.stream().filter(Objects::nonNull).forEach(n -> nodeList.addAll(n.getChildren()));
            return 1 + getMaxDepthOfTree(nodeList);
        }
    }

    public UpdateReturnable updateGameTree(Board board, GameTreeNode tree) {
        try {
            Thread thread;
            tree = updateRootNode(board, tree);

            final Runnable createTreeRunner = new TreeGenerator(tree, this.overallDepth - 1, false);
            thread = new Thread(createTreeRunner);
            thread.start();
            return new UpdateReturnable(tree, thread);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new UpdateReturnable(tree, null);
    }


}
