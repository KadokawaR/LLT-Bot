package lielietea.mirai.plugin.bombcardgame;

import java.util.List;
import java.util.Map;

//还远没写完！
public class BombCardSessionManager {
    Boolean isActive;
    int cardDrawn;
    Map<CardColorType,CardHolder> cardStacks; //用来存取已抽牌信息
    Map<SpecialNoticeType, List<Long>> noticeData;//用来存储哪些玩家已经被特殊提示过

    public int getDrawnCardSumByColor(String color,long qqID){
        return 0;
    }

    public boolean isDrawnCardInAllColor(long qqID){
        return true;
    }

    enum CardColorType{ //牌色
        WHITE,
        BLACK,
        RED
    }

    enum SpecialNoticeType{ //特殊提示种类
        DRAW_3_CARDS,
        DRAW_3_CARDS_WITH_DIFFERENT_COLOR,
        DRAW_IN_DIFFERENT_GROUP
    }
}
