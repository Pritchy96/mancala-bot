package org.AIandGames.mancalabot.helpers;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
    private int overallDepth;

    public GameTreeNode generateRootNode(Side ourSide, Boolean wePlayFirst) throws CloneNotSupportedException {

        Board boardInit = new Board(7, 7);

        try {
            Reader reader = new FileReader("tree.json"); 
            return new GsonBuilder().create().fromJson(reader, GameTreeNode.class);
        } catch (FileNotFoundException fileException) {
            System.err.println("No Input tree.json found, generating a tree instead");
            
            return GameTreeNode.builder()
            .board(boardInit.clone())
            .children(new ArrayList<>())
            .currentSide(ourSide.opposite())
            .playersTurn(wePlayFirst)
            .build();
        }
    }

    public GameTreeNode checkTree(GameTreeNode tree, Board board) { // BFS
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

    public Thread updateGameTree(Board board, GameTreeNode tree) {
        try {
            Thread thread;
            tree = checkTree(tree, board);

            TreeGenerator tg = new TreeGenerator(tree, this.overallDepth - 1, false);
            thread = new Thread(tg);
            thread.start();
            return thread;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
