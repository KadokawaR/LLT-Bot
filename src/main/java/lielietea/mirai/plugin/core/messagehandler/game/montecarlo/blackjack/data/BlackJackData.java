package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data;

import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackPhase;

import java.util.ArrayList;
import java.util.List;

public class BlackJackData {
    long ID; // 如果是好友消息则为好友ID
    List<BlackJackPlayer> blackJackPlayerList;
    BlackJackPhase phase;
    int cardnumber;

    public long getID() {
        return ID;
    }

    public List<BlackJackPlayer> getBlackJackPlayerList() {
        return blackJackPlayerList;
    }

    public BlackJackPhase getPhase() {
        return phase;
    }

    public int getCardnumber() {
        return cardnumber;
    }

    //群聊内有人触发，进入Callin阶段
    public BlackJackData(long id){
        ID = id;
        blackJackPlayerList = new ArrayList<>();
        phase = BlackJackPhase.Callin;
        cardnumber=0;
    }

    //群聊内有人下注，进入Bet环节
    public BlackJackData(long groupid, long playerid, int bet){
        BlackJackPlayer blackJackPlayer = new BlackJackPlayer(playerid,bet);
        ID = groupid;
        blackJackPlayerList = new ArrayList<>();
        blackJackPlayerList.add(blackJackPlayer);
        phase = BlackJackPhase.Bet;
        cardnumber=0;
    }

    //好友对话内下注，进入Bet环节
    public BlackJackData(long playerid, int bet){
        BlackJackPlayer blackJackPlayer = new BlackJackPlayer(playerid,bet);
        ID = playerid;
        blackJackPlayerList = new ArrayList<>();
        blackJackPlayerList.add(blackJackPlayer);
        phase = BlackJackPhase.Bet;
        cardnumber=0;
    }

    public void setPhase(BlackJackPhase ph) {
        phase = ph;
    }
}
