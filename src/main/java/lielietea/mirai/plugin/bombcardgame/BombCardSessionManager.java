package lielietea.mirai.plugin.bombcardgame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//还远没写完！
public class BombCardSessionManager {
    static int MAXIMUM_CARDS_DRAWN = 10;
    Boolean isActive;
    int cardDrawn;
    Map<CardColorType,CardHolder> cardStacks; //用来存取已抽牌信息
    Map<SpecialNoticeType, List<Long>> noticeData;//用来存储哪些玩家已经被特殊提示过
    static BombCardSessionManager INSTANCE = new BombCardSessionManager();

    BombCardSessionManager(){
        isActive=false;
        cardDrawn=0;
        cardStacks = new HashMap<>();
        noticeData = new HashMap<>();
    }

    public BombCardSessionManager getInstance(){
        return INSTANCE;
    }
    
    void clearSession(){
        isActive=false;
        cardDrawn=0;
        cardStacks = new HashMap<>();
        noticeData = new HashMap<>();
    }

    int getDrawnCardSumByColor(CardColorType color,long qqID){
        if(cardStacks.containsKey(color)){
            return cardStacks.get(color).getDrawnCardSum(qqID);
        } else {
            return 0;
        }
    }

    boolean isDrawnCardInAllColor(long qqID){
        int w,b,r;
        w = getDrawnCardSumByColor(CardColorType.WHITE,qqID);
        b = getDrawnCardSumByColor(CardColorType.BLACK,qqID);
        r = getDrawnCardSumByColor(CardColorType.RED,qqID);
        if(w>0&&b>0&&r>0) return true;
        else return false;
    }

    public enum CardColorType{ //牌色
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
