package lielietea.mirai.plugin.bombcardgame;

import org.jetbrains.annotations.NotNull;

class Card implements Comparable<Card> {
    public long qqID;
    public long groupID;
    public int sequence;
    public String cardContent;
    public CardType cardType;

    public Card(long qqID, long groupID, int sequence, String cardContent, CardType cardType) {
        this.qqID = qqID;
        this.groupID = groupID;
        this.sequence = sequence;
        this.cardContent = cardContent;
        this.cardType = cardType;
    }

    /**
     * 排序依据的顺序是QQ群号->QQ号->抽牌顺序
     *
     * @param o 不用我们管，这个是排序方法调用的
     * @return 不用我们管，这个是排序方法调用的
     */
    @Override
    public int compareTo(@NotNull Card o) {
        if(groupID>o.groupID) return 1;
        else if(groupID<o.groupID) return -1;
        else{
            if(qqID>o.qqID) return 1;
            else if(qqID<o.qqID) return -1;
            else{
                if(sequence>o.sequence) return 1;
                else return -1;
            }
        }

    }

    public enum CardType { //牌色
        WHITE,
        BLACK,
        RED
    }

    @Override
    public String toString() {
        return "CardDrawn{" +
                "qqID=" + qqID +
                ", groupID=" + groupID +
                ", sequence=" + sequence +
                ", cardContent='" + cardContent + '\'' +
                '}';
    }
}
