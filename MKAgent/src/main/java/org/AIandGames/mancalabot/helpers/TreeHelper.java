package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Board;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.GameTreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TreeHelper {

    public GameTreeNode generateRootNode(Side ourSide, Boolean wePlayFirst) throws CloneNotSupportedException {
        Board boardInit = new Board(7, 7);

        return GameTreeNode.builder()
                .board(boardInit.clone())
                .children(new ArrayList<>())
                .currentSide(ourSide.opposite())
                .depth(0)
                .parent(null)
                .playersTurn(wePlayFirst)
                .build();
    }

    public GameTreeNode checkTree(GameTreeNode tree, Board board) { // BFS
        final Queue<GameTreeNode> nodesToVisit = new LinkedBlockingQueue<>();
        final HashSet<GameTreeNode> visitedNodes = new HashSet<>();

        nodesToVisit.add(tree);

        while (!nodesToVisit.isEmpty()) {
            final GameTreeNode visitingNode = nodesToVisit.remove();

            if (visitingNode.getBoard().equals(board)) {
                visitingNode.setParent(null);
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

    public void updateGameTree(Board board, Thread thread, GameTreeNode tree) {
        try {
            final GameTreeNode newRoot = tree.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(child -> child.getBoard().equals(board))
                    .findFirst()
                    .orElseThrow(Exception::new);

            newRoot.setDepth(0);
            tree = newRoot;
            TreeGenerator tg = new TreeGenerator(tree, 6, false);
            thread = new Thread(tg);
            thread.start();
            newRoot.setParent(null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
