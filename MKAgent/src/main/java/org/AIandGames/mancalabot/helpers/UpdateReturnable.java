package org.AIandGames.mancalabot.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.AIandGames.mancalabot.GameTreeNode;

@AllArgsConstructor
@Getter
public class UpdateReturnable {
    private GameTreeNode gameTreeNode;
    private Thread thread;
}
