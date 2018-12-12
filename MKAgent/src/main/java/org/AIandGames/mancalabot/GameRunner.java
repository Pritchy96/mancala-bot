package org.AIandGames.mancalabot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Enums.TerminalState;
import org.AIandGames.mancalabot.Protocol.MoveTurn;
import org.AIandGames.mancalabot.exceptions.InvalidMessageException;
import org.AIandGames.mancalabot.helpers.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;


public class GameRunner {
    private static final int OVERALL_DEPTH = 10;
    private static final Boolean WRITE_TREE = false;
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
    private int totalMovesBothPlayers = 0;
    private final int depthOfStaticTree = 4;


    private void setupSocketServer() {
        try {
            /*
      Input from the game engine.
     */ /**
             * Input from the game engine.
             */ //The actual server expects the client to be running and waiting, and java sockets
            //...expect the server to be running and waiting... Set up a Server that just listens
            //...so the client and server don't time out as a result.
            final ServerSocket server = new ServerSocket(12345);
            final Socket clientSocket = server.accept();
            this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.output = new PrintWriter(clientSocket.getOutputStream(), true);
            this.messageHelper = new MessageHelper(this.input, this.output);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    void run() {
        final Board board = new Board(7, 7);

        this.setupSocketServer();

        String msg;
        while (true) {
            try {
                msg = this.messageHelper.recvMsg();

                switch (Protocol.getMessageType(msg)) {

                    case START:
                        this.runStartCase(msg, board);
                        break;

                    case STATE:
                        this.runStateCase(msg, board);
                        break;

                    case END:
                        this.statePrinter.printEndState();
                        return;
                }
            } catch (final InvalidMessageException | IOException | CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void runStateCase(final String msg, final Board board) throws InvalidMessageException {
        final MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);

        if (this.opponentWentLast && moveTurn.move == Protocol.SWAP) {
            this.ourSide = this.ourSide.opposite();
            this.ourMoveCount--;
        }

        // is it not our turn?
        if (!moveTurn.ourTurn) {
            this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
            this.opponentWentLast = true;
            this.totalMovesBothPlayers++;
        } else {

            if (this.totalMovesBothPlayers > this.depthOfStaticTree) {
                try {
                    this.thread.join();
                    this.tree = this.treeHelper.updateRootNode(board, this.tree, this.ourSide);
                    this.makeAMove(board, moveTurn);
                    final UpdateReturnable returnable = this.treeHelper.updateGameTree(board, this.tree, this.ourSide);
                    this.thread = returnable.getThread();
                    this.tree = returnable.getGameTreeNode();
                } catch (final InterruptedException | CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            } else { // static tree
                this.makeAMove(board, moveTurn);

                if (this.totalMovesBothPlayers >= this.depthOfStaticTree) {
                    if (!this.thread.isAlive()) {
                        try {
                            this.tree = this.treeHelper.generateRootNode(this.ourSide, board);
                            final Runnable createTreeRunner = new TreeGenerator(this.tree, OVERALL_DEPTH, true, this.ourSide);
                            this.thread = new Thread(createTreeRunner);
                            this.thread.start();
                        } catch (final CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void makeAMove(final Board board, final MoveTurn moveTurn) {
        this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
        final Kalah testKalah = new Kalah(board);


        if (this.canWeSwap() && this.shouldWeSwap(moveTurn)) {
            this.performSwap();
        } else if (this.totalMovesBothPlayers > this.depthOfStaticTree) {
            //If there is only one valid move available, make it without doing any checks.
            final List<GameTreeNode> childrenNoNulls = this.tree.getChildren();
            childrenNoNulls.removeAll(Collections.singleton(null));
            if (childrenNoNulls.size() == 1) {
                this.makeMoveIfLegal(new Move(this.ourSide, childrenNoNulls.get(0).getHoleNumber()), testKalah);
            }
            // Tries to make the best guess move, if its not legal, defaults to right most pot.
            else if (this.tree.getTerminalState() != TerminalState.NON_TERMINAL) {
                this.moveRightMostPot(testKalah);
            } else if (!this.moveBestGuess(testKalah)) {
                this.statePrinter.printBestGuessError();
                this.moveRightMostPot(testKalah);
            }
            this.opponentWentLast = false;
        } else {
            this.moveRightMostPot(testKalah);
        }

        this.ourMoveCount++;
        this.totalMovesBothPlayers++;
    }

    private boolean shouldWeSwap(MoveTurn moveTurn) {
        return (moveTurn.move >= 4);
    }

    private boolean canWeSwap() {
        return !this.wePlayFirst && this.ourMoveCount == 0;
    }

    private void runStartCase(final String msg, final Board board) throws InvalidMessageException, CloneNotSupportedException {
        this.wePlayFirst = Protocol.interpretStartMsg(msg);

        this.ourSide = this.statePrinter.printStartMessage(this.wePlayFirst);

        if (this.wePlayFirst) {
            this.messageHelper.sendMsg(Protocol.createMoveMsg(7));
            // TODO Whats our best opening move ?
            this.opponentWentLast = false;
            this.ourMoveCount++;
            this.totalMovesBothPlayers++;
        }

        if (WRITE_TREE) {
            System.err.println("Writing Tree to tree.json");
            try (final Writer writer = new FileWriter("tree.json")) {
                final Gson gson = new GsonBuilder().create();
                gson.toJson(this.tree, writer);
                writer.close();
                System.exit(0);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void performSwap() {
        this.messageHelper.sendSwapMsg();
        this.ourSide = this.ourSide.opposite();
        this.wePlayFirst = true;
        this.statePrinter.printPerformedSwap(this.ourSide);
    }

    private void moveRightMostPot(final Kalah testKalah) {
        for (int i = 7; i > 0; i--) {
            final Move testMove = new Move(this.ourSide, i);
            if (this.makeMoveIfLegal(testMove, testKalah)) {
                break;
            }
        }
    }

    private boolean moveBestGuess(final Kalah kalah) {
        final Move bestGuess = this.tree.getBestMove(this.ourSide);
        this.statePrinter.printBestMoveGuess(bestGuess);
        return bestGuess != null && this.makeMoveIfLegal(bestGuess, kalah);
    }

    private boolean makeMoveIfLegal(final Move move, final Kalah kalah) {
        if (kalah.isLegalMove(move)) {
            kalah.makeMove(move);
            this.statePrinter.printLegalMoveMade(move);
            this.messageHelper.sendMsg(Protocol.createMoveMsg(move.getHole()));
            this.opponentWentLast = false;
            return true;
        }
        return false;
    }
}
