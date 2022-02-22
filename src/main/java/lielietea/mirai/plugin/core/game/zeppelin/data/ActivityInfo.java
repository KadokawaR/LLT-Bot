package lielietea.mirai.plugin.core.game.zeppelin.data;

import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

public class ActivityInfo {
    private Coordinate departure;
    private Coordinate destination;
    private long playerID;
    private long messageEventID; // 如果是好友私聊则为0
    private MultiBotHandler.BotName botName;
    private long targetPlayerID;
    private int goodsValue;
    private String goodsName;
    private Date startTime;

    public ActivityInfo(Coordinate departure, Coordinate destination, MessageEvent event){
        this.departure = departure;
        this.destination = destination;
        this.playerID = event.getSender().getId();
        if(event instanceof FriendMessageEvent) this.messageEventID = 0;
        else this.messageEventID = event.getSubject().getId();
        this.botName = MultiBotHandler.BotName.get(event.getBot().getId());
        this.targetPlayerID = 0;
        this.goodsValue = 0;
        this.goodsName = "";
        this.startTime = new Date();
    }

    public ActivityInfo(MessageEvent event){
        this.departure = CityInfoUtils.getCityCoords("VLA");
        this.destination = CityInfoUtils.getCityCoords("SGP");
        this.playerID = event.getSender().getId();
        if(event instanceof FriendMessageEvent) this.messageEventID = 0;
        else this.messageEventID = event.getSubject().getId();
        this.botName = MultiBotHandler.BotName.get(event.getBot().getId());
        this.targetPlayerID = 0;
        this.goodsValue = 0;
        this.goodsName = "";
        this.startTime = new Date();
    }

    public ActivityInfo(){
        this.departure = null;
        this.destination = null;
        this.playerID = 0;
        this.messageEventID = 0;
        this.botName = null;
        this.targetPlayerID = 0;
        this.goodsValue = 0;
        this.goodsName = "";
        this.startTime = new Date();
    }

    public Coordinate getDeparture() { return departure; }

    public void setDeparture(Coordinate departure) {
        this.departure = departure;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

    public long getPlayerID() {
        return playerID;
    }

    public void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    public long getMessageEventID() {
        return messageEventID;
    }

    public void setMessageEventID(long messageEventID) {
        this.messageEventID = messageEventID;
    }

    public long getTargetPlayerID() {
        return targetPlayerID;
    }

    public void setTargetPlayerID(long targetPlayerID) {
        this.targetPlayerID = targetPlayerID;
    }

    public int getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(int goodsValue) {
        this.goodsValue = goodsValue;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getGoodsName() {
        String res = this.goodsName;
        try {
            res = URLDecoder.decode(this.goodsName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public MultiBotHandler.BotName getBotName() {
        return botName;
    }

    public void setBotName(MultiBotHandler.BotName botName) {
        this.botName = botName;
    }
}
