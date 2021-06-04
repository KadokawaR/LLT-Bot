package lielietea.mirai.plugin.bombcardgame;

//还远没写完！
class ContentGenerator {
    /**
     * 获取牌面随机内容
     * @param color 牌面颜色
     * @param isBombCard 是否为炸弹牌
     * @return 牌面内容
     */
    public String getRandomContent(Card.CardType color, boolean isBombCard){
        return "test-content "+ color +" = isBombCard: " + isBombCard;
    }
}
