package org.AIandGames.mancalabot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The main application class. It also provides methods for communication
 * with the game engine.
 */
public class Main {
    /**
     * Input from the game engine.
     */

    //The actual server expects the client to be running and waiting, and java sockets
    //...expect the server to be running and waiting... Set up a Server that just listens
    //...so the client and server don't time out as a result.
    private static ServerSocket server;
    private static Socket clientSocket;
    private static Reader input;
    private static PrintWriter output;

    /**
     * Sends a message to the game engine.
     *
     * @param msg The message.
     */
    public static void sendMsg(String msg) {
        System.out.print(msg);
        System.out.flush();

        output.print(msg);
        output.flush();
    }

    /**
     * Receives a message from the game engine. Messages are terminated by
     * a '\n' character.
     *
     * @return The message.
     * @throws IOException if there has been an I/O error.
     */
    public static String recvMsg() throws IOException {
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

    public static void setupSocketServer() {
        try {
            server = new ServerSocket(12345); // Setup server on localhost port 12345
            clientSocket = server.accept(); // client socket on port 12345
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method, invoked when the program is started.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Thread thread = new Thread();
        setupSocketServer();

        String msg;
        Side ourSide = Side.NORTH;
        boolean wePlayFirst = false;
        boolean opponentWentLast = true; //no other way to explicitly track who went last from server output?

        Board board = new Board(7, 7);
        Protocol.MoveTurn moveTurn;
        GameTreeNode tree = null;

        while (true) {
            try {
                msg = recvMsg();
                MsgType msgType = Protocol.getMessageType(msg);

                switch (msgType) {

                    case START:

                        wePlayFirst = Protocol.interpretStartMsg(msg);
                        ourSide = printStartMessage(ourSide, wePlayFirst);

                        tree = generateRootNode(msg, ourSide);

                        if (!thread.isAlive()) {
                            Runnable createTreeRunner = new TreeGenerator(tree, 6);
                            thread = new Thread(createTreeRunner);
                            thread.start();
                        }

                        break;

                    case STATE:
                        moveTurn = Protocol.interpretStateMsg(msg, board);

                        // is it not our turn?
                        if (!moveTurn.ourTurn) {
                            printCurrentState(opponentWentLast, board, moveTurn);
                            opponentWentLast = true;
                            System.err.println("Not our turn - continuing to make tree");
                            System.err.println("||-------------------------------------||\n");
                        } else {
                            try {
                                thread.join();

                                Kalah testKalah = new Kalah(board);

                                printCurrentState(opponentWentLast, board, moveTurn);

                                if (!wePlayFirst) {
                                    sendMsg(Protocol.createSwapMsg());
                                    ourSide = ourSide.opposite();
                                    wePlayFirst = true;
                                    System.err.println("We swapped to :: " + ourSide);
                                    System.err.println("||-------------------------------------||\n");
                                    break;
                                }

                                opponentWentLast = moveAsNormal(ourSide, opponentWentLast, moveTurn, testKalah);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

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

    private static GameTreeNode generateRootNode(String msg, Side ourSide) throws InvalidMessageException, CloneNotSupportedException {
        GameTreeNode tree;
        Board boardInit = new Board(7, 7);
        boolean moveTurnInit = Protocol.interpretStartMsg(msg);

        tree = GameTreeNode.builder()
                .board(boardInit.clone())
                .children(new ArrayList<>())
                .currentSide(ourSide)
                .depth(0)
                .parent(null)
                .playersTurn(moveTurnInit)
                .build();
        return tree;
    }

    private static boolean moveAsNormal(Side ourSide, boolean opponentWentLast, Protocol.MoveTurn moveTurn, Kalah testKalah) {
        for (int i = 7; i > 0; i--) {
            Move testMove = new Move(ourSide, i);
            if (testKalah.isLegalMove(testMove)) {
                sendMsg(Protocol.createMoveMsg(i));
                opponentWentLast = false;
                System.err.println("We play hole :: " + i);
                System.err.println("||-------------------------------------||\n");
                break;
            }
        }
        return opponentWentLast;
    }

    private static void printCurrentState(boolean opponentWentLast, Board board, Protocol.MoveTurn moveTurn) {
        System.err.println("||----------------STATE----------------||");
        if (Kalah.gameWon(board))
            System.err.println("We've already reached a terminal node!");
        if (opponentWentLast)
            System.err.println("Opponent played last with hole :: " + moveTurn.move);
        else
            System.err.println("We played last with hole :: " + moveTurn.move);
        System.err.println("The board ::\n " + board);
    }

    private static Side printStartMessage(Side ourSide, boolean wePlayFirst) {
        if (wePlayFirst) {
            ourSide = Side.SOUTH;
        }
        System.err.println("||--------------GAME START-------------||");
        System.err.println("Us to go first :: " + wePlayFirst);
        System.err.println("We are :: " + ourSide);
        return ourSide;
    }


}
