package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;

import java.util.Objects;

public class RadarUtils {

    static double distance(Coordinate coord1, Coordinate coord2){
        return Math.sqrt(Math.pow(coord1.x-coord2.x,2)+Math.pow(coord1.y-coord2.y,2));
    }

    static boolean isInAttackRange(Coordinate coord1,Coordinate coord2){
        return distance(coord1,coord2)< Config.COLLISION_DISTANCE;
    }


    static double speed(long playerID){
        return ((Objects.requireNonNull(Aircraft.get(playerID)).getSpeedFactor()-1)*0.1+1)*Config.SPEED_FACTOR;
    }

    static Coordinate direction(Coordinate departure, Coordinate destination){
        double coefficient = Math.sqrt(Math.pow(departure.y- destination.y, 2)+Math.pow(departure.x- destination.x,2));
        double x = (destination.x- departure.x)/coefficient;
        double y = (destination.y- departure.y)/coefficient;
        return new Coordinate(x,y);
    }
}
