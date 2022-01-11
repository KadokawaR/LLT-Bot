package lielietea.mirai.plugin.core.game.zeppelin;

import lielietea.mirai.plugin.core.game.zeppelin.Notification.NotificationCenter;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Notification;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;
import lielietea.mirai.plugin.core.game.zeppelin.processor.ActivityUtils;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class TestTools {

    public static void addNewNotification(MessageEvent event){
        if(event.getMessage().contentToString().contains("/addmesg")){
            String mesg = event.getMessage().contentToString().replace("/addmesg","");
            mesg = mesg.replace(" ","");
            long messageID = 0;
            if(event instanceof GroupMessageEvent) messageID=event.getSubject().getId();
            Notification n = new Notification(mesg, MultiBotHandler.getBotName(event.getBot().getId()),event.getSender().getId(),messageID);
            NotificationCenter.add(n);
        }
    }

    public static void generateNewShip(MessageEvent event){
        if(event.getMessage().contentToString().contains("/addnewship")){
            ActivityUtils.generatePhantomShip();
        }
    }

    public static void test(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        addNewNotification(event);
        generateNewShip(event);
    }

    public static void printAircraftLocation(){
        for(AircraftInfo ai: Aircraft.getInstance().aircrafts){
            System.out.println(ai.getCoordinate().x+","+ai.getCoordinate().y);
        }
    }
}
