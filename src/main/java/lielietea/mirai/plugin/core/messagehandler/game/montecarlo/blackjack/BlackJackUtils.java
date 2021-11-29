package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack;

import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.BancoDeEspana;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.SenoritaCounter;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.CasinoCroupier;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackData;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackPlayer;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackOperation;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackPhase;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.Color;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.graalvm.compiler.phases.Phase;

import java.util.*;

public class BlackJackUtils {



    //判定消息里面是否有触发关键词
    public static boolean isBlackJack(MessageEvent event){
        return event.getMessage().contentToString().equals("/blackjack")||event.getMessage().contentToString().equals("二十一点");
    }

    //判定是否是下注
    public static boolean isBet(MessageEvent event){
        return event.getMessage().contentToString().equals("/bet")||event.getMessage().contentToString().equals("下注");
    }

    //判定是哪种操作
    public static BlackJackOperation bjOperation(MessageEvent event){
        switch(event.getMessage().contentToString()){
            case "/assurance":
            case "/Assurance":
            case "买保险":
                return BlackJackOperation.Assurance;
            case "/deal":
            case "/Deal":
            case "要牌":
                return BlackJackOperation.Deal;
            case "/double":
            case "/Double":
            case "双倍下注":
                return BlackJackOperation.Double;
            case "/fold":
            case "/Fold":
            case "停牌":
                return BlackJackOperation.Fold;
            case "/pair":
            case "/Pair":
            case "下注对子":
                return BlackJackOperation.Pair;
            case "/split":
            case "/Split":
            case "分牌":
                return BlackJackOperation.Split;
            case "/surrender":
            case "/Surrender":
            case "投降":
                return BlackJackOperation.Surrender;
        }
        return null;
    }

    //返回下注的钱，不行返回null
    public static Integer getBet(MessageEvent event){
        String message = event.getMessage().contentToString();
        message = message.replace(" ","");
        if (message.contains("/bet")){
            message = message.replace("/bet","");
        }
        if (message.contains("下注")){
            message = message.replace("下注","");
        }
        try {
            return Integer.parseInt(message);
        }
        catch(NumberFormatException e){
            e.printStackTrace();
        }
        return null;
    }

    //生成四幅扑克牌组成的序列
    public static List<Integer> createPokerPile(){
        List<Integer> list = new ArrayList<>();
        for(int i=1;i<=52*4;i++){ list.add(i); }
        Collections.shuffle(list);
        return list;
    }

    //获得扑克牌的花色
    public static Color getColor(Integer integer){
        return Color.values()[integer%4];
    }

    //获得扑克牌的数字
    public static Character getNumber(Integer integer){
        int number = integer%13;
        if (number<=10 && number>=2) return (char)number;
        switch (number){
            case 1: return 'A';
            case 11: return 'J';
            case 12: return 'Q';
            case 0: return 'K';
        }
        return null;
    }

    //组合成扑克牌字符串
    public static String getPoker(Integer integer){
        String poker = "";
        switch(getColor(integer)){
            case Club: poker += "♣"; break;
            case Heart: poker += "♥"; break;
            case Spade: poker += "♠"; break;
            case Diamond: poker += "♦"; break;
        }
        return poker+getNumber(integer);
    }

    //查看列表里是否有相应号码
    public static boolean isInTheList(MessageEvent event,List<BlackJackData> globalData){
        for (BlackJackData bjd : globalData){
            if (bjd.getID()==event.getSubject().getId()){
                return true;
            }
        }
        return false;
    }

    //查看全局列表里是几号
    public static Integer indexInTheList(MessageEvent event,List<BlackJackData> globalData){
        int index = 0;
        for (BlackJackData bjd : globalData){
            if (bjd.getID()==event.getSubject().getId()){
                return index;
            }
            index += 1;
        }
        return null;
    }

    //判定是否有钱
    public static boolean hasEnoughMoney(MessageEvent event, int bet){
        return SenoritaCounter.hasEnoughMoney(event,bet);
    }

    //查看用户在列表里第几个
    public static Integer indexOfThePlayer(List<BlackJackPlayer> blackJackPlayerList, long ID){
        int index = 0;
        for (BlackJackPlayer bjp : blackJackPlayerList){
            if (bjp.getID()==ID){
                return index;
            }
            index += 1;
        }
        return null;
    }

    //查看用户是否在该群的列表里
    public static boolean playerIsInTheList(List<BlackJackPlayer> blackJackPlayerList, long ID){
        for (BlackJackPlayer bjp : blackJackPlayerList){
            if (bjp.getID()==ID){
                return true;
            }
        }
        return false;
    }

    //是不是群聊
    public static boolean isGroupMessage(MessageEvent event){
        return (event.getClass().equals(GroupMessageEvent.class));
    }
}
