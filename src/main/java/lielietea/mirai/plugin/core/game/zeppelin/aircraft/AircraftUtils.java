package lielietea.mirai.plugin.core.game.zeppelin.aircraft;

import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.Notice;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;

public class AircraftUtils {

    public static String changePirateStatus(boolean isPirate, long playerID){
        if(!Aircraft.exist(playerID)) return(Notice.NOT_REGISTERED);
        if(Activity.isInActivity(playerID)) return Notice.IS_IN_ACTIVITY;
        if(Aircraft.isPirate(playerID)==isPirate) return Notice.CANNOT_CHANGE_PIRATE_STATUS;
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        ai.setPirate(isPirate);
        Aircraft.updateRecord(ai);
        if(isPirate) return Notice.BECOME_PIRATE;
        return Notice.BECOME_TRADER;
    }

    public static void changeHomePort(){

    }
}
