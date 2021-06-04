package lielietea.mirai.plugin.bombcardgame;

import java.util.*;
import java.util.stream.Collectors;

//还没写完！
class CardHolder {
    List<Card> cardStack;

    public CardHolder(){
        cardStack = new ArrayList<>();
    }


    //向该牌堆中添加一张被抽过的牌
    public void addCard(Card card){
        cardStack.add(card);
    }


    //获取该牌堆中某位玩家抽过的牌的总数
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


    //检测该牌堆中是否有某位玩家在不同的群中过抽牌
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

    //获取炸弹牌受害者列表
    public Set<VictimPair> getBombCardVictim(){
        Set<VictimPair> victimList = new HashSet<>();
        Card.CardType victimCardType = cardStack.get(cardStack.size()-1).cardType;
        List<Card> victimCardStack = cardStack.stream().filter(card -> card.cardType==victimCardType).collect(Collectors.toList());
        for(Card victimCard: victimCardStack){
            victimList.add(new VictimPair(victimCard.qqID,victimCard.groupID));
        }
        return victimList;
    }



    @Override
    public String toString() {
        return "CardHolder{" +
                "cardStack=" + cardStack +
                '}';
    }
}
