package org.AIandGames.mancalabot;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class GeneratorRunnable implements Runnable {
    private GameTreeNode root;
    private int depth;
    private boolean allowSwap;


    @Override
    public void run() {
        try {
            root.generateChildren(this.depth, allowSwap);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
