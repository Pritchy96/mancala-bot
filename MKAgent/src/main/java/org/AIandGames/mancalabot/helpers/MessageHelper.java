package org.AIandGames.mancalabot.helpers;

import org.AIandGames.mancalabot.Protocol;

import lombok.AllArgsConstructor;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

@AllArgsConstructor
public class MessageHelper {
    private final Reader input;
    private final PrintWriter output;

    public void sendMsg(final String msg) {
        this.output.print(msg);
        this.output.flush();
    }

    public void sendSwapMsg() {
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
