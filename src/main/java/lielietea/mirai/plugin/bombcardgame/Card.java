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

    @Override
    public String toString() {
        return "Card{" +
                "qqID=" + qqID +
                ", groupID=" + groupID +
                ", sequence=" + sequence +
                ", cardContent='" + cardContent + '\'' +
                ", cardType=" + cardType +
                '}';
    }

    public enum CardType { //牌类型
        PLACE,
        SCENE,
        RACE,
        COMMON_OBJECT,
        SPECIAL_OBJECT,
        MOTTO,
        EFFECT,
        BOMB_CARD_GAME_BACKGROUND,
        AUTHORS_WORDS
    }

}
