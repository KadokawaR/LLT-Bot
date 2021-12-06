package lielietea.mirai.plugin.utils.multibot;

public class BotConfig {
    long ID;
    boolean acceptFriend;
    boolean acceptGroup;
    boolean answerFriend;
    boolean answerGroup;

    BotConfig(long ID){
        this.ID = ID;
        this.acceptFriend=true;
        this.acceptGroup=true;
        this.answerFriend=true;
        this.answerGroup=true;
    }
}
