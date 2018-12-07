package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class MessageHelper {
    private final Reader input;
    private final PrintWriter output;


    public MessageHelper(final Reader input, final PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void sendMsg(final String msg) {
        System.out.print(msg);
        System.out.flush();

        this.output.print(msg);
        this.output.flush();
    }

    public void sendSwapMsg() {
        System.out.print(Protocol.createSwapMsg());
        System.out.flush();

        this.output.print(Protocol.createSwapMsg());
        this.output.flush();
    }

    public String recvMsg() throws IOException {
        final StringBuilder message = new StringBuilder();
        int newCharacter;

        do {
            newCharacter = this.input.read();
            if (newCharacter == -1)
                throw new EOFException("Input ended unexpectedly.");
            message.append((char) newCharacter);
        } while ((char) newCharacter != '\n');

        return message.toString();
    }
}
