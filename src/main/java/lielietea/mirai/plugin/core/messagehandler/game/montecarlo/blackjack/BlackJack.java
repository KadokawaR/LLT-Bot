package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.BancoDeEspana;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.SenoritaCounter;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackData;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.data.BlackJackPlayer;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.blackjack.enums.BlackJackPhase;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.*;

public class BlackJack extends BlackJackUtils{

    static final String BlackJackRules = "里格斯公司邀请您参与本局二十一点,请在60秒之内下注。";
    static final String BlackJackStops = "本局二十一点已经取消。";
    static final String NotRightBetNumber = "下注指令不正确，请重新再尝试";
    static final String YouDontHaveEnoughMoney = "操作失败，请检查您的南瓜比索数量。";
    static final String StartBetNotice = "下注阶段已经开始，预计在60秒内结束。可以通过/bet+金额反复追加下注。";
    static final String EndBetNotice = "下注阶段已经结束。";
    static final String StartOperateNotice = "现在可以进行操作。功能列表如下：\n\n要牌 or /deal\n双倍下注 or /double\n停牌 or /fold\n下注对子 or /pair\n分牌 or /split\n买保险 or /assurance\n投降 or /surrender\n";
    static final String BustNotice = "您爆牌了。";
    static final String EndGameNotice = "本局游戏已经结束，感谢您的参与。如下为本局玩家的获得金额：";

    List<Long> isInBetProcess = new ArrayList<>();

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
    static final Timer endOperationTimer = new Timer(true);

    //更改列表里面的状态
    public static void changePhase(MessageEvent event, BlackJackPhase bjp){
        if (event.getClass().equals(GroupMessageEvent.class)) {
            getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).setPhase(bjp);
        } else {
            getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).setPhase(bjp);
        }
    }

    //更改赌注的金额
    public static void changeBet(MessageEvent event, int additionalBet){
        if (event.getClass().equals(GroupMessageEvent.class)) {
            int overwriteBet = additionalBet + getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
            getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBet(overwriteBet);
        } else {
            int overwriteBet = additionalBet + getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
            getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBet(overwriteBet);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //对话框中输入/blackjack或者二十一点
    public static void checkBlackJack(MessageEvent event){
        if(isBlackJack(event)){

            //进行判定是Friend还是Group
            if(isGroupMessage(event)) {
                if (!isInTheList(event, getGlobalData(event))) {
                    getGlobalData(event).add(new BlackJackData(event.getSubject().getId()));
                    event.getSubject().sendMessage(BlackJackRules);
                    cancelInSixtySeconds(event,getGlobalData(event));
                }
            } else {
                if (!isInTheList(event,getGlobalData(event))){
                    getGlobalData(event).add(new BlackJackData(event.getSubject().getId()));
                    event.getSubject().sendMessage(BlackJackRules);
                    cancelInSixtySeconds(event,getGlobalData(event));
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
                        getGlobalData(event).remove(getGlobalData(event).get(index));
                    }
                } else {event.getSubject().sendMessage(BlackJackStops);}
            }
        },60*1000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
                        if (!isInTheList(event, getGlobalData(event))) {
                            getGlobalData(event).add(new BlackJackData(event.getSubject().getId()));
                            event.getSubject().sendMessage(BlackJackRules);
                        }
                        //将当前的Phase改为下注阶段Bet
                        changePhase(event,BlackJackPhase.Bet);
                        //如果没有第一次进入过下注阶段，那么给List加一个flag，设置定时关闭任务
                        //下注阶段在第一个人下注后60秒关闭
                        if(!getINSTANCE().isInBetProcess.contains(event.getSubject().getId())){
                            event.getSubject().sendMessage(StartBetNotice);
                            getINSTANCE().isInBetProcess.add(event.getSubject().getId());
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

                        //FriendMessageEvent 开始
                    } else {
                        //如果blackjack状态都没进入过，可以直接通过/bet来进入游戏状态
                        if (!isInTheList(event, getGlobalData(event))) {
                            getGlobalData(event).add(new BlackJackData(event.getSubject().getId()));
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
                    MessageChainBuilder mcb = mcbProcessor(event);
                    mcb.append(YouDontHaveEnoughMoney);
                    event.getSubject().sendMessage(mcb.asMessageChain());
                }
            } else {
                MessageChainBuilder mcb = mcbProcessor(event);
                mcb.append(NotRightBetNumber);
                event.getSubject().sendMessage(mcb.asMessageChain());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //下注阶段的定时任务
    public static void endBetInSixtySeconds(MessageEvent event){
        endBetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getSubject().sendMessage(EndBetNotice);
                //删除该flag
                getINSTANCE().isInBetProcess.remove(event.getSubject().getId());
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
        return indexOfThePlayer(getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList(),ID);
    }

    //查看用户在列表里第几个
    public static Integer indexOfTheBookMaker(MessageEvent event){
        return indexOfThePlayer(getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList(),0);
    }


    //查看用户是否在该群的列表里
    public static boolean playerIsInTheList(MessageEvent event) {
        return playerIsInTheList(getGlobalData(event).get(indexInTheList(event, getGlobalData(event))).getBlackJackPlayerList(),event.getSender().getId());

    }

    //查看全局列表里是几号
    public static Integer indexInTheList(MessageEvent event){
        return indexInTheList(event, getGlobalData(event));
    }

    //添加新玩家
    public static void addNewPlayer(MessageEvent event, int bet){
        getGlobalData(event).get(indexInTheList(event)).addBlackJackPlayerList(new BlackJackPlayer(event.getSender().getId(), bet));
    }

    //添加庄家
    public static void addBookmaker(MessageEvent event){
        getGlobalData(event).get(indexInTheList(event)).addBlackJackPlayerList(new BlackJackPlayer(true));

    }

    //发牌操作
    public static void cardShuffle(MessageEvent event){
        getGlobalData(event).get(indexInTheList(event)).setCardPile(createPokerPile());
    }

    //翻牌操作
    public static void dealCards(MessageEvent event){
        int playerNumber = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().size();
        for (int index=0;index<playerNumber;index++) {
            List<Integer> cardList = new ArrayList<>();
            //抽两张牌，一张是当张，一张是当张+人数（包括庄家）
            cardList.add(getGlobalData(event).get(indexInTheList(event)).getCardPile().get(index));
            cardList.add(getGlobalData(event).get(indexInTheList(event)).getCardPile().get(index + playerNumber));
            //塞牌
            getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCards(cardList);
        }
        //设置已经被抽出来的张数
        getGlobalData(event).get(indexInTheList(event)).setCardnumber(playerNumber * 2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //告知玩家牌面情况
    public static void showTheCards(MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if(isGroupMessage(event)){
            mcb.append("抽牌情况如下：");
            for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()){
                mcb = groupMCBBuilder(bjp,event); }

            //FriendMessageEvent
        } else {
            mcb.append("抽牌情况如下：");
            for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()) {
                mcb = friendMCBBuilder(bjp);
            }
        }
        event.getSubject().sendMessage(mcb.asMessageChain());
        //允许玩家操作
        allowPlayerToOperate(event);
        //设置定时任务
        endOperationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                foldEveryoneInSixtySeconds(event);
            }
        },60*1000);
    }

    //showTheCards里面用到的群界面返回MessageChainBuilder
    public static MessageChainBuilder groupMCBBuilder(BlackJackPlayer bjp, MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if (bjp.isBookmaker()) {
            mcb.append("\n\n").append("庄家的牌是：");
            mcb.append("\n").append(getPoker(bjp.getCards().get(0))).append("\n❓❓");
        } else {
            mcb.append("\n\n").append((new At(event.getSender().getId()))).append("的牌是：");
            for (Integer card : bjp.getCards()) {
                mcb.append("\n").append(getPoker(card));
            }
        }
        return mcb;
    }

    //showTheCards里面用到的用户界面返回MessageChainBuilder
    public static MessageChainBuilder friendMCBBuilder(BlackJackPlayer bjp){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if (bjp.getID() == 0) {
            mcb.append("\n\n").append("庄家的牌是：");
            mcb.append("\n").append(getPoker(bjp.getCards().get(0))).append("\n❓❓");
        } else {
            mcb.append("\n\n").append("你的牌是：");
            for (Integer card : bjp.getCards()) {
                mcb.append("\n").append(getPoker(card));
            }
        }
        return mcb;
    }

    //解除禁止玩家操作
    public static void allowPlayerToOperate(MessageEvent event){
        for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()){
            if (!bjp.isBookmaker()) { bjp.setCanOperate(true); }
        }
        event.getSubject().sendMessage(StartOperateNotice);
    }

    //定时任务：60秒内把所有玩家全部fold掉，并强制进入结算模式
    public static void foldEveryoneInSixtySeconds(MessageEvent event){
        for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()){
            if (!bjp.isBookmaker()) { bjp.setCanOperate(false); }
        }
        resultCalculator(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //开始玩家操作
    public static void playerOperation(MessageEvent event){
        if(bjOperation(event)==null) return;
        if(operationAvailabilityCheck(event)) return;

        //主操作
        startOperation(event);
        //判定是否都fold
        if (!haveAllFolded(event)) return;
        endOperationTimer.cancel();
        //结算了
        resultCalculator(event);
    }

    //playOperation里面使用的Switch
    public static void startOperation(MessageEvent event){
        switch (Objects.requireNonNull(bjOperation(event))) {
            case Assurance:
                assurance(event);
                break;
            case Deal:
                deal(event);
                break;
            case Double:
                doubleBet(event);
                break;
            case Fold:
                fold(event);
                break;
            case Pair:
                pair(event);
                break;
            case Split:
                split(event);
                break;
            case Surrender:
                surrender(event);
                break;
        }
    }

    //判定是否全员fold
    public static boolean haveAllFolded(MessageEvent event){
        for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()){
            if (bjp.isBookmaker()) continue;
            if (bjp.isCanOperate()) return false;
        }
        return true;

    }

    //返回是否可以操作
    public static boolean operationAvailabilityCheck(MessageEvent event){
        return (!getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isCanOperate());
    }

    //保险
    public static void assurance(MessageEvent event){
        if (!canBuyAssurance(event)) return;
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setHasAssurance(true);
    }

    //要牌
    public static void deal(MessageEvent event){
        //塞牌进去，增加卡牌数量，判定是否爆牌，爆了就不能operate，
        getCardSendNotice(event,1);
        bustThatMthrfckr(event);
    }

    //双倍下注
    public static void doubleBet(MessageEvent event){
        //只有没双倍才能双倍
        if(!canDouble(event)) return;
        int bet = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
        if (!hasEnoughMoney(event, bet)){
            //很遗憾地通知您 您没有钱
            event.getSubject().sendMessage(mcbProcessor(event).append(YouDontHaveEnoughMoney).asMessageChain());
            return;
        }
        SenoritaCounter.minusMoney(event, bet);
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBet(bet * 2);

    }

    //停牌
    public static void fold(MessageEvent event){
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCanOperate(false);
    }

    //下注对子
    public static void pair(MessageEvent event){
        if (!canBetPair(event)) return;
        //下注对子扣钱
        int bet = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
        //如果有钱就能扣款了
        if (!hasEnoughMoney(event, bet)){
            //很遗憾地通知您 您没有钱
            event.getSubject().sendMessage(mcbProcessor(event).append(YouDontHaveEnoughMoney).asMessageChain());
            return;
        }
        SenoritaCounter.minusMoney(event, bet);
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setBetPair(true);

    }

    //分牌
    public static void split(MessageEvent event){
        if(!canSplitTheCards(event)) return;
        //扣款！
        int bet = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getBet();
        if (!hasEnoughMoney(event, bet)){
            //很遗憾地通知您 您没有钱
            event.getSubject().sendMessage(mcbProcessor(event).append(YouDontHaveEnoughMoney).asMessageChain());
            return;
        }
        SenoritaCounter.minusMoney(event, bet);

        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCanOperate(false);
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setHasSplit(true);

        splitGetCardSendNotice(event);
    }

    //投降
    public static void surrender(MessageEvent event){
        //改变flag
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setHasSurrendered(true);
        //也算是停牌了
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCanOperate(false);
        event.getSubject().sendMessage(mcbProcessor(event).append("您投降了，将会返还您一半的赌注。").asMessageChain());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //能否买保险
    public static boolean canBuyAssurance(MessageEvent event){
        return getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(0)%13==1;
    }


    //能否分牌
    public static boolean canSplitTheCards(MessageEvent event){
        List<Integer> cardList = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getCards();
        return (cardList.size() == 2) && cardList.get(0).equals(cardList.get(1));
    }

    //能否双倍下注
    public static boolean canDouble(MessageEvent event){return !getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isDouble(); }

    //能否下注对子
    public static boolean canBetPair(MessageEvent event){ return !getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isBetPair(); }

    //计算点数
    public static int cardPointCalculator(List<Integer> cardList){
        int totalPoints = 0;
        for(Integer card : cardList){
            int actualCard = card % 13;
            if(card>10) actualCard = 10;
            if(card==0) actualCard = 10;
            if(card==1) actualCard = 11;
            totalPoints += actualCard;
            if((totalPoints>21)&&cardList.contains(1)) totalPoints -= 10;
        }
        return totalPoints;
    }

    //计算单张牌的点数，A算11
    public static int cardPointCalculator(Integer card){
        int actualCard = card % 13;
        if(card>10) actualCard = 10;
        if(card==0) actualCard = 10;
        if(card==1) actualCard = 11;
        return actualCard;
    }

    //判定是否爆牌了
    public static boolean hasBusted(MessageEvent event){
        List<Integer> cardList = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getCards();
        return cardPointCalculator(cardList) > 21;
    }

    public static boolean hasBusted(int points){
        return points > 21;
    }

    //爆牌操作
    public static void bustThatMthrfckr(MessageEvent event){
        if(hasBusted(event)){
            getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setHasBusted(true);
            getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).setCanOperate(false);
            MessageChainBuilder mcb = mcbProcessor(event);
            event.getSubject().sendMessage(mcb.append(BustNotice).asMessageChain());
        }
    }

    //抽一张卡
    public static int getCard(MessageEvent event){
        int cardNumber = getGlobalData(event).get(indexInTheList(event)).getCardnumber();
        int card = getGlobalData(event).get(indexInTheList(event)).getCardPile().get(cardNumber);
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).addCards(card);
        getGlobalData(event).get(indexInTheList(event)).setCardnumber(cardNumber + 1);
        return card;
    }

    //庄家抽一张卡
    public static void bookMakerGetCard(MessageEvent event){
        int cardNumber = getGlobalData(event).get(indexInTheList(event)).getCardnumber();
        int card = getGlobalData(event).get(indexInTheList(event)).getCardPile().get(cardNumber);
        getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).addCards(card);
        getGlobalData(event).get(indexInTheList(event)).setCardnumber(cardNumber + 1);
    }

    //抽卡操作 先抽卡，给排队+1，然后发送消息
    public static void getCardSendNotice(MessageEvent event, int num){
        MessageChainBuilder mcb = mcbProcessor(event);
        mcb.append("您抽到的牌是：");
        for (int i=0;i<num;i++){
            mcb.append("\n").append(getPoker(getCard(event)));
        }
        event.getSubject().sendMessage(mcb.asMessageChain());
    }

    //Split抽卡操作 先抽卡，给排队+1，然后发送消息
    public static void splitGetCardSendNotice(MessageEvent event){
        MessageChainBuilder mcb = mcbProcessor(event);
        mcb.append("您的原始牌为").append(getPoker(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getCards().get(0))).append("\n")
                .append(getPoker(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).getCards().get(0)));
        mcb.append("您两个牌堆抽到的牌分别是：");
        for (int i=0;i<2;i++){
            mcb.append("\n").append(getPoker(getCard(event)));
        }
        event.getSubject().sendMessage(mcb.asMessageChain());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //结算
    public static void resultCalculator(MessageEvent event){
        //庄家先操作
        bookmakerDoesTheFinalMove(event);
        //计算分值
        Map<Long,Double> resultMap = bookfucker(event);
        //返回赌款
        for ( Long ID : resultMap.keySet()){
            SenoritaCounter.addMoney(ID,(int)Math.round(resultMap.get(ID)));
        }
        //赌场进出帐
        casinoHasItsFinalLaugh(resultMap);
        //通知玩家
        sendFinalNotice(event, resultMap);
    }


    //查看庄家的牌有没有超过17点
    public static boolean bookmakerNeedsToGetMoreCards(MessageEvent event){
        return cardPointCalculator(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards())<17;
    }

    //庄家的最后操作
    public static void bookmakerDoesTheFinalMove(MessageEvent event){
        //只要没到十七点就得继续开牌
        while(bookmakerNeedsToGetMoreCards(event)){
            bookMakerGetCard(event);
        }
    }

    public static Map<Long,Double> bookfucker(MessageEvent event){
        Map<Long,Double> resultMap = new HashMap<>();
        for (BlackJackPlayer bjp : getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList()){
            if (bjp.isBookmaker()) continue;
            int bet = bjp.getBet();
            resultMap.put(bjp.getID(),bet*calculateGeneralPoint(event));
        }
        return resultMap;
    }

    //不算保险、对子
    public static double calculateNormalPoint(MessageEvent event) {
        //投降
        if (getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isHasSurrendered()) return 0.5;
        //分牌
        if (getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isHasSplit()) return calculateSplitPoint(event);
        //爆了
        if (getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isHasBusted()) return 0;
        //计算庄家点数
        int bookmakersPoints = cardPointCalculator(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards());
        //庄家爆了
        if (hasBusted(bookmakersPoints)) return 2;
        //计算玩家点数
        int playersPoints = cardPointCalculator(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards());
        return calculateBigOrSmall(bookmakersPoints,playersPoints);
    }

    public static int calculateSplitPoint(MessageEvent event){
        int bookmakersPoints = cardPointCalculator(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards());
        List<Integer> playersPointsList1 = new ArrayList<>();
        List<Integer> playersPointsList2 = new ArrayList<>();
        playersPointsList1.add(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(0));
        playersPointsList1.add(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(2));
        playersPointsList2.add(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(1));
        playersPointsList2.add(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(3));
        return calculateBigOrSmall(bookmakersPoints,cardPointCalculator(playersPointsList1))+calculateBigOrSmall(bookmakersPoints,cardPointCalculator(playersPointsList2));
    }

    //比大小返回
    public static int calculateBigOrSmall(int bookmakersPoints, int playersPoints){
        if (playersPoints == bookmakersPoints) return 1;
        if (playersPoints > bookmakersPoints) return 2;
        return 0;
    }

    //计算对子
    public static int calculatePairPoint(MessageEvent event){
        int card1 = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(0)%13;
        int card2 = getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(1)%13;
        if (card1==card2) return 11;
        return 0;
    }

    //计算保险
    public static int calculateAssurancePoint(MessageEvent event){
        int coefficient = 1;
        if (getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isHasSplit()){
            coefficient = 2;
        }
        int card = cardPointCalculator(getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfTheBookMaker(event)).getCards().get(1));
        if(card==10) return coefficient;
        return 0;
    }

    //计算总倍率
    public static double calculateGeneralPoint(MessageEvent event){
        if (getGlobalData(event).get(indexInTheList(event)).getBlackJackPlayerList().get(indexOfThePlayer(event)).isHasAssurance()){
            return (0.5*(calculateNormalPoint(event)+calculatePairPoint(event))+calculateAssurancePoint(event));
        }
        return (calculateNormalPoint(event)+calculatePairPoint(event));
    }

    //发送最终结算
    public static void sendFinalNotice(MessageEvent event,Map<Long,Double> resultMap){
        MessageChainBuilder mcb = new MessageChainBuilder();
        for (long ID : resultMap.keySet()){
            mcb.append(EndBetNotice).append("\n");
            if (isGroupMessage(event)){
                mcb.append("\n").append(new At(ID)).append(" 获得了").append(String.valueOf(resultMap.get(ID))).append("南瓜比索");
            } else {
                mcb.append("\n").append("您获得了").append("南瓜比索");
            }
        }
    }

    public static void casinoHasItsFinalLaugh(Map<Long,Double> resultMap){
        double finalAmount = 0;
        for (long ID : resultMap.keySet()){
            finalAmount += resultMap.get(ID);
        }
        if (finalAmount>0) { SenoritaCounter.addMoney(0,(int)Math.round(finalAmount)); }
        if (finalAmount<0) SenoritaCounter.minusMoneyMaybeAllIn(0,-(int)Math.round(finalAmount));
    }
}
