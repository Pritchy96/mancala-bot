package org.AIandGames.mancalabot;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * The main application class. It also provides methods for communication
 * with the game engine.
 */
public class Main
{
    /**
     * Input from the game engine.
     */
    private static Reader input = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Sends a message to the game engine.
     * @param msg The message.
     */
    public static void sendMsg (String msg)
    {
    	System.out.print(msg);
    	System.out.flush();
    }

    /**
     * Receives a message from the game engine. Messages are terminated by
     * a '\n' character.
     * @return The message.
     * @throws IOException if there has been an I/O error.
     */
    public static String recvMsg() throws IOException
    {
    	StringBuilder message = new StringBuilder();
    	int newCharacter;

    	do
    	{
    		newCharacter = input.read();
    		if (newCharacter == -1)
    			throw new EOFException("Input ended unexpectedly.");
    		message.append((char)newCharacter);
    	} while((char)newCharacter != '\n');

		return message.toString();
    }

	/**
	 * The main method, invoked when the program is started.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args)
	{
        System.err.println("Starting Test Bot");

		try
		{
			String s;
			while (true)
			{
				System.err.println();
				s = recvMsg();
				System.err.print("Received: " + s);
				try {
					org.AIandGames.mancalabot.MsgType mt = org.AIandGames.mancalabot.Protocol.getMessageType(s);
					switch (mt)
					{
						case START: System.err.println("A start.");
							boolean first = org.AIandGames.mancalabot.Protocol.interpretStartMsg(s);
							System.err.println("Starting player? " + first);
							break;
						case STATE: System.err.println("A state.");
							org.AIandGames.mancalabot.Board b = new org.AIandGames.mancalabot.Board(6,6);
							org.AIandGames.mancalabot.Protocol.MoveTurn r = org.AIandGames.mancalabot.Protocol.interpretStateMsg (s, b);
							System.err.println("This was the move: " + r.move);
							System.err.println("Is the game over? " + r.end);
							if (!r.end) System.err.println("Is it our turn again? " + r.again);
							System.err.print("The board:\n" + b);
							break;
						case END: System.err.println("An end. Bye bye!"); return;
					}

				} catch (org.AIandGames.mancalabot.InvalidMessageException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		catch (IOException e)
		{
			System.err.println("This shouldn't happen: " + e.getMessage());
		}

	}
}
