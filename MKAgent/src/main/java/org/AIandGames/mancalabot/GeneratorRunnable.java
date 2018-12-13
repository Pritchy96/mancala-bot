package org.AIandGames.mancalabot;

import lombok.AllArgsConstructor;
import org.AIandGames.mancalabot.Enums.Side;


@AllArgsConstructor
public class GeneratorRunnable implements Runnable {
    private final GameTreeNode root;
    private final int depth;
    private final Side ourSide;


    @Override
    public void run() {
        try {
            this.root.generateChildren(this.depth, this.ourSide);
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
