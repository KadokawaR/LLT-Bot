package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Radar {

    List<AircraftInfo> pirates;
    List<AircraftInfo> traders;

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Radar(){
        pirates = new ArrayList<>();
        traders = new ArrayList<>();
        executor.schedule(new RadarTask(),1, TimeUnit.MINUTES);
    }

    static final Radar INSTANCE = new Radar();
    public static Radar getInstance(){ return INSTANCE;}

    static class RadarTask implements Runnable{
        @Override
        public void run() {

        }
    }

    static void updateList(){
        getInstance().pirates.clear();
        getInstance().traders.clear();
        for(ActivityInfo ai:Activity.getInstance().activities){
            long playerID = ai.getPlayerID();
            AircraftInfo aircraftInfo = Aircraft.get(playerID);
            assert aircraftInfo != null;
            if(aircraftInfo.isPirate()) getInstance().pirates.add(aircraftInfo);
            else getInstance().traders.add(aircraftInfo);
        }
    }

    static double distance(Coordinate coord1, Coordinate coord2){
        return Math.sqrt(Math.pow(coord1.x-coord2.x,2)+Math.pow(coord1.y-coord2.y,2));
    }

    static boolean isInAttackRange(Coordinate coord1,Coordinate coord2){
        return distance(coord1,coord2)< Config.COLLISION_DISTANCE;
    }

    static void collisionCheck(){
        for
    }

}
