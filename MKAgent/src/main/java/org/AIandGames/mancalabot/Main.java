package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Enums.TerminalState;
import org.apache.commons.collections4.ListUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
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

    /**
     * The main method, invoked when the program is started.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        Board board = new Board(7, 7);
        TreeGenerator generator = new TreeGenerator();

        GameTreeNode root = GameTreeNode.builder()
                .terminalState(TerminalState.NON_TERMINAL)
                .currentSide(Side.SOUTH)
                .parent(null)
                .depth(0)
                .board(board.clone())
                .children(new ArrayList<>())
                .hValues(null)
                .playersTurn(true)
                .value(0)
                .build();


        final long initTime = System.currentTimeMillis();
        generator.generateTree(root, 9);
        final long afterTime = System.currentTimeMillis();
        System.out.println("Time: " + (afterTime - initTime));
    }
}
