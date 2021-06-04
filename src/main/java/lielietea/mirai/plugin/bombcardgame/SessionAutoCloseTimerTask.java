package lielietea.mirai.plugin.bombcardgame;

import java.util.Timer;
import java.util.TimerTask;

public class SessionAutoCloseTimerTask extends TimerTask {
    String sessionID;
    int plannedCooldownTimeInSecond;

    public SessionAutoCloseTimerTask(String sessionID, int plannedCooldownTimeInSecond) {
        this.sessionID = sessionID;
        this.plannedCooldownTimeInSecond = plannedCooldownTimeInSecond;
    }

    @Override
    public void run() {
        //结束一局游戏，设置游戏为冷却状态
        if(BombCardSession.INSTANCE.sessionStatus == BombCardSession.SessionStatus.ACTIVE && BombCardSession.INSTANCE.sessionID == sessionID){
            BombCardSession.INSTANCE.sessionStatus = BombCardSession.SessionStatus.COOLDOWN;
            BombCardSession.INSTANCE.clearSession();

            //设置另外一个计时器，让游戏可以脱离冷却状态
            Timer timer = new Timer();
            timer.schedule(new SessionFinishCooldownTimerTask(sessionID), plannedCooldownTimeInSecond * 1000);
        }
    }
}
