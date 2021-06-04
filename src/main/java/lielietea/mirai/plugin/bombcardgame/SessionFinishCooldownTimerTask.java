package lielietea.mirai.plugin.bombcardgame;

import java.util.TimerTask;

public class SessionFinishCooldownTimerTask extends TimerTask {
    String sessionID;

    public SessionFinishCooldownTimerTask(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public void run() {
        //让游戏脱离冷却状态
        if(BombCardSession.INSTANCE.sessionStatus == BombCardSession.SessionStatus.COOLDOWN && BombCardSession.INSTANCE.sessionID == sessionID){
            BombCardSession.INSTANCE.sessionStatus = BombCardSession.SessionStatus.INACTIVE;
        }

        //重置Session ID
        BombCardSession.INSTANCE.sessionID = "";

    }
}
