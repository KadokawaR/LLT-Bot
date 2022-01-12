package lielietea.mirai.plugin.core.game.zeppelin.data;

import com.google.gson.annotations.SerializedName;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.ShipKind;
import lielietea.mirai.plugin.core.game.zeppelin.function.Shop;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.UIUtils;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;

import java.util.Date;

public class AircraftInfo {
    private String name;
    @SerializedName("ID")
    private long playerID;
    @SerializedName("Port")
    private String homePortCode;
    @SerializedName("S")
    private int speedFactor;
    @SerializedName("M")
    private int moneyFactor;
    @SerializedName("A")
    private int attackFactor;
    @SerializedName("P")
    private ShipKind shipKind;
    @SerializedName("C")
    private Coordinate coordinate;

    public AircraftInfo(ShipKind shipKind){
        this.name= UIUtils.randomAircraftName();
        //todo:可能存在隐患重复，后需更改
        this.playerID = (new Date().getTime())*-1;
        this.coordinate = CityInfoUtils.getRandomCoords();
        this.homePortCode = CityInfoUtils.getCityCode(this.coordinate);
        ModeInfo mi = Shop.getModeInfo(Shop.getRandomCode());
        assert mi != null;
        this.speedFactor=mi.getSpeed();
        this.moneyFactor=mi.getMoney();
        this.attackFactor=mi.getAttack();
        this.shipKind=shipKind;
    }

    public AircraftInfo(String name, long playerID, String homePortCode){
        this.name=name;
        this.playerID=playerID;
        this.homePortCode=homePortCode;
        this.speedFactor=1;
        this.moneyFactor=1;
        this.attackFactor=1;
        this.shipKind =ShipKind.NormalShip;
        this.coordinate= CityInfoUtils.getCityCoords(homePortCode);
    }

    public void set(AircraftInfo ai){
        setName(ai.getName());
        setPlayerID(ai.getPlayerID());
        setHomePortCode(ai.getHomePortCode());
        setSpeedFactor(ai.getSpeedFactor());
        setMoneyFactor(ai.getMoneyFactor());
        setAttackFactor(ai.getAttackFactor());
        setShipKind(ai.getShipKind());
        setCoordinate(ai.getCoordinate());
    }

    public String getMode(){
        return Shop.getShipMode(getSpeedFactor(),getAttackFactor(),getMoneyFactor());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPlayerID() {
        return playerID;
    }

    public void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    public String getHomePortCode() {
        return homePortCode;
    }

    public void setHomePortCode(String homePortCode) {
        this.homePortCode = homePortCode;
    }

    public int getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(int speedFactor) {
        this.speedFactor = speedFactor;
    }

    public int getMoneyFactor() {
        return moneyFactor;
    }

    public void setMoneyFactor(int moneyFactor) {
        this.moneyFactor = moneyFactor;
    }

    public int getAttackFactor() {
        return attackFactor;
    }

    public void setAttackFactor(int attackFactor) {
        this.attackFactor = attackFactor;
    }

    public ShipKind getShipKind() {
        return shipKind;
    }

    public void setShipKind(ShipKind pirate) {
        shipKind = pirate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public boolean isPirate() {
        return getShipKind()==ShipKind.Pirate;
    }
}
