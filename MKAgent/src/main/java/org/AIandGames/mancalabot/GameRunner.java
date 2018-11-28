package org.AIandGames.mancalabot;

import org.AIandGames.mancalabot.Protocol.MoveTurn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

class GameRunner {
    private Reader input;
    private PrintWriter output;
    private Boolean wePlayFirst = false;
    private Boolean opponentWentLast = true;
    private GameTreeNode tree = null;
    private MoveTurn moveTurn = null;
    private long ourMoveCount = 0;
    private Side ourSide;
    private Thread thread = new Thread();
    private Stack<Board> opponentMoveStack = new Stack<>();


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
    }

    private void sendSwapMsg() {
        System.out.print(Protocol.createSwapMsg());
        System.out.flush();

        output.print(Protocol.createSwapMsg());
        output.flush();
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

    private void runStateCase(String msg, Board board, Thread thread) throws InvalidMessageException, CloneNotSupportedException {
        moveTurn = Protocol.interpretStateMsg(msg, board);

        if (opponentWentLast && moveTurn.move == -1) {
            ourSide = ourSide.opposite();
            ourMoveCount--;
        }

        // is it not our turn?
        if (!moveTurn.ourTurn) {
            //opponentMoveStack.push(board.clone());
            printCurrentState(board);
            System.err.println("Not our turn - continuing to make tree");
            System.err.println("||-------------------------------------||\n");
            opponentWentLast = true;
        } else {
            try {
                thread.join();

                // here board has the current state of the game board after the opp move
                // check if our tree's root is in the correct position
                // else move the root of the tree to the correct position
                // if it is not in the tree assume it was a double move that got us here so just ignore.
                checkTree(board);


                printCurrentState(board);

                //updateGameTreeFromOppMoves(board);

                Kalah testKalah = new Kalah(board);


                if (canWeSwap() && shouldWeSwap()) {
                    performSwap();
                } else {
                    moveAsNormal(testKalah);
                }

                updateGameTree(board);

                ourMoveCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkTree(Board board) { // BFS
        final Queue<GameTreeNode> nodesToVisit = new LinkedBlockingQueue<>();
        final HashSet<GameTreeNode> visitedNodes = new HashSet<>();

        nodesToVisit.add(tree);

        while (!nodesToVisit.isEmpty()) {
            final GameTreeNode visitingNode = nodesToVisit.remove();

            if (visitingNode.getBoard().equals(board)) {
                tree = visitingNode;
                tree.setParent(null);
                return;
            }

            visitingNode.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(child -> !visitedNodes.contains(child))
                    .forEach(nodesToVisit::add);

            visitedNodes.add(visitingNode);
        }

    }

    private void updateGameTreeFromOppMoves(Board board) {
        try {

            while (!opponentMoveStack.empty()) {
                final Board oppMove = opponentMoveStack.pop();

                tree = tree.getChildren().stream()
                        .filter(Objects::nonNull)
                        .filter(child -> child.getBoard().equals(oppMove))
                        .findFirst()
                        .orElseThrow(Exception::new);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGameTree(Board board)  {

        try {
            final GameTreeNode newRoot = tree.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(child -> child.getBoard().equals(board))
                    .findFirst()
                    .orElseThrow(Exception::new);

            newRoot.setDepth(0);
            tree = newRoot;
            TreeGenerator tg = new TreeGenerator(tree, 6, false);
            thread = new Thread(tg);
            thread.start();
            newRoot.setParent(null);


        } catch (Exception e) {
            e.printStackTrace();
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
            ourMoveCount++;
        }

        if (!thread.isAlive()) {
            Runnable createTreeRunner = new TreeGenerator(tree, 6, true);
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
                .currentSide(ourSide.opposite())
                .depth(0)
                .parent(null)
                .playersTurn(wePlayFirst)
                .build();
    }

    private void moveAsNormal(Kalah testKalah) {
        for (int i = 7; i > 0; i--) {
            Move testMove = new Move(ourSide, i);
            if (testKalah.isLegalMove(testMove)) {
                sendMsg(Protocol.createMoveMsg(i));
                testKalah.makeMove(testMove);
                System.err.println("We play hole :: " + i);
                System.err.println("||-------------------------------------||\n");
                break;
            }
        }
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
