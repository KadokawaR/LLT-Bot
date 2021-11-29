package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data;

import lielietea.mirai.plugin.utils.IdentityUtil;

import java.util.ArrayList;
import java.util.List;

public class BlackJackPlayer {
    long ID;
    List<Integer> cards;
    int bet;
    boolean betPair;
    boolean hasSplit;
    boolean canOperate;

    //生成普通玩家
    public BlackJackPlayer(long id, int betNumber){
        ID = id;
        cards = new ArrayList<>();
        bet = betNumber;
        betPair = false;
        hasSplit = false;
        canOperate = false;
    }

    //生成庄家
    public BlackJackPlayer(boolean bookmaker){
        ID = 0;
        cards = new ArrayList<>();
        bet = 0;
        betPair = false;
        hasSplit = false;
        canOperate = false;
    }
}
