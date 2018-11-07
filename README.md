# mancala-bot
A bot to play Mancala 7-7, written for COMP34120 (The University Of Manchester). This was the first group project for the course.

# From the Instructions:

The usage is:

java -jar ManKalah.jar \<agent1> \<agent2>

where \<agent1> and \<agent2> are programs which play the game. These could both be your bot, for example, or one could be yours and one could be one of your friends. We provide you with several agents, one is called 'MKRefAgent.jar'. The agents are entered as strings, enclosed in quotes.

The game-playing agents interact with the game engine via a protocol which is described informally in /opt/info/courses/COMP34120/Project1_2018/protocol/Protocol-info.txt, and in Backus-Naur form in /opt/info/courses/COMP34120/Project1_2018/protocol.txt.

One way to test your knowledge of the protocol is to play the game against the reference agent. You could do this as follows:

1.       Open a window (e.g. using xterm) and type in this window

nc -l localhost 12345

2.       Start the game engine

java -jar ManKalah.jar "nc localhost 12345" "java -jar MKRefAgent.jar"

You can play in the xterm against the provided agent (who you will probably be able to beat) and you will have to use the protocol commands to communicate with the game engine. Likewise, you can play two humans against each other by providing different ports (type nc -l localhost 12345 in one window and nc -l localhost 12346 in the other), or play MKRefAgent against itself in the obvious way.

The game engine contains a time-out mechanism which will end the game when one agent is taking too long (to avoid infinite loops). When this happens, the other agent is declared winner. The time-out period is one hour in total for each player. The game engine also ends the game when one agent attempts an illegal move or sends an illegal message.

Instructions found here: http://syllabus.cs.manchester.ac.uk/ugt/2018/COMP34120/COMP34120Project1.htm
