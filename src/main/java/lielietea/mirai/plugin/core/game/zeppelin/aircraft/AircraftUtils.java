package lielietea.mirai.plugin.core.game.zeppelin.aircraft;

import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.Notice;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Objects;

public class AircraftUtils {

    public static String changePirateStatus(ShipKind shipKind, MessageEvent event){
        long playerID = event.getSender().getId();
        if(Activity.exist(playerID)) return Notice.IS_IN_ACTIVITY;
        if(Aircraft.getShipKind(playerID)==shipKind) return Notice.CANNOT_CHANGE_PIRATE_STATUS;
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        ai.setShipKind(shipKind);
        Aircraft.updateRecord(ai);
        if(shipKind==ShipKind.Pirate) {
            return Notice.BECOME_PIRATE;
        } else {
            Coordinate current = ai.getCoordinate();
            Coordinate homePort = CityInfoUtils.getCityCoords(ai.getHomePortCode());
            ActivityInfo ac = new ActivityInfo(current,homePort,event);
            Activity.updateRecord(ac);
            return Notice.BECOME_TRADER+"目前您正在返回母港,等抵达母港之后可以开始进行活动。";
        }

    }

    public static String changeHomePort(String homePortCode, long playerID){
        if(Activity.exist(playerID)) return Notice.IS_IN_ACTIVITY;
        if(Objects.requireNonNull(Aircraft.get(playerID)).getHomePortCode().equals(homePortCode)) return Notice.CANNOT_CHANGE_HOME_PORT;
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        ai.setHomePortCode(homePortCode);
        Aircraft.updateRecord(ai);
        return Notice.HOME_PORT_CHANGED+ CityInfoUtils.getCityNameCN(homePortCode);
    }
}
