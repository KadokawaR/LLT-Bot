package lielietea.mirai.plugin.core.game.zeppelin.data;

import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;

public class Notification {
    String message;
    MultiBotHandler.BotName botName;
    long playerID;
    long messageID;//0 if FriendMessageEvent

    public Notification(String message, MultiBotHandler.BotName botName, long playerID, long messageID) {
        this.message = message;
        this.botName = botName;
        this.playerID = playerID;
        this.messageID = messageID;
    }

    public Notification(ActivityInfo ai,String message){
        this.message=message;
        this.botName=ai.getBotName();
        this.playerID=ai.getPlayerID();
        this.messageID=ai.getMessageEventID();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MultiBotHandler.BotName getBotName() {
        return botName;
    }

    public void setBotName(MultiBotHandler.BotName botName) {
        this.botName = botName;
    }

    public long getPlayerID() {
        return playerID;
    }

    public void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }
}
