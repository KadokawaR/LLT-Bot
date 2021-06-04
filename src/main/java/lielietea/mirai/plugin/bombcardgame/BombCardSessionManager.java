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


    BombCardSessionManager(){
    }

    public static BombCardSessionManager getInstance(){
        return INSTANCE;
    }

    public void handleGameSession(GroupMessageEvent event){
        //游戏在冷却中时无法进行操作
        if(isGameInCoolDown()){
            event.getGroup().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(COOLDOWN_REPLY.get(rand.nextInt(COOLDOWN_REPLY.size())))
                    .build());
        }
        else{
            //如果游戏没有初始化，或者刚脱离冷却
            if(isGameInactive()) startNewSession();

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
    void startNewSession(){
        //生成新的SeesionID
        String newSeesionID = UUID.randomUUID().toString();

        //设置一个计时器，限制一局的游戏时间
        Timer timer = new Timer();
        timer.schedule(new SessionAutoCloseTimerTask(newSeesionID,GAME_SESSION_COOLDOWN_IN_SECONDS), GAME_SESSION_LENGTH_IN_SECONDS * 1000);

        //进入抽牌流程
    }

    //处理抽牌流程
    void handleDrawCard(GroupMessageEvent event){

    }





}
