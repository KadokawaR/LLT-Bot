package lielietea.mirai.plugin.bombcardgame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BombCardSession {
    static int MAXIMUM_CARDS_DRAWN = 12;

    SessionStatus sessionStatus; //游戏状态
    String sessionID; //某局游戏的ID
    int cardDrawn; //保存已抽牌数
    CardHolder cardStacks; //保存已抽牌信息
    Map<SpecialNoticeType, List<Long>> noticeData;//保存哪些玩家已经被特殊提示过
    public static BombCardSession INSTANCE = new BombCardSession();

    BombCardSession(){
        sessionStatus=SessionStatus.INACTIVE;
        cardDrawn=0;
        cardStacks = new CardHolder();
        noticeData = new HashMap<>();
    }

    void clearSession(){
        cardDrawn=0;
        cardStacks.clearStack();
        noticeData.clear();
    }

    enum SpecialNoticeType{ //特殊提示种类
        DRAW_IN_DIFFERENT_GROUP
    }

    public enum SessionStatus{ //特殊提示种类
        INACTIVE,
        ACTIVE,
        COOLDOWN
    }
}
