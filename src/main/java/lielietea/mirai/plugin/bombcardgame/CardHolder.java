package lielietea.mirai.plugin.bombcardgame;

import java.util.ArrayList;
import java.util.List;

//还没写完！
class CardHolder {
    List<Card> cardStack;

    public CardHolder(){
        cardStack = new ArrayList<>();
    }

    /**
     * 向该牌堆中添加一张被抽过的牌
     * @param card 被添加的牌
     */
    public void addCard(Card card){
        cardStack.add(card);
    }

    /**
     * 获取该牌堆中某位玩家抽过的牌的总数
     * @param qqID 玩家QQ号
     * @return 某玩家在该牌堆中抽牌总数
     */
    public int getDrawnCardSum(long qqID){
        int temp = 0;
        for(Card card : cardStack){
            if(card.qqID==qqID) temp++;
        }
        return temp;
    }

    public void clearStack(){
        cardStack.clear();
    }

    /**
     * 检测该牌堆中是否有某位玩家在不同的群中过抽牌
     * @param qqID 玩家QQ号
     * @return 检测结果
     */
    public boolean isDrawnCardInDifferentGroup(long qqID){
        long temp = 0;
        for(Card card : cardStack){
            if(card.qqID==qqID) {
                if(temp==0){
                    temp = card.groupID;
                }
                else{
                    if(temp==card.groupID)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CardHolder{" +
                "cardStack=" + cardStack +
                '}';
    }
}
