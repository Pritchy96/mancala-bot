package org.AIandGames.mancalabot.Heuristics;

import org.AIandGames.mancalabot.Enums.Side;
import org.AIandGames.mancalabot.Move;

import java.util.Arrays;
import java.util.List;

public class MovesStaticList {

    private static final Move M_1_S = new Move(Side.SOUTH, 1);
    private static final Move M_2_S = new Move(Side.SOUTH, 2);
    private static final Move M_3_S = new Move(Side.SOUTH, 3);
    private static final Move M_4_S = new Move(Side.SOUTH, 4);
    private static final Move M_5_S = new Move(Side.SOUTH, 5);
    private static final Move M_6_S = new Move(Side.SOUTH, 6);
    private static final Move M_7_S = new Move(Side.SOUTH, 7);

    private static final Move M_1_N = new Move(Side.NORTH, 1);
    private static final Move M_2_N = new Move(Side.NORTH, 2);
    private static final Move M_3_N = new Move(Side.NORTH, 3);
    private static final Move M_4_N = new Move(Side.NORTH, 4);
    private static final Move M_5_N = new Move(Side.NORTH, 5);
    private static final Move M_6_N = new Move(Side.NORTH, 6);
    private static final Move M_7_N = new Move(Side.NORTH, 7);

    public final static List<Move> MOVES_LIST = Arrays.asList(M_1_N, M_2_N, M_3_N, M_4_N, M_5_N, M_6_N, M_7_N,
            M_1_S, M_2_S, M_3_S, M_4_S, M_5_S, M_6_S, M_7_S);


}
