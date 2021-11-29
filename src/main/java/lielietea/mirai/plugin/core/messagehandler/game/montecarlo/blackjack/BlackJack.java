package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack;

import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.SenoritaCounter;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackData;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackPlayer;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackOperation;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackPhase;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.Color;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.*;

public class BlackJack extends BlackJackUtils{

    static final String BlackJackRules = "本局二十一点已经开始,请在一分钟之内下注。";
    static final String BlackJackStops = "本局二十一点已经取消。";
    static final String NotRightBetNumber = "下注的金额不正确，请重新再尝试";
    static final String YouDontHaveEnoughMoney = "操作失败，请检查您的南瓜比索数量。";
    static final String StartBetNotice = "下注阶段已经开始，预计在60秒内结束。可以通过/bet+金额反复追加下注。";
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
            getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalFriendData)).setPhase(bjp);
        }
    }

    public static void changeBet(MessageEvent event, int additionalBet){
        if (event.getClass().equals(GroupMessageEvent.class)) {
            int overwriteBet = additionalBet + getINSTANCE().globalGroupData.get(indexInTheList(event, getINSTANCE().globalGroupData)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
            getINSTANCE().globalGroupData.get(indexInTheList(event, getINSTANCE().globalGroupData)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBet(overwriteBet);
        } else {
            int overwriteBet = additionalBet + getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalFriendData)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
            getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalFriendData)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBet(overwriteBet);
        }
    }

    //对话框中输入/blackjack或者二十一点
    public static void checkBlackJack(MessageEvent event){
        if(isBlackJack(event)){

            //进行判定是Friend还是Group
            if(isGroupMessage(event)) {
                if (!isInTheList(event, getINSTANCE().globalGroupData)) {
                    getINSTANCE().globalGroupData.add(new BlackJackData(event.getSubject().getId()));
                    event.getSubject().sendMessage(BlackJackRules);
                    cancelInSixtySeconds(event,getINSTANCE().globalGroupData);
                }
            } else {
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
            Integer bet = getBet(event);
            if (bet!= null) {

                //判定账户里是否有钱
                if (hasEnoughMoney(event, bet)) {

                    //进行判定是Friend还是Group
                    if (isGroupMessage(event)) {
                        //如果这个群blackjack状态都没进入过，可以直接通过/bet来进入游戏状态
                        if (!isInTheList(event, getINSTANCE().globalGroupData)) {
                            getINSTANCE().globalGroupData.add(new BlackJackData(event.getSubject().getId()));
                            event.getSubject().sendMessage(BlackJackRules);
                        }
                        //将当前的Phase改为下注阶段Bet
                        changePhase(event,BlackJackPhase.Bet);
                        //如果没有第一次进入过下注阶段，那么给List加一个flag，设置定时关闭任务
                        //召唤庄家
                        //下注阶段在第一个人下注后60秒关闭
                        if(!isInBetProcess.contains(event.getSubject().getId())){
                            event.getSubject().sendMessage(StartBetNotice);
                            isInBetProcess.add(event.getSubject().getId());
                            //召唤庄家
                            addBookmaker(event);
                            //定时任务
                            endBetInSixtySeconds(event);
                        }
                        //扣钱
                        SenoritaCounter.minusMoney(event,bet);
                        //如果已经有了则追加写入
                        if(playerIsInTheList(event)){
                            changeBet(event,bet);
                        } else {
                            //写入赌注
                            addNewPlayer(event,bet);
                        }

                    } else {
                        //FriendMessageEvent 开始
                        //如果blackjack状态都没进入过，可以直接通过/bet来进入游戏状态
                        if (!isInTheList(event, getINSTANCE().globalFriendData)) {
                            getINSTANCE().globalFriendData.add(new BlackJackData(event.getSubject().getId()));
                            event.getSubject().sendMessage(BlackJackRules);
                        }
                        //将当前的Phase改为下注阶段Bet
                        changePhase(event,BlackJackPhase.Bet);
                        //扣钱
                        SenoritaCounter.minusMoney(event,bet);
                        //召唤庄家
                        addBookmaker(event);
                        //写入赌注
                        addNewPlayer(event,bet);
                        //进入发牌操作
                        cardShuffle(event);
                        dealCards(event);
                        showTheCards(event);
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
                cardShuffle(event);
                dealCards(event);
                showTheCards(event);
            }
        },60*1000);
    }

    //查看用户在列表里第几个
    public static Integer indexOfThePlayer(MessageEvent event) {
        return indexOfThePlayer(event,event.getSender().getId());
    }

    //查看用户在列表里第几个
    public static Integer indexOfThePlayer(MessageEvent event, long ID){
        return indexOfThePlayer(getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalGroupData)).getBlackJackPlayerList(),ID);
    }


    //查看用户是否在该群的列表里
    public static boolean playerIsInTheList(MessageEvent event) {
        return playerIsInTheList(getINSTANCE().globalFriendData.get(indexInTheList(event, getINSTANCE().globalGroupData)).getBlackJackPlayerList(),event.getSender().getId());
    }

    //查看全局列表里是几号
    public static Integer indexInTheList(MessageEvent event){
        if (isGroupMessage(event)) {
            return indexInTheList(event, getINSTANCE().globalGroupData);
        } else {
            return indexInTheList(event, getINSTANCE().globalFriendData);
        }
    }

    //添加新玩家
    public static void addNewPlayer(MessageEvent event, int bet){
        if(isGroupMessage(event)) {
            getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList().add(new BlackJackPlayer(event.getSender().getId(), bet));
        } else {
            getINSTANCE().globalFriendData.get(indexInTheList(event)).getBlackJackPlayerList().add(new BlackJackPlayer(event.getSender().getId(), bet));
        }
    }

    //添加庄家
    public static void addBookmaker(MessageEvent event){
        getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList().add(new BlackJackPlayer(true));
    }

    //发牌操作
    public static void cardShuffle(MessageEvent event){
        if(isGroupMessage(event)) {
            getINSTANCE().globalGroupData.get(indexInTheList(event)).setCardPile(createPokerPile());
        } else {
            getINSTANCE().globalFriendData.get(indexInTheList(event)).setCardPile(createPokerPile());
        }
    }

    //翻牌操作
    public static void dealCards(MessageEvent event){
        if (isGroupMessage(event)) {
            int playerNumber = getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList().size();
            int index = 0;
            for (BlackJackPlayer bjp : getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList()) {
                List<Integer> cardList = new ArrayList<>();
                //抽两张牌，一张是当张，一张是当张+人数（包括庄家）
                cardList.add(getINSTANCE().globalGroupData.get(indexInTheList(event)).getCardPile().get(index));
                cardList.add(getINSTANCE().globalGroupData.get(indexInTheList(event)).getCardPile().get(index + playerNumber));
                //塞牌
                getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCards(cardList);
                index += 1;
            }
            //设置已经被抽出来的张数
            getINSTANCE().globalGroupData.get(indexInTheList(event)).setCardnumber(playerNumber * 2);
        } else {
            for (int index=0;index<2;index++){
                List<Integer> cardList = new ArrayList<>();
                //抽两张牌，一张是当张，一张是当张+人数（包括庄家）
                cardList.add(getINSTANCE().globalFriendData.get(indexInTheList(event)).getCardPile().get(index));
                cardList.add(getINSTANCE().globalFriendData.get(indexInTheList(event)).getCardPile().get(index + 2));
                getINSTANCE().globalFriendData.get(indexInTheList(event)).getBlackJackPlayerList().get(index).setCards(cardList);
            }
        }
    }

    //告知玩家牌面情况
    public static void showTheCards(MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if(isGroupMessage(event)){
            mcb.append("抽牌情况如下：");
            for (BlackJackPlayer bjp : getINSTANCE().globalGroupData.get(indexInTheList(event)).getBlackJackPlayerList()){
                if (bjp.getID()==0){
                    mcb.append("\n\n").append("庄家的牌是：");
                    mcb.append("\n").append(getPoker(bjp.getCards().get(0))).append("\n❓❓");
                } else {
                    mcb.append("\n\n").append((new At(event.getSender().getId()))).append("的牌是：");
                    for (Integer card : bjp.getCards()) {
                        mcb.append("\n").append(getPoker(card));
                    }
                }
            }
        //FriendMessageEvent
        } else {
            mcb.append("抽牌情况如下：");
            for (BlackJackPlayer bjp : getINSTANCE().globalFriendData.get(indexInTheList(event)).getBlackJackPlayerList()){
                if (bjp.getID()==0){
                    mcb.append("\n\n").append("庄家的牌是：");
                    mcb.append("\n").append(getPoker(bjp.getCards().get(0))).append("\n❓❓");
                } else {
                    mcb.append("\n\n").append("你的牌是：");
                    for (Integer card : bjp.getCards()) {
                        mcb.append("\n").append(getPoker(card));
                    }
                }
            }
        }

        event.getSubject().sendMessage(mcb.asMessageChain());
    }

}
