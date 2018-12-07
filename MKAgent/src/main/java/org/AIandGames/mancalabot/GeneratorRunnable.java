package org.AIandGames.mancalabot;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class GeneratorRunnable implements Runnable {
    private final GameTreeNode root;
    private final int depth;
    private final boolean allowSwap;


    @Override
    public void run() {
        try {
            this.root.generateChildren(this.depth, this.allowSwap);
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
