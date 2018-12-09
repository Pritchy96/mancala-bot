package org.AIandGames.mancalabot;

import lombok.extern.java.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Protocol.MoveTurn;
import org.AIandGames.mancalabot.exceptions.InvalidMessageException;
import org.AIandGames.mancalabot.helpers.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

@Log
public class GameRunner {
    private static final int OVERALL_DEPTH = 9;
    private static final Boolean WRITE_TREE = false;
    private static final boolean USE_SOCKETS = true;
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
    private static final int DEPTH_OF_STATIC_TREE = 4;


    private void setupServerIO() throws IOException {
        if (USE_SOCKETS) {
          /*Input from the game engine.*/
          //The actual server expects the client to be running and waiting, and java sockets
          //...expect the server to be running and waiting... Set up a Server that just listens
          //...so the client and server don't time out as a result.

        final Socket clientSocket;
        try (finalServerSocket server = new ServerSocket(12345)) {
         clientSocket = server.accept();}
        this. input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this. output = new PrintWriter(clientSocket.getOutputStream(), true);} else {
            this.input = new BufferedReader(new InputStreamReader(System.in));
            this.output = new PrintWriter(new OutputStreamWriter(System.out));
        }
        this.messageHelper = new MessageHelper(this.input, this.output, USE_SOCKETS);
    }

    void run() throws IOException, InvalidMessageException {
        final Board board = new Board(7, 7);

        this.setupServerIO();

        String msg;
        while (true) {
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
        }
    }

    private void runStateCase(final String msg, final Board board) throws InvalidMessageException {
        final MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);

        if (this.opponentSwapped(moveTurn)) {
            this.ourSide = this.ourSide.opposite();
            this.ourMoveCount--;
        }

        // is it not our turn?
        if (!moveTurn.ourTurn) {
            this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
            this.opponentWentLast = true;
            this.totalMovesBothPlayers++;
        } else {
            if (this.useDynamicTree()) {
                try {
                    this.thread.join();
                } catch (final InterruptedException e) {
                    this.thread.interrupt();
                    log.severe(e.getMessage());
                }
                this.tree = this.treeHelper.updateRootNode(board, this.tree);
                this.makeAMove(board, moveTurn);
                final UpdateReturnable updateReturnable = this.treeHelper.updateGameTree(board, this.tree);
                this.thread = updateReturnable.getThread();
                this.tree = updateReturnable.getGameTreeNode();
            } else { // use static tree
                this.makeAMove(board, moveTurn);
                if (this.endOfStaticTree() && !this.thread.isAlive()) {
                    this.tree = this.treeHelper.generateRootNode(this.ourSide, board);
                    final Runnable createTreeRunner = new TreeGenerator(this.tree, OVERALL_DEPTH, true, this.ourSide);
                    this.thread = new Thread(createTreeRunner);
                    this.thread.start();
                }
            }
        }
    }

    private boolean endOfStaticTree() {
        return this.totalMovesBothPlayers >= DEPTH_OF_STATIC_TREE;
    }

    private boolean useDynamicTree() {
        return this.totalMovesBothPlayers > DEPTH_OF_STATIC_TREE;
    }

    private boolean opponentSwapped(final MoveTurn moveTurn) {
        return this.opponentWentLast && moveTurn.move == Protocol.SWAP;
    }

    private void makeAMove(final Board board, final MoveTurn moveTurn) {
        this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
        final Kalah testKalah = new Kalah(board);


        if (this.canWeSwap() && this.shouldWeSwap(moveTurn)) {
            this.performSwap();
        } else if (this.useDynamicTree()) {
            //If there is only one valid move available, make it without doing any checks.
            final List<GameTreeNode> childrenNoNulls = this.tree.getChildren();
            childrenNoNulls.removeAll(Collections.singleton(null));
            if (childrenNoNulls.size() == 1) {
                this.makeMoveIfLegal(new Move(this.ourSide, childrenNoNulls.get(0).getHoleNumber()), testKalah);
            }
            // Tries to make the best guess move, if its not legal, defaults to right most pot.
            else if (Kalah.gameWon(board)) {
                this.moveRightMostPot(testKalah);
                this.treeHelper.setOverallDepth(1);
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

    private void runStartCase(final String msg, final Board board) throws InvalidMessageException {
        this.wePlayFirst = Protocol.interpretStartMsg(msg);

        this.ourSide = this.wePlayFirst ? Side.SOUTH : Side.NORTH;
        this.statePrinter.printStartMessage(this.wePlayFirst, this.ourSide);

        if (this.wePlayFirst) {
            this.messageHelper.sendMsg(Protocol.createMoveMsg(4));
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
