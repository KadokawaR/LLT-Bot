package lielietea.mirai.plugin.core.game.zeppelin;

import lielietea.mirai.plugin.core.game.zeppelin.notification.NotificationCenter;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.function.Shop;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.UserInterface;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Radar;
import net.mamoe.mirai.event.events.MessageEvent;

public class Zeppelin {
    public static void start(MessageEvent event){
        UserInterface.zeppelinCommand(event);
    }

    public static void ini(){
        Radar.getInstance().ini();
        NotificationCenter.getInstance().ini();
        Aircraft.getInstance().ini();
        Activity.getInstance().ini();
        CityInfoUtils.getINSTANCE().ini();
        Shop.getInstance().ini();
    }

    public static void test(MessageEvent event){
        TestTools.test(event);
    }
}
