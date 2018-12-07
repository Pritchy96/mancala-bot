package org.AIandGames.mancalabot.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.AIandGames.mancalabot.GameTreeNode;

@AllArgsConstructor
@Getter
public class UpdateReturnable {
    private final GameTreeNode gameTreeNode;
    private final Thread thread;
}
