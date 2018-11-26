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
        while (true) {
            try {
                msg = recvMsg();
                MsgType msgType = Protocol.getMessageType(msg);
                switch (msgType) {
                    case START:
                        System.err.println("Game start");
                        boolean first = Protocol.interpretStartMsg(msg);
                        System.err.println("Us to go first :: " + first);
                        break;
                    case STATE:
                        System.err.println("State");
                        Board board = new Board(7,7);
                        Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board);
                        System.err.println("The move :: " + moveTurn.move);
                        System.err.println("End of game :: " + moveTurn.end);
                        if (!moveTurn.end) {
                            System.err.println("Our turn :: " + moveTurn.again);
                        }
                        System.err.println("The board ::\n " + board);
                        break;
                    case END:
                        System.err.println("The end.");
                        break;
                }
            } catch (InvalidMessageException ime) {
                ime.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
