package org.AIandGames.mancalabot;

import lombok.*;


@AllArgsConstructor
public class GeneratorRunnable implements Runnable {
    private GameTreeNode root;
    private int depth;


    @Override
    public void run() {
        try {
            root.generateChildren(this.depth);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
