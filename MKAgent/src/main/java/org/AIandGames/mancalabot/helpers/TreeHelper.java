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
import org.AIandGames.mancalabot.Enums.TerminalState;
import org.AIandGames.mancalabot.GameTreeNode;
import org.AIandGames.mancalabot.Enums.Side;

import lombok.AllArgsConstructor;
import org.AIandGames.mancalabot.Protocol;

@AllArgsConstructor
public class TreeHelper {
    private final int relativeOverallDepth;

    public GameTreeNode loadOrGenerateRootNodeAtGameStart(final Side ourSide, final Board board) {

        try {
            final Reader reader = new FileReader("tree.json");
            final GameTreeNode root = new GsonBuilder().create().fromJson(reader, GameTreeNode.class);
            reader.close();
            return root;
        } catch (IOException fileException) {
            System.err.println("tree.json not found, generating from scratch.");
        }
        return generateRootNodeWithoutLoading(ourSide, board, true);
    }

    public GameTreeNode generateRootNodeWithoutLoading(final Side ourSide, final Board board, boolean southSide) {
        TerminalState terminalState;

        if (board.getSeedsInStore(ourSide) >= 50) {
            terminalState = TerminalState.WIN_TERMINAL;
        } else if (board.getSeedsInStore(ourSide.opposite()) >= 50) {
            terminalState = TerminalState.LOSE_TERMINAL;
        } else {
            terminalState = TerminalState.NON_TERMINAL;
        }

        Side currentSide;
        if (southSide) {
            currentSide = Side.SOUTH;
        } else {
            currentSide = Side.NORTH;
        }

        return GameTreeNode.builder()
                .board(board.clone())
                .children(new ArrayList<>())
                .currentSide(currentSide)
                .terminalState(terminalState)
                .build();
    }

    public GameTreeNode updateRootNode(final Board board, final GameTreeNode tree, final Side ourSide, Protocol.MoveTurn moveTurn) { // BFS
        final Queue<GameTreeNode> nodesToVisit = new LinkedBlockingQueue<>();
        final HashSet<GameTreeNode> visitedNodes = new HashSet<>();

        nodesToVisit.add(tree);

        while (!nodesToVisit.isEmpty()) {
            final GameTreeNode visitingNode = nodesToVisit.remove();

            if (this.isFoundAndNotRootOrFoundAndRootAndOurSide(board, tree, ourSide, visitingNode)) {
                return visitingNode;
            }


            visitingNode.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(child -> !visitedNodes.contains(child))
                    .forEach(nodesToVisit::add);


            visitedNodes.add(visitingNode);
        }

        // the node was not found at a depth > 0. Return the current root if the move was in the past else null.
        if (this.moveWasInThePast(board, tree)) {
            if (moveTurn.ourTurn){
                return this.generateRootNodeWithoutLoading(ourSide, board, ourSide.equals(Side.SOUTH));
            }
            else {
                return this.generateRootNodeWithoutLoading(ourSide, board, ourSide.opposite().equals(Side.SOUTH));
            }
        }
        return tree;
    }

    private boolean isFoundAndNotRootOrFoundAndRootAndOurSide(final Board board, final GameTreeNode tree, final Side ourSide, final GameTreeNode visitingNode) {
        if (visitingNode.getBoard().equals(board)) {
            if (visitingNode.equals(tree)) {
                return visitingNode.getCurrentSide().equals(ourSide);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean moveWasInThePast(final Board board, final GameTreeNode tree) {
        return tree.getBoard().getSeedsInStore(Side.SOUTH) < board.getSeedsInStore(Side.SOUTH)
                || tree.getBoard().getSeedsInStore(Side.NORTH) < board.getSeedsInStore(Side.NORTH);
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

    public UpdateReturnable updateGameTree(final Board board, GameTreeNode tree, final Side ourSide, Protocol.MoveTurn moveTurn) {
        try {
            final Thread thread;
            tree = this.updateRootNode(board, tree, ourSide, moveTurn);

            final Runnable createTreeRunner = new TreeGenerator(tree, this.relativeOverallDepth - 1, ourSide);
            thread = new Thread(createTreeRunner);
            thread.start();
            return new UpdateReturnable(tree, thread);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return new UpdateReturnable(tree, null);
    }
}
