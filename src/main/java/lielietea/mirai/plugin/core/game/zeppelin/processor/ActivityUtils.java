package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.ShipKind;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.function.Shop;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.Notice;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.UIUtils;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivityUtils {

    public static void startAsPirateChasingShip(MessageEvent event, String shipCode/*需判定shipCode是否存在*/){
        ActivityInfo ai = new ActivityInfo(event);
        ai.setTargetPlayerID(Objects.requireNonNull(Aircraft.getIDFromName(shipCode)));
        Activity.updateRecord(ai);
    }

    public static boolean isPirateChasingShip(ActivityInfo ai){
        return ai.getTargetPlayerID()!=0;
    }

    public static void startAsPirateStationed(MessageEvent event, Coordinate stationedCoord){
        Activity.delete(event.getSender().getId());
        ActivityInfo ai = new ActivityInfo(event);
        ai.setDeparture(stationedCoord);
        ai.setDestination(stationedCoord);
        Activity.updateRecord(ai);
    }

    public static boolean isPirateStationed(ActivityInfo ai){
        return ai.getDeparture().equals(ai.getDestination())&&Aircraft.getShipKind(ai.getPlayerID())== ShipKind.Pirate;
    }

    public static void abortFlight(MessageEvent event){
        long playerID = event.getSender().getId();
        //如果是海盗则直接停下，如果是商船则返回出发地
        if(Aircraft.getShipKind(playerID)==ShipKind.Pirate){
            Activity.delete(playerID);
        } else {
            ActivityInfo originalAI = Activity.get(playerID);
            Activity.delete(playerID);
            ActivityInfo newAI = new ActivityInfo(event);
            newAI.setDeparture(Objects.requireNonNull(Aircraft.get(playerID)).getCoordinate());
            assert originalAI != null;
            newAI.setDestination(originalAI.getDeparture());
            Activity.updateRecord(newAI);
        }
    }

    public static void abortFlight(AircraftInfo ai){
        long playerID = ai.getPlayerID();
        //如果是海盗则直接停下，如果是商船则返回出发地
        if(Aircraft.getShipKind(playerID)==ShipKind.Pirate){
            Activity.delete(playerID);
        } else {
            ActivityInfo activityInfo = Activity.get(playerID);
            assert activityInfo != null;
            activityInfo.setDestination(activityInfo.getDeparture());
            activityInfo.setDeparture(Objects.requireNonNull(Aircraft.get(playerID)).getCoordinate());
            activityInfo.setGoodsValue(0);
            activityInfo.setGoodsName("");
            activityInfo.setStartTime(new Date());
            Activity.updateRecord(activityInfo);
        }
    }

    public static void abortFlight(ActivityInfo ai){
        long playerID = ai.getPlayerID();
        //如果是海盗则直接停下，如果是商船则返回出发地
        if(Aircraft.getShipKind(playerID)==ShipKind.Pirate){
            Activity.delete(playerID);
        } else {
            ai.setDestination(ai.getDeparture());
            ai.setDeparture(Objects.requireNonNull(Aircraft.get(playerID)).getCoordinate());
            ai.setGoodsValue(0);
            ai.setGoodsName("");
            ai.setStartTime(new Date());
            Activity.updateRecord(ai);
        }
    }

    public static String goHome(MessageEvent event){
        long playerID = event.getSender().getId();
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        if(Activity.exist(playerID)) return Notice.IS_IN_ACTIVITY;
        if(CityInfoUtils.isInCity(ai.getCoordinate())){
            if(ai.getHomePortCode().equals(CityInfoUtils.getCityCode(ai.getCoordinate()))){
                return Notice.SAME_CITY_WARNING;
            }
        }
        ActivityInfo ac = new ActivityInfo(event);
        ac.setDeparture(ai.getCoordinate());
        ac.setDestination(CityInfoUtils.getCityCoords(ai.getHomePortCode()));
        Activity.updateRecord(ac);
        String res = "您的飞艇正在前往母港"+CityInfoUtils.getCityNameCN(ai.getHomePortCode());
        if(ActivityUtils.isPirate(ac)) res+="，该操作消耗燃油费。";
        return res;
    }

    public static void startAsTrader(MessageEvent event,String goodsName,int goodsValue,String destinationCode){
        long playerID = event.getSender().getId();
        AircraftInfo ai = Aircraft.get(playerID);
        ActivityInfo ac = new ActivityInfo(event);
        assert ai != null;
        ac.setDeparture(ai.getCoordinate());
        ac.setDestination(CityInfoUtils.getCityCoords(destinationCode));
        ac.setGoodsName(goodsName);
        ac.setGoodsValue(goodsValue);
        Activity.updateRecord(ac);
    }

    public static void startAsTrader(MessageEvent event,String goodsName,int goodsValue,Coordinate coord){
        long playerID = event.getSender().getId();
        AircraftInfo ai = Aircraft.get(playerID);
        ActivityInfo ac = new ActivityInfo(event);
        assert ai != null;
        ac.setDeparture(ai.getCoordinate());
        ac.setDestination(coord);
        ac.setGoodsName(goodsName);
        ac.setGoodsValue(goodsValue);
        Activity.updateRecord(ac);
    }

    public static void generatePolice(){
        AircraftInfo ai = new AircraftInfo(ShipKind.Police);
        ActivityInfo ac = new ActivityInfo();

        Coordinate departure = CityInfoUtils.getRandomCoords();
        Coordinate destination = CityInfoUtils.getRandomCoords();
        while(departure==destination){
            destination = CityInfoUtils.getRandomCoords();
        }

        ai.setCoordinate(departure);
        Aircraft.updateRecord(ai);

        ac.setPlayerID(ai.getPlayerID());
        ac.setDeparture(departure);
        ac.setDestination(destination);

        Activity.updateRecord(ac);
    }

    public static void generatePhantomShip(){
        AircraftInfo ai = new AircraftInfo(ShipKind.PhantomShip);
        ActivityInfo ac = new ActivityInfo();

        Coordinate departure = CityInfoUtils.getRandomCoords();
        Coordinate destination = CityInfoUtils.getRandomCoords();
        while(departure==destination){
            destination = CityInfoUtils.getRandomCoords();
        }

        ai.setCoordinate(departure);
        Aircraft.updateRecord(ai);

        ac.setPlayerID(ai.getPlayerID());
        ac.setDeparture(departure);
        ac.setDestination(destination);
        ac.setGoodsName(GoodsGenerator.name());
        ac.setGoodsValue(GoodsGenerator.value(departure,destination,ai.getPlayerID()));

        Activity.updateRecord(ac);
    }

    public static boolean isPolice(ActivityInfo ai) {
        AircraftInfo a = Aircraft.get(ai.getPlayerID());
        assert a != null;
        return a.getShipKind()==ShipKind.Police;
    }

    public static boolean isPirate(ActivityInfo ai) {
        AircraftInfo a = Aircraft.get(ai.getPlayerID());
        assert a != null;
        return a.getShipKind()==ShipKind.Pirate;
    }
}
