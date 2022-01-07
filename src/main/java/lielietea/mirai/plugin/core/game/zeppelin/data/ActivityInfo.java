package lielietea.mirai.plugin.core.game.zeppelin.data;

import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;

import java.util.Date;

public class ActivityInfo {
    private Coordinate departure;
    private Coordinate destination;
    private long playerID;
    private long messageEventID; // 如果是好友私聊则为0
    private long targetPlayerID;
    private int goodsValue;
    private int goodsCode;
    private Date startTime;

    public ActivityInfo(Coordinate departure, Coordinate destination, long playerID, long messageEventID){
        this.departure = departure;
        this.destination = destination;
        this.playerID = playerID;
        this.messageEventID = messageEventID;
        this.targetPlayerID = 0;
        this.goodsValue = 0;
        this.goodsCode = 0;
        this.startTime = new Date();
    }

    public ActivityInfo(){
        this.departure = CityInfoUtils.getCityCoords("VLA");
        this.destination = CityInfoUtils.getCityCoords("SGP");
        this.playerID = 0;
        this.messageEventID = 0;
        this.targetPlayerID = 0;
        this.goodsValue = 0;
        this.goodsCode = 0;
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

    public int getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(int goodsCode) {
        this.goodsCode = goodsCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
