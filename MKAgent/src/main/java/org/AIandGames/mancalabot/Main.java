package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Enums.TerminalState;
import org.apache.commons.collections4.ListUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

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
        setupSocketServer();

        String msg;
        Side ourSide = Side.NORTH;
        boolean wePlayFirst = false;
        boolean opponentWentLast = true; //no other way to explicitly track who went last from server output?

        while (true) {
            try {

                msg = recvMsg();
                MsgType msgType = Protocol.getMessageType(msg);

                switch (msgType) {

                    case START:

                        wePlayFirst = Protocol.interpretStartMsg(msg);
                        ourSide = printStartMessage(ourSide, wePlayFirst);
                        break;

                    case STATE:

                        Board board = new Board(7, 7);
                        Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);
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
                        break;

                    case END:

                        System.err.println("The end.");
                        return;
                }
            } catch (InvalidMessageException | IOException ime) {
                ime.printStackTrace();
            }
        }
    }

    private static boolean moveAsNormal(Side ourSide, boolean opponentWentLast, Protocol.MoveTurn moveTurn, Kalah testKalah) {
        if (!moveTurn.end && moveTurn.again) {
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
        } else if (!moveTurn.end && !moveTurn.again) {
            opponentWentLast = true;
            System.err.println("||-------------------------------------||\n");
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
