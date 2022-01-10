package lielietea.mirai.plugin.core.game.zeppelin.Notification;

import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;

import java.util.Objects;

public class NotificationGenerator {
    public enum NotificationKind{
        PirateBothDisarmed,
        PirateBeatAnotherPirate,
        PirateGetBeatenByAnotherPirate,
        PirateGetBeatenByTrader,
        PirateGetBeatenByPolice,
        PirateRobTrader,
        /*====================*/
        TraderGetRobbed,
        TraderBeatPirate,
        /*====================*/
        PirateEndStationed,
        PirateLostTarget,
        /*====================*/
        TraderArriveDestination,
        ArriveDestination,
        /*====================*/
        WeatherRelatedNotification
    }

    public static String get(NotificationKind nk,ActivityInfo ai){
        switch (nk){
            case PirateBothDisarmed:
                return "您的飞艇在和海盗船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"的交火中双双坠毁，已扣取全部南瓜比索。目前已解除海盗标记并正在返回母港。";
            case PirateBeatAnotherPirate:
                return "您的飞艇在和海盗船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"的交火中战胜对方。";
            case PirateGetBeatenByAnotherPirate:
                return "您的飞艇在和海盗船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"的交火中被击败，已扣取全部南瓜比索。目前已解除海盗标记并正在返回母港。";
            case PirateGetBeatenByTrader:
                return "您的飞艇在和商船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"的交火中被击败，已扣取全部南瓜比索。目前已解除海盗标记并正在返回母港。";
            case PirateGetBeatenByPolice:
                return "您的飞艇在和警察的交火中被击败，已扣取全部南瓜比索。";
            case PirateRobTrader:
                return "您的飞艇已成功劫持商船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"，并获得货物："+ai.getGoodsName()+"，价值"+ai.getGoodsValue()+"南瓜比索";

            case TraderGetRobbed:
                return "您的飞艇在航行过程中被海盗船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"劫持，货物已经掉落，您即将返回出发地"+ CityInfoUtils.getCityNameCN(ai.getDeparture());
            case TraderBeatPirate:
                return "您的飞艇在航行过程中遭遇海盗船"+ Objects.requireNonNull(Aircraft.get(ai.getPlayerID())).getName()+"劫持，对方已经被您击败，您将继续前往目的地"+ CityInfoUtils.getCityNameCN(ai.getDestination());

            case PirateEndStationed:
                return "您的飞艇已经停止驻扎。";
            case PirateLostTarget:
                return "您的飞艇在跟随目标"+ Objects.requireNonNull(Aircraft.get(ai.getTargetPlayerID())).getName()+"时丢失对方，并在原地停下。";

            case TraderArriveDestination:
                return "您已经抵达目的地"+CityInfoUtils.getCityNameCN(ai.getDestination())+"，成功运送货物："+ai.getGoodsName()+"，获得"+ai.getGoodsValue()/10+"南瓜比索。";
            case ArriveDestination:
                return "您已经抵达目的地"+CityInfoUtils.getCityNameCN(ai.getDestination());

            case WeatherRelatedNotification:
                return "";
        }
        return "";
    }
    
}
