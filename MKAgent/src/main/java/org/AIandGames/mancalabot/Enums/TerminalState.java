package org.AIandGames.mancalabot.Enums;

public enum TerminalState {
    WIN_TERMINAL((byte) 1),
    LOSE_TERMINAL((byte) -1),
    NON_TERMINAL((byte) 0);

    TerminalState(final byte terminalState) {
    }
}
