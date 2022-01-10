package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.bank.PumpkinPesoWindow;
import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.Notification.NotificationCenter;
import lielietea.mirai.plugin.core.game.zeppelin.Notification.NotificationGenerator;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.ShipKind;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.data.Notification;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import org.checkerframework.checker.units.qual.C;


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Radar {

    public List<AircraftInfo> pirates;
    public List<AircraftInfo> traders;
    public List<AircraftInfo> polices;
    public List<AircraftInfo> phantoms;
    public Map<Long,Coordinate> tempMap;

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Radar(){
        pirates = new ArrayList<>();
        traders = new ArrayList<>();
        polices = new ArrayList<>();
        phantoms = new ArrayList<>();
        tempMap = new HashMap<>();
        executor.scheduleAtFixedRate(new RadarTask(),Config.NOTIFICATION_INITIAL_DELAY,1, TimeUnit.MINUTES);
    }

    static final Radar INSTANCE = new Radar();
    public static Radar getInstance(){ return INSTANCE;}

    static class RadarTask implements Runnable{
        @Override
        public void run() {
            updateList();
            pirateInfoCheck();
            move();
            collisionCheck();
            arrivalCheck();
        }
    }

    static void updateList(){
        getInstance().pirates.clear();
        getInstance().traders.clear();
        getInstance().polices.clear();
        getInstance().phantoms.clear();
        for(ActivityInfo ai:Activity.getInstance().activities){
            long playerID = ai.getPlayerID();
            AircraftInfo aircraftInfo = Aircraft.get(playerID);
            assert aircraftInfo != null;
            ShipKind sk = aircraftInfo.getShipKind();
            switch(sk){
                case Pirate:
                    getInstance().pirates.add(aircraftInfo);
                    break;
                case NormalShip:
                    getInstance().traders.add(aircraftInfo);
                    break;
                case PhantomShip:
                    getInstance().phantoms.add(aircraftInfo);
                    break;
                case Police:
                    getInstance().polices.add(aircraftInfo);
                    break;
            }
        }
    }


    static void collision(AircraftInfo ai1,AircraftInfo ai2){
        if(ai1.isPirate()&& ai2.isPirate()) {
            pirateCollision(ai1, ai2);
            return;
        }
        if(!ai1.isPirate()&&ai2.isPirate()){
            pirateTraderCollision(ai2,ai1);
            return;
        }
        if(ai1.isPirate()&&!ai2.isPirate()){
            pirateTraderCollision(ai1, ai2);
        }
    }

    static void pirateCollision(AircraftInfo p1,AircraftInfo p2){
        if(p1.getAttackFactor()==p2.getAttackFactor()){
            ActivityInfo ai1 = Activity.get(p1.getPlayerID());
            ActivityInfo ai2 = Activity.get(p2.getPlayerID());

            assert ai1 != null;
            assert ai2 != null;

            String mesg1 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateBothDisarmed,ai2);
            String mesg2 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateBothDisarmed,ai1);

            NotificationCenter.add(new Notification(ai1,mesg1));
            NotificationCenter.add(new Notification(ai2,mesg2));

            disarmPirateShip(p1);
            disarmPirateShip(p2);

            return;
        }

        ActivityInfo ai1 = Activity.get(p1.getPlayerID());
        ActivityInfo ai2 = Activity.get(p2.getPlayerID());
        assert ai1 != null;
        assert ai2 != null;
        String mesg1;
        String mesg2;

        if(p1.getAttackFactor()> p2.getAttackFactor()){

            mesg1 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateBeatAnotherPirate, ai2);
            mesg2 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateGetBeatenByAnotherPirate, ai1);

            NotificationCenter.add(new Notification(ai1,mesg1));
            NotificationCenter.add(new Notification(ai2,mesg2));

            disarmPirateShip(p2);

        } else {

            mesg1 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateGetBeatenByAnotherPirate, ai2);
            mesg2 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateBeatAnotherPirate, ai1);

            NotificationCenter.add(new Notification(ai1,mesg1));
            NotificationCenter.add(new Notification(ai2,mesg2));
            disarmPirateShip(p1);
        }
    }

    static void pirateTraderCollision(AircraftInfo p,AircraftInfo t){
        ActivityInfo aiTrader = Activity.get(t.getPlayerID());
        ActivityInfo aiPirate = Activity.get(p.getPlayerID());
        assert aiTrader != null;
        assert aiPirate != null;
        String mesg1;
        String mesg2;

        if(t.getAttackFactor()>=p.getAttackFactor()){
            mesg1 = NotificationGenerator.get(NotificationGenerator.NotificationKind.TraderBeatPirate, aiPirate);
            mesg2 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateGetBeatenByTrader, aiTrader);
            disarmPirateShip(p);
        } else {
            mesg1 = NotificationGenerator.get(NotificationGenerator.NotificationKind.TraderGetRobbed, aiPirate);
            mesg2 = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateRobTrader, aiTrader);
            PumpkinPesoWindow.addMoney(aiPirate.getPlayerID(),aiTrader.getGoodsValue());
            ActivityUtils.abortFlight(aiTrader);

        }

        NotificationCenter.add(new Notification(aiTrader,mesg1));
        NotificationCenter.add(new Notification(aiPirate,mesg2));

    }

    static void piratePoliceCollision(AircraftInfo p){
        ActivityInfo ai = Activity.get(p.getPlayerID());
        assert ai!= null;
        String mesg = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateGetBeatenByPolice,null);
        NotificationCenter.add(new Notification(ai,mesg));
        disarmPirateShip(p);
    }

    static void disarmPirateShip(AircraftInfo ai){
        ai.setShipKind(ShipKind.NormalShip);
        Aircraft.updateRecord(ai);
        PumpkinPesoWindow.minusMoneyMaybeAllIn(ai.getPlayerID(),Config.PIRATE_BAIL);
        ActivityInfo ac = Activity.get(ai.getPlayerID());
        assert ac != null;
        ac.setDeparture(ai.getCoordinate());
        ac.setDestination(CityInfoUtils.getCityCoords(ai.getHomePortCode()));
        Activity.updateRecord(ac);
    }

    static void collisionCheck(){
        int pirateNum = getInstance().pirates.size();
        for(int i=0;i<pirateNum;i++) {
            for (AircraftInfo ai : getInstance().polices) {
                if (CityInfoUtils.isInCityProtection(ai.getCoordinate())) continue;
                if (!RadarUtils.isInAttackRange(getInstance().pirates.get(i).getCoordinate(), ai.getCoordinate()))
                    continue;
                piratePoliceCollision(getInstance().pirates.get(i));
                break;
            }
        }

        pirateNum = getInstance().pirates.size();
        for(int i=0;i<pirateNum;i++) {
            if(pirateNum<2) break;
            for (int j = i + 1; j < pirateNum; j++) {
                if (CityInfoUtils.isInCityProtection(getInstance().pirates.get(i).getCoordinate()) || CityInfoUtils.isInCityProtection(getInstance().pirates.get(j).getCoordinate()))
                    continue;
                if (!RadarUtils.isInAttackRange(getInstance().pirates.get(i).getCoordinate(), getInstance().pirates.get(j).getCoordinate()))
                    continue;
                collision(getInstance().pirates.get(i), getInstance().pirates.get(j));
                break;//只能撞一艘
            }
        }

        pirateNum = getInstance().pirates.size();
        for(int i=0;i<pirateNum;i++) {
            for (AircraftInfo ai : getInstance().traders) {
                if (CityInfoUtils.isInCityProtection(ai.getCoordinate())) continue;
                if (!RadarUtils.isInAttackRange(getInstance().pirates.get(i).getCoordinate(), ai.getCoordinate()))
                    continue;
                collision(getInstance().pirates.get(i), ai);
                break;//只能撞一艘
            }
        }

        pirateNum = getInstance().pirates.size();
        for(int i=0;i<pirateNum;i++) {
            for (AircraftInfo ai : getInstance().phantoms) {
                if (CityInfoUtils.isInCityProtection(ai.getCoordinate())) continue;
                if (!RadarUtils.isInAttackRange(getInstance().pirates.get(i).getCoordinate(), ai.getCoordinate()))
                    continue;
                collision(getInstance().pirates.get(i), ai);
                break;//只能撞一艘
            }
        }
    }

    static void move(){
        getInstance().tempMap.clear();
        for(AircraftInfo ai: bigList()){ move(ai); }
        Aircraft.updateRecord(getInstance().tempMap);
        getInstance().tempMap.clear();
    }

    static void move(AircraftInfo ai){
        ActivityInfo ac = Activity.get(ai.getPlayerID());
        assert ac != null;
        Coordinate destination = ac.getDestination();
        Coordinate currentLoc = ai.getCoordinate();
        double speed = RadarUtils.speed(ac.getPlayerID());
        Coordinate direction = RadarUtils.direction(currentLoc,destination);
        Coordinate distance = new Coordinate(Math.abs(destination.x- currentLoc.x), Math.abs(destination.y- currentLoc.y));
        Coordinate speedCoord = new Coordinate(speed*direction.x,speed*direction.y);
        Coordinate speedCoordAbs = new Coordinate(Math.abs(speed*direction.x),Math.abs(speed* direction.y));
        //如果距离小于速度+2 则到终点
        //如果没到终点 则speed*direction.x 和 speed*direction.y
        Coordinate finalCoord = new Coordinate(currentLoc.x+speedCoord.x, currentLoc.y+speedCoord.y);
        if(distance.x-2<speedCoordAbs.x) finalCoord.x = destination.x;
        if(distance.x-2<speedCoordAbs.x) finalCoord.y = destination.y;
        getInstance().tempMap.put(ai.getPlayerID(),finalCoord);
    }

    //跟丢目标判定和停止驻扎判定
    static void pirateInfoCheck(){
        for(AircraftInfo ai: getInstance().pirates){
            ActivityInfo ac = Activity.get(ai.getPlayerID());
            assert ac != null;
            if(ac.getTargetPlayerID()!=0){
                if(!Activity.exist(ac.getTargetPlayerID())){
                    //跟丢了
                    ActivityUtils.abortFlight(ai);
                    String message = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateLostTarget,ac);
                    NotificationCenter.add(new Notification(ac,message));
                    continue;
                }
            }

            //超时
            Date startTime = ac.getStartTime();
            Date now = new Date();

            if((now.getTime()-startTime.getTime())>60*2*1000){
                ActivityUtils.abortFlight(ac);
                String message = NotificationGenerator.get(NotificationGenerator.NotificationKind.PirateEndStationed,ac);
                NotificationCenter.add(new Notification(ac,message));
            }
        }
    }

    static void arrivalCheck(){
        for(AircraftInfo ai: bigList()){
            arrive(ai);
        }
    }

    static void arrive(AircraftInfo ai){
        ActivityInfo ac = Activity.get(ai.getPlayerID());
        assert ac != null;
        if(ActivityUtils.isPirateStationed(ac)) return;
        if(ac.getDestination().equals(ai.getCoordinate())){
            if(ai.getShipKind()==ShipKind.NormalShip){
                PumpkinPesoWindow.addMoney(ai.getPlayerID(),ac.getGoodsValue()/10);
                Notification n = new Notification(ac,NotificationGenerator.get(NotificationGenerator.NotificationKind.TraderArriveDestination,ac));
                NotificationCenter.add(n);
            }

            if(ai.getShipKind()==ShipKind.Pirate){
                Notification n = new Notification(ac,NotificationGenerator.get(NotificationGenerator.NotificationKind.ArriveDestination,ac));
                NotificationCenter.add(n);
            }

            Activity.delete(ai.getPlayerID());
        }
    }

    static List<AircraftInfo> bigList(){
        List<AircraftInfo> allInList = new ArrayList<>();
        allInList.addAll(getInstance().phantoms);
        allInList.addAll(getInstance().pirates);
        allInList.addAll(getInstance().traders);
        allInList.addAll(getInstance().polices);
        return allInList;
    }

}
