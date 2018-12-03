package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Protocol.MoveTurn;
import org.AIandGames.mancalabot.exceptions.InvalidMessageException;
import org.AIandGames.mancalabot.helpers.MessageHelper;
import org.AIandGames.mancalabot.helpers.StatePrinter;
import org.AIandGames.mancalabot.helpers.TreeGenerator;
import org.AIandGames.mancalabot.helpers.TreeHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class GameRunner {
    private PrintWriter output;
    private Reader input;
    private Boolean wePlayFirst = false;
    private Boolean opponentWentLast = true;
    private GameTreeNode tree = null;
    private long ourMoveCount = 0;
    private Side ourSide;
    private Thread thread = new Thread();
    private MessageHelper messageHelper;
    private final StatePrinter statePrinter = new StatePrinter();
    private final TreeHelper treeHelper = new TreeHelper();


    private void setupSocketServer() {
        try {
            /*
      Input from the game engine.
     */ /**
             * Input from the game engine.
             */ //The actual server expects the client to be running and waiting, and java sockets
            //...expect the server to be running and waiting... Set up a Server that just listens
            //...so the client and server don't time out as a result.
            ServerSocket server = new ServerSocket(12345);
            Socket clientSocket = server.accept();
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            messageHelper = new MessageHelper(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run() {
        Board board = new Board(7, 7);

        setupSocketServer();

        String msg;
        while (true) {
            try {
                msg = messageHelper.recvMsg();

                switch (Protocol.getMessageType(msg)) {

                    case START:
                        runStartCase(msg, thread);
                        break;

                    case STATE:
                        runStateCase(msg, board, thread);
                        break;

                    case END:
                        System.err.println("The end.");
                        return;
                }
            } catch (InvalidMessageException | IOException | CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void runStateCase(String msg, Board board, Thread thread) throws InvalidMessageException, CloneNotSupportedException {
        MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);

        if (opponentWentLast && moveTurn.move == -1) {
            ourSide = ourSide.opposite();
            ourMoveCount--;
        }

        // is it not our turn?
        if (!moveTurn.ourTurn) {
            statePrinter.printCurrentState(board, opponentWentLast, ourMoveCount, moveTurn);
            System.err.println("Not our turn - continuing to make tree");
            System.err.println("||-------------------------------------||\n");
            opponentWentLast = true;
        } else {
            try {
                thread.join();
                tree = treeHelper.checkTree(tree, board);
                System.err.println("tree depth: " + treeHelper.getMaxDepthOfTree(Arrays.asList(tree)));
//                List<GameTreeNode> pHead = tree.getChildren();
//                int depthChild = tree.getDepth();
//                while( !pHead.isEmpty() ) {
//                    depthChild++;
//                    final Optional<GameTreeNode> first = pHead.stream().filter(Objects::nonNull).findFirst();
//
//                    if (first.isPresent()) {
//                        pHead = first.get().getChildren();
//                    } else {
//                        pHead = new ArrayList<>();
//                    }
//                }
//
//                System.err.println("child depth: " + depthChild);
//                System.err.println("depth diff: " + (depthChild - tree.getDepth()));

                statePrinter.printCurrentState(board, opponentWentLast, ourMoveCount, moveTurn);
                Kalah testKalah = new Kalah(board);


                if (canWeSwap() && shouldWeSwap()) {
                    performSwap();
                } else {
                    moveAsNormal(testKalah);
                }

                ourMoveCount++;
                thread = treeHelper.updateGameTree(board, tree);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean shouldWeSwap() {
        return true; // for now
    }

    private boolean canWeSwap() {
        return !wePlayFirst && ourMoveCount == 0;
    }

    private void runStartCase(String msg, Thread thread) throws InvalidMessageException, CloneNotSupportedException {
        wePlayFirst = Protocol.interpretStartMsg(msg);

        ourSide = statePrinter.printStartMessage(wePlayFirst);
        tree = treeHelper.generateRootNode(ourSide, wePlayFirst);

        if (wePlayFirst) {
            messageHelper.sendMsg(Protocol.createMoveMsg(7), opponentWentLast);
            ourMoveCount++;
        }

        if (!thread.isAlive()) {
            Runnable createTreeRunner = new TreeGenerator(tree, 8, true);
            thread = new Thread(createTreeRunner);
            thread.start();
        }
    }

    private void performSwap() {
        messageHelper.sendSwapMsg();
        ourSide = ourSide.opposite();
        wePlayFirst = true;
        System.err.println("We swapped to :: " + ourSide);
        System.err.println("||-------------------------------------||\n");
    }

    private void moveAsNormal(Kalah testKalah) {
        for (int i = 7; i > 0; i--) {
            Move testMove = new Move(ourSide, i);
            if (testKalah.isLegalMove(testMove)) {
                messageHelper.sendMsg(Protocol.createMoveMsg(i), opponentWentLast);
                testKalah.makeMove(testMove);
                System.err.println("We play hole :: " + i);
                System.err.println("||-------------------------------------||\n");
                break;
            }
        }
    }

}
