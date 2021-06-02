package lielietea.mirai.plugin.bombcardgame;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class CardDrawn implements Comparable<CardDrawn> {
    public long qqID;
    public long groupID;
    public int sequence;
    public String cardContent;

    public CardDrawn(long qqID, long groupID, int sequence, String cardContent) {
        this.qqID = qqID;
        this.groupID = groupID;
        this.sequence = sequence;
        this.cardContent = cardContent;
    }

    /**
     * 排序依据的顺序是QQ群号->QQ号->抽牌顺序
     *
     * @param o 不用我们管，这个是排序方法调用的
     * @return 不用我们管，这个是排序方法调用的
     */
    @Override
    public int compareTo(@NotNull CardDrawn o) {
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
