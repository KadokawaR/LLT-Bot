package lielietea.mirai.plugin.game.garden;

import lielietea.mirai.plugin.game.garden.propertyenum.PlantSeed;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Date;
import java.util.Objects;

public class Planting extends GardenUtils{
    public static GardenWorld plant(GroupMessageEvent event, int loc, PlantSeed plantSeed, GardenWorld gw){
        int index = getGroupGarden(event, gw);
        if (index==-1){
            return null;
        }
        Date date = new Date();
        long dateNum = date.getTime();
        for(int i = 0; i< gw.group.get(index).layout.size(); i++){
            if(gw.group.get(index).layout.get(i).loc==loc){
                gw.group.get(index).layout.get(i).object=plantSeed.ordinal();
                gw.group.get(index).layout.get(i).stamp=dateNum;
            }
        }
        return gw;
    }
}
