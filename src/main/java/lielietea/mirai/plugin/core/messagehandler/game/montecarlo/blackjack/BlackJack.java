package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack;

import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.SenoritaCounter;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackData;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackOperation;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackPhase;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.Color;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;

public class BlackJack extends BlackJackUtils{

    static final String BlackJackRules = "本局二十一点已经开始,请在一分钟之内下注。";
    static final String BlackJackStops = "本局二十一点已经取消。";
    static final String NotRightBetNumber = "下注的金额不正确，请重新再尝试";
    static final String YouDontHaveEnoughMoney = "操作失败，请检查您的南瓜比索数量。";
    static final String EndBetNotice = "下注阶段已经结束。";

    static List<Long> isInBetProcess = new ArrayList<>();

    private static final BlackJack INSTANCE;

    static {
        INSTANCE = new BlackJack();
    }

    public static BlackJack getINSTANCE() {
        return INSTANCE;
    }

    public List<BlackJackData> globalGroupData;
    public List<BlackJackData> globalFriendData;

    static final Timer blackjackCancelTimer = new Timer(true);
    static final Timer endBetTimer = new Timer(true);

    //更改列表里面的状态
    public static void changePhase(MessageEvent event, BlackJackPhase bjp){
        if (event.getClass().equals(GroupMessageEvent.class)) {
            getINSTANCE().globalGroupData.get(indexInTheList(event, getINSTANCE().globalGroupData)).setPhase(bjp);
        } else {
            getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalGroupData)).setPhase(bjp);
        }
    }

    //对话框中输入/blackjack或者二十一点
    public static void checkBlackJack(MessageEvent event){
        if(isBlackJack(event)){

            //进行判定是Friend还是Group
            if(event.getClass().equals(GroupMessageEvent.class)) {
                if (!isInTheList(event, getINSTANCE().globalGroupData)) {
                    getINSTANCE().globalGroupData.add(new BlackJackData(event.getSubject().getId()));
                    event.getSubject().sendMessage(BlackJackRules);
                    cancelInSixtySeconds(event,getINSTANCE().globalGroupData);
                }
            }

            if(event.getClass().equals(FriendMessageEvent.class)) {
                if (!isInTheList(event,getINSTANCE().globalFriendData)){
                    getINSTANCE().globalFriendData.add(new BlackJackData(event.getSubject().getId()));
                    event.getSubject().sendMessage(BlackJackRules);
                    cancelInSixtySeconds(event,getINSTANCE().globalFriendData);
                }
            }
        }
    }

    //60秒之内如果没有进入下一阶段就自动取消
    public static void cancelInSixtySeconds(MessageEvent event,List<BlackJackData> globalData){
        blackjackCancelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Integer index = indexInTheList(event,globalData);
                if (index != null){
                    if(!globalData.get(index).getPhase().equals(BlackJackPhase.Callin)) {
                        event.getSubject().sendMessage(BlackJackStops);
                        if (GroupMessageEvent.class.equals(event.getClass())) {
                            getINSTANCE().globalGroupData.remove(index);
                        } else  {
                            getINSTANCE().globalFriendData.remove(index);
                        }
                    }
                } else {event.getSubject().sendMessage(BlackJackStops);};
            }
        },60*1000);
    }


    //对话框中输入/bet或者下注
    public static void checkBet(MessageEvent event) {
        if (isBet(event)) {
            //判定数值是否正确
            if (getBet(event) != null) {

                //判定账户里是否有钱
                if (hasEnoughMoney(event, getBet(event))) {

                    //进行判定是Friend还是Group
                    if (event.getClass().equals(GroupMessageEvent.class)) {
                        //如果这个群blackjack状态都没进入过，可以直接通过/bet来进入游戏状态
                        if (!isInTheList(event, getINSTANCE().globalGroupData)) {
                            getINSTANCE().globalGroupData.add(new BlackJackData(event.getSubject().getId()));
                            event.getSubject().sendMessage(BlackJackRules);
                        }
                        //将当前的Phase改为下注阶段Bet
                        changePhase(event,BlackJackPhase.Bet);
                        //如果没有第一次进入过下注阶段，那么给List加一个flag，设置定时关闭任务
                        //下注阶段在第一个人下注后60秒关闭
                        if(!isInBetProcess.contains(event.getSubject().getId())){
                            isInBetProcess.add(event.getSubject().getId());
                            //定时任务
                            endBetInSixtySeconds(event);
                        }
                    }

                    if (event.getClass().equals(FriendMessageEvent.class)) {
                        if (!isInTheList(event, getINSTANCE().globalFriendData)) {

                        }
                    }

                } else {
                    event.getSubject().sendMessage(YouDontHaveEnoughMoney);
                }
            } else {
                event.getSubject().sendMessage(NotRightBetNumber);
            }
        }
    }

    //下注阶段的定时任务
    public static void endBetInSixtySeconds(MessageEvent event){
        endBetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getSubject().sendMessage(EndBetNotice);
                //删除该flag
                isInBetProcess.remove(event.getSubject().getId());
                //更改状态
                changePhase(event,BlackJackPhase.Operation);
                //进入发牌操作
            }
        },60*1000);
    }




}
