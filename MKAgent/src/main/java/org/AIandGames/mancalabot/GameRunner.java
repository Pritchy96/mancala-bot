package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Enums.TerminalState;
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
    private static final int OVERALL_DEPTH = 9;
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
    private final TreeHelper treeHelper = new TreeHelper(OVERALL_DEPTH);


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

    private void runStateCase(String msg, Board board, Thread thread) throws InvalidMessageException {
        MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);

        if (opponentWentLast && moveTurn.move == Protocol.SWAP) {
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

                statePrinter.printCurrentState(board, opponentWentLast, ourMoveCount, moveTurn);
                Kalah testKalah = new Kalah(board);


                if (canWeSwap() && shouldWeSwap()) {
                    performSwap();
                } else {
                    // Tries to make the best guess move, if its not legal, defaults to right most pot.
                    if (tree.getTerminalState() != TerminalState.NON_TERMINAL) {
                        moveRightMostPot(testKalah);
                    }
                    else if (!moveBestGuess(testKalah)) {
                        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.err.println("OUR BEST GUESS IS NOT LEGAL! Big Problem! - Playing right most pot");
                        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        moveRightMostPot(testKalah);
                    }
                    opponentWentLast = false;
                 }

                ourMoveCount++;
                thread = treeHelper.updateGameTree(board, tree);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean shouldWeSwap() {
        return false; // for now
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
            // TODO Whats our best opening move ?
            ourMoveCount++;
        }

        if (!thread.isAlive()) {
            Runnable createTreeRunner = new TreeGenerator(tree, OVERALL_DEPTH, true);
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

    private void moveRightMostPot(Kalah testKalah) {
        for (int i = 7; i > 0; i--) {
            Move testMove = new Move(ourSide, i);
            if (makeMoveIfLegal(testMove, testKalah)) {
                break;
            }
        }
    }

    private boolean moveBestGuess(Kalah kalah) {
        Move bestGuess = tree.getBestMove(ourSide);
        System.err.println("Our best guess is :: " + bestGuess);
        if (bestGuess != null && makeMoveIfLegal(bestGuess, kalah)) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean makeMoveIfLegal(Move move, Kalah kalah) {
        if (kalah.isLegalMove(move)) {
            System.err.println("We Made this move :: " + move);
            messageHelper.sendMsg(Protocol.createMoveMsg(move.getHole()), opponentWentLast);
            return true;
        }
        return false;
    }
}
