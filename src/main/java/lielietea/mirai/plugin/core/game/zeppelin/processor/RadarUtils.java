package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;

import java.util.Objects;

public class RadarUtils {

    static double distance(Coordinate coord1, Coordinate coord2){
        return Math.sqrt(Math.pow(coord1.x-coord2.x,2)+Math.pow(coord1.y-coord2.y,2));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isInAttackRange(Coordinate coord1, Coordinate coord2){
        return distance(coord1,coord2)< Config.COLLISION_DISTANCE;
    }


    static double speed(long playerID){
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai!=null;
        double speed = ((ai.getSpeedFactor()-1)*0.1+1)*Config.SPEED_FACTOR;
        if (!ai.isPirate()){
            speed = speed * Config.PIRATE_SPEED_BONUS;
        }
        return speed;
    }

    static Coordinate direction(Coordinate departure, Coordinate destination){
        double coefficient = Math.sqrt(Math.pow(departure.y- destination.y, 2)+Math.pow(departure.x- destination.x,2));
        if (coefficient==0) return new Coordinate(0,0);
        double x = (destination.x- departure.x)/coefficient;
        double y = (destination.y- departure.y)/coefficient;
        return new Coordinate(x,y);
    }
}
