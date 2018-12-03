package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Protocol.MoveTurn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class GameRunner {
    private Reader input;
    private PrintWriter output;
    private Boolean wePlayFirst = false;
    private Boolean opponentWentLast = true;
    private GameTreeNode tree = null;
    private MoveTurn moveTurn = null;
    private long ourMoveCount = 0;
    private Side ourSide;


    /**
     * Sends a message to the game engine.
     *
     * @param msg The message.
     */
    private void sendMsg(String msg) {
        System.out.print(msg);
        System.out.flush();

        output.print(msg);
        output.flush();
        opponentWentLast = false;

        System.err.println("||-------------------------------------||\n");
    }

    private void sendSwapMsg() {
        String swapMessage = Protocol.createSwapMsg();
        System.out.print(swapMessage);
        System.out.flush();

        output.print(swapMessage);
        output.flush();

        System.err.println("||-------------------------------------||\n");
    }

    /**
     * Receives a message from the game engine. Messages are terminated by
     * a '\n' character.
     *
     * @return The message.
     * @throws IOException if there has been an I/O error.
     */
    private String recvMsg() throws IOException {
        StringBuilder message = new StringBuilder();
        int newCharacter;

        do {
            newCharacter = input.read();
            if (newCharacter == -1)
                throw new EOFException("Input ended unexpectedly.");
            message.append((char) newCharacter);
        } while ((char) newCharacter != '\n');

        return message.toString();
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void run() {
        Thread thread = new Thread();
        Board board = new Board(7, 7);

        setupSocketServer();

        String msg;
        while (true) {
            try {
                msg = recvMsg();

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
        moveTurn = Protocol.interpretStateMsg(msg, board);


        if (opponentWentLast && moveTurn.move == -1) {
            ourSide = ourSide.opposite();
            ourMoveCount--;
        }


        // is it not our turn?
        if (!moveTurn.ourTurn) {
            printCurrentState(board);
            System.err.println("Not our turn - continuing to make tree");
            System.err.println("||-------------------------------------||\n");
            opponentWentLast = true;
        } else {
            try {
                thread.join();

                printCurrentState(board);

                Kalah testKalah = new Kalah(board);
                if (canWeSwap() && shouldWeSwap()) {
                    performSwap();
                } else {
                    // Tries to make the best guess move, if its not legal, defaults to right most pot.
                    if (!moveBestGuess(testKalah)) {
                        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.err.println("OUR BEST GUESS IS NOT LEGAL! Big Problem! - Playing right most pot");
                        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        moveRightMostPot(testKalah);
                    }
                }
                ourMoveCount++;
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

        printStartMessage();
        generateRootNode();

        if (wePlayFirst) {
            sendMsg(Protocol.createMoveMsg(7));
            // TODO Whats our best opening move ?
            ourMoveCount++;
        }

        if (!thread.isAlive()) {
            Runnable createTreeRunner = new TreeGenerator(tree, 6);
            thread = new Thread(createTreeRunner);
            thread.start();
        }
    }

    private void performSwap() {
        sendSwapMsg();
        ourSide = ourSide.opposite();
        wePlayFirst = true;
        System.err.println("We swapped to :: " + ourSide);
        System.err.println("||-------------------------------------||\n");
    }

    private void generateRootNode() throws CloneNotSupportedException {
        Board boardInit = new Board(7, 7);

        tree = GameTreeNode.builder()
                .board(boardInit.clone())
                .children(new ArrayList<>())
                .currentSide(ourSide)
                .depth(0)
                .parent(null)
                .playersTurn(wePlayFirst)
                .build();
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
        Move bestGuess = tree.getBestMove();
        if (makeMoveIfLegal(bestGuess, kalah)) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean makeMoveIfLegal(Move move, Kalah kalah) {
        if (kalah.isLegalMove(move)) {
            sendMsg(Protocol.createMoveMsg(move.getHole()));
            return true;
        }
        return false;

    }

    private void printCurrentState(Board board) {
        System.err.println("||----------------STATE----------------||");
        if (Kalah.gameWon(board))
            System.err.println("We've already reached a terminal node!");
        if (opponentWentLast)
            System.err.println("Opponent played last with hole :: " + moveTurn.move
                    + "\nnumber of moves we have made: " + ourMoveCount);
        else
            System.err.println("We played last with hole :: " + moveTurn.move
                    + "\nnumber of moves we have made: " + ourMoveCount);
        System.err.println("The board ::\n " + board);
    }

    private void printStartMessage() {
        if (wePlayFirst) {
            ourSide = Side.SOUTH;
        } else {
            ourSide = Side.NORTH;
        }
        System.err.println("||--------------GAME START-------------||");
        System.err.println("Us to go first :: " + wePlayFirst);
        System.err.println("We are :: " + ourSide);
        System.err.println("||-------------------------------------||\n");
    }

}
