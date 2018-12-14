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
    private final static int DEPTH_OF_STATIC_MOVES = 2;


    private void setupServerIO() {
        if (USE_SOCKETS) {
            try {
                /* Input from the game engine. */
                //The actual server expects the client to be running and waiting, and java sockets
                //...expect the server to be running and waiting... Set up a Server that just listens
                //...so the client and server don't time out as a result.
                final ServerSocket server = new ServerSocket(12345);
                final Socket clientSocket = server.accept();
                this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.output = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (final IOException e) {
                e.printStackTrace();
            } 
        } else {
            this.input = new BufferedReader(new InputStreamReader(System.in));
            this.output = new PrintWriter(new OutputStreamWriter(System.out));
        }
         this.messageHelper = new MessageHelper(this.input, this.output, USE_SOCKETS);
    }

    void run() {
        final Board board = new Board(7, 7);

        this.setupServerIO();

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
            } catch (final InvalidMessageException | IOException  e) {
                e.printStackTrace();
            }
        }
    }

    private void runStateCase(final String msg, final Board board) throws InvalidMessageException {
        final MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);

        if (hasOpponentSwapped(moveTurn)) {
            opponentHasSwapped();
        }

        // is it opponents turn?
        if (!moveTurn.ourTurn) {
            // TODO: Check if we can update the tree at this point, including pruning
            this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
            this.opponentWentLast = true;
            this.totalMovesBothPlayers++;
        } else if (this.canWeSwap() && this.shouldWeSwap(moveTurn)) {
            this.performSwap();
        }
        else {
            try {
                // Flow:
                // Wait for thread join
                // find root node
                // prune to that node
                // update generate to depth OVERALL_DEPTH
                // Wait for thread join
                // perform minimax
                // Make move

                this.thread.join();

                // Will update root node to correct place (BFS)
                // then generates the last level of depth, waiting for thread to join before continuing.
                ensureCorrectTreeDepth(board);

                this.makeAMove(board, moveTurn);

            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeAMove(final Board board, final MoveTurn moveTurn) {
        this.statePrinter.printCurrentState(board, this.opponentWentLast, this.ourMoveCount, moveTurn);
        final Kalah kalah = new Kalah(board);

        final List<GameTreeNode> childrenNoNulls = this.tree.getChildren();
        childrenNoNulls.removeAll(Collections.singleton(null));
        if (childrenNoNulls.size() == 1) {
            System.err.println("one non null child");
            this.makeMoveIfLegal(new Move(this.ourSide, childrenNoNulls.get(0).getHoleNumber()), kalah);
        }
        else if (this.tree.getTerminalState() != TerminalState.NON_TERMINAL) {
            System.err.println("Terminal state");
            this.moveRightMostPot(kalah);
        }
        else if (!this.moveBestGuess(kalah)) {
            this.statePrinter.printBestGuessError();
            this.moveRightMostPot(kalah);
        }
        this.opponentWentLast = false;
        this.ourMoveCount++;
        this.totalMovesBothPlayers++;
    }

    private void runStartCase(final String msg, final Board board) throws InvalidMessageException {
        this.wePlayFirst = Protocol.interpretStartMsg(msg);

        if (wePlayFirst) {
            this.ourSide = Side.SOUTH;
        } else {
            this.ourSide = Side.NORTH;
        }

        this.statePrinter.printStartMessage(this.wePlayFirst, this.ourSide);

        if (this.wePlayFirst) {
            this.makeStartCaseFirstMoveOfGame();
        }

        // In the start case, regardless of who goes first, a move is made.
        this.totalMovesBothPlayers++;

        if (WRITE_TREE) {
            writeTreeToFile();
        }

        // GENERATE TREE
        generateInitialTree(board);
    }

    private void generateInitialTree(Board board) {
        this.tree = this.treeHelper.generateRootNode(this.ourSide, board);
        final Runnable createTreeRunner = new TreeGenerator(this.tree, OVERALL_DEPTH, this.ourSide);
        this.thread = new Thread(createTreeRunner);
        this.thread.start();
    }

    private void ensureCorrectTreeDepth(Board board) throws InterruptedException {
        final UpdateReturnable returnable = this.treeHelper.updateGameTree(board, this.tree, this.ourSide);
        this.thread = returnable.getThread();
        this.tree = returnable.getGameTreeNode();
        this.thread.join();
    }

    private boolean hasOpponentSwapped(MoveTurn moveTurn) {
        return this.opponentWentLast && moveTurn.move == Protocol.SWAP;
    }

    private void opponentHasSwapped() {
        this.ourSide = this.ourSide.opposite();
        this.ourMoveCount--;
        // TODO: Check move count variables are correct.
    }

    private void makeStartCaseFirstMoveOfGame() {
        this.messageHelper.sendMsg(Protocol.createMoveMsg(4));
        this.opponentWentLast = false;
        this.ourMoveCount++;
    }

    private boolean shouldWeSwap(MoveTurn moveTurn) {
        return (moveTurn.move >= 4); // TODO Should we consider 3? Run against a few bots to see how it does.
        //return true;
    }

    private boolean canWeSwap() {
        return this.ourMoveCount == 0 && !this.wePlayFirst;
    }

    private void performSwap() {
        this.messageHelper.sendSwapMsg();
        this.ourSide = this.ourSide.opposite();
        this.wePlayFirst = true;
        this.statePrinter.printPerformedSwap(this.ourSide);
    }

    private void moveRightMostPot(final Kalah kalah) {
        for (int i = 7; i > 0; i--) {
            final Move testMove = new Move(this.ourSide, i);
            if (this.makeMoveIfLegal(testMove, kalah)) {
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

    private void writeTreeToFile() {
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
