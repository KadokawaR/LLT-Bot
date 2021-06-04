package lielietea.mirai.plugin.bombcardgame;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.*;

//还远没写完！
public class BombCardSessionManager {
    static BombCardSessionManager INSTANCE = new BombCardSessionManager();
    static ArrayList<String> COOLDOWN_REPLY = new ArrayList<>(List.of(
            "牌堆的填充就和调配奶茶一样需要时间，请耐心。",
            "牌堆还在自动填充中，现在游戏还不能开始。",
            "牌堆填充中，请稍等一会儿"));
    static int GAME_SESSION_LENGTH_IN_SECONDS = 180;
    static int GAME_SESSION_COOLDOWN_IN_SECONDS = 15;
    static Random rand = new Random();
    //权重树
    static TreeMap<Double,Card.CardType> weightedCardTypeExceptBombCard = new TreeMap<Double,Card.CardType>(){
        {
            /*
            在此处添加内容
            weightedCardTypeExceptBombCard.put(double 累计权重,Card.CardType 类型);
            请注意是累计权重！
             */
            weightedCardTypeExceptBombCard.put(1D, Card.CardType.PLACE);
            weightedCardTypeExceptBombCard.put(2D, Card.CardType.SCENE);
            weightedCardTypeExceptBombCard.put(3D, Card.CardType.RACE);
            weightedCardTypeExceptBombCard.put(4D, Card.CardType.COMMON_OBJECT);
            weightedCardTypeExceptBombCard.put(5D, Card.CardType.SPECIAL_OBJECT);
            weightedCardTypeExceptBombCard.put(6D, Card.CardType.MOTTO);
            weightedCardTypeExceptBombCard.put(7D, Card.CardType.EFFECT);
            weightedCardTypeExceptBombCard.put(7.1D, Card.CardType.BOMB_CARD_GAME_BACKGROUND);
            weightedCardTypeExceptBombCard.put(7.2D, Card.CardType.AUTHORS_WORDS);
        }
    };


    BombCardSessionManager(){
    }

    public static BombCardSessionManager getInstance(){
        return INSTANCE;
    }

    public synchronized void handleGameSession(GroupMessageEvent event){
        //游戏在冷却中时无法进行操作
        if(isGameInCoolDown()){
            event.getGroup().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(COOLDOWN_REPLY.get(rand.nextInt(COOLDOWN_REPLY.size())))
                    .build());
        }
        else{
            //如果游戏没有初始化，或者刚脱离冷却
            if(isGameInactive()) startNewSession(event);

            //如果游戏已经在进行中
            else handleDrawCard(event);
        }
    }

    //检测游戏是否在冷却中
    boolean isGameInCoolDown(){
        return BombCardSession.INSTANCE.sessionStatus == BombCardSession.SessionStatus.COOLDOWN;
    }

    //检测游戏是否没有初始化，或者刚脱离冷却
    boolean isGameInactive(){
        return BombCardSession.INSTANCE.sessionStatus == BombCardSession.SessionStatus.INACTIVE;
    }

    //开始一局新游戏时进行初始化
    void startNewSession(GroupMessageEvent event){
        //生成新的SeesionID
        String newSeesionID = UUID.randomUUID().toString();

        //设置一个计时器，限制一局的游戏时间
        Timer timer = new Timer();
        timer.schedule(new SessionAutoCloseTimerTask(newSeesionID,GAME_SESSION_COOLDOWN_IN_SECONDS), GAME_SESSION_LENGTH_IN_SECONDS * 1000);

        //进入抽牌流程
        handleDrawCard(event);
    }

    //结束游戏流程
    void endCurrentSession(){
        //设置游戏状态为冷却中
        BombCardSession.INSTANCE.sessionStatus = BombCardSession.SessionStatus.COOLDOWN;
        BombCardSession.INSTANCE.clearSession();

        //设置一个计时器，让游戏可以脱离冷却状态
        Timer timer = new Timer();
        timer.schedule(new SessionFinishCooldownTimerTask(BombCardSession.INSTANCE.sessionID), GAME_SESSION_COOLDOWN_IN_SECONDS * 1000);

    }

    //处理抽牌流程
    void handleDrawCard(GroupMessageEvent event){
        //随机生成一张牌
        Card.CardType type = randomlyGenerateCardType();
        Card newlyDrawnCard = new Card(event.getSender().getId(),
                event.getGroup().getId(),
                BombCardSession.INSTANCE.cardDrawn,
                ContentGenerator.getRandomContent(type),
                type);

        //将牌加入牌堆
        BombCardSession.INSTANCE.addCardToStack(newlyDrawnCard);

        //广播抽牌结果
        broadcastDrawCardResult(event,newlyDrawnCard);

        //处理是否触发相关特殊提示
        handleSpecialNotice(event);

        //如果该牌是炸弹牌
        if(newlyDrawnCard.cardType == Card.CardType.BOMB){
            broadcastGameResult();
            endCurrentSession();

        }
        //如果这张牌是最后一张牌
        else if(BombCardSession.INSTANCE.cardDrawn >= BombCardSession.MAXIMUM_CARDS_DRAWN){
            broadcastGameResult();
            endCurrentSession();
        }


    }

    //生成牌类型
    Card.CardType randomlyGenerateCardType(){
        //先决定是否生成炸弹牌
        if(1d/BombCardSession.MAXIMUM_CARDS_DRAWN < Math.random()){
            return Card.CardType.BOMB;
        }
        //如果不生成炸弹牌，再按权重生成其他牌
        else {
            double randomWeight = weightedCardTypeExceptBombCard.lastKey() * Math.random();
            SortedMap<Double, Card.CardType> tailMap = weightedCardTypeExceptBombCard.tailMap(randomWeight, false);
            return weightedCardTypeExceptBombCard.get(tailMap.firstKey());
        }
    }

    //广播抽牌结果
    void broadcastDrawCardResult(GroupMessageEvent event,Card card){

    }

    //广播游戏结果
    void broadcastGameResult(){

    }

    //检测是否有触发特殊提示。如果触发特殊提示，处理数据并进行广播
    void handleSpecialNotice(GroupMessageEvent event){

    }





}
