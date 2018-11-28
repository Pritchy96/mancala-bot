package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class MessageHelper {
    private Reader input;
    private PrintWriter output;


    public MessageHelper(Reader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void sendMsg(String msg, Boolean opponentWentLast) {
        System.out.print(msg);
        System.out.flush();

        output.print(msg);
        output.flush();
        opponentWentLast = false;
    }

    public void sendSwapMsg() {
        System.out.print(Protocol.createSwapMsg());
        System.out.flush();

        output.print(Protocol.createSwapMsg());
        output.flush();
    }

    public String recvMsg() throws IOException {
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
}
