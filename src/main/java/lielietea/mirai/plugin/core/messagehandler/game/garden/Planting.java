package lielietea.mirai.plugin.core.messagehandler.game.garden;

import lielietea.mirai.plugin.core.messagehandler.game.garden.propertyenum.PlantSeed;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Date;
import java.util.Random;

public class Planting extends GardenUtils{

    /**
     * 根据指定位置种下种子
     */
    public static GardenWorld plant(long groupID, int loc, PlantSeed plantSeed, GardenWorld gw){
        int index = getGroupGarden(groupID, gw);
        if (index==-1){
            return gw;
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

    /**
     * 随机种种子
     */
    public static GardenWorld plantRandom(GardenWorld gw, GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        Random random = new Random();
        for(int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).layout.size();i++){
            if (gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object==0) {
                int seedNum = gw.group.get(getGroupGarden(groupID, gw)).seedlist.get(random.nextInt(gw.group.get(getGroupGarden(groupID, gw)).seedlist.size()));
                PlantSeed ps = PlantSeed.values()[seedNum];
                gw = plant(groupID,i,ps,gw);
            }
        }
        return gw;
    }

    /**
     * 所有的地种下相同种子
     */
    public static GardenWorld plantSingle(GardenWorld gw, GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        String message = event.getMessage().contentToString();
        String[] messageSplit = message.split(" ");
        int seedNumber = Integer.parseInt(messageSplit[2]);
        boolean seedChecker = false;
        for(int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).seedlist.size();i++){
            if (gw.group.get(getGroupGarden(groupID,gw)).seedlist.get(i)==seedNumber){
                seedChecker = true;
            }
        }
        if (seedChecker){
            PlantSeed ps = PlantSeed.values()[seedNumber];
            for (int j=0;j<gw.group.get(getGroupGarden(groupID,gw)).layout.size();j++){
                if (gw.group.get(getGroupGarden(groupID,gw)).layout.get(j).object==0) {
                    gw = plant(groupID, j, ps, gw);
                }
            }
        } else {
            event.getGroup().sendMessage("输入的种子不在种子库中。");
        }
        return gw;
    }

    /**
     * 输入多颗种子，交替耕种
     */
    public static GardenWorld plantMixed(GardenWorld gw, GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        String message = event.getMessage().contentToString();
        String[] messageSplit = message.split(" ");
        int[] seedNumber = new int[messageSplit.length-1];
        boolean[] seedChecker = new boolean[messageSplit.length-1];
        for(int i=0;i<messageSplit.length-1;i++){
            seedNumber[i] = Integer.parseInt(messageSplit[i]);
            seedChecker[i] = false;
        }

        for(int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).seedlist.size();i++){
            for(int j=0;j<messageSplit.length-1;j++){
                if (gw.group.get(getGroupGarden(groupID,gw)).seedlist.get(i)==seedNumber[j]) {
                    seedChecker[i] = true;
                }
            }
        }

        //检测数组boolean是否都是true
        boolean seedCheckerAll = true;
        for(int i=0;i<messageSplit.length-1;i++){
            seedCheckerAll = seedCheckerAll&&seedChecker[i];
        }

        if(seedCheckerAll){
            int seedCount = 0;
            PlantSeed ps;
            for(int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).layout.size();i++){
                if (gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object==0) {
                    if (seedCount>=seedNumber.length){
                        seedCount=0;
                    }
                    ps = PlantSeed.values()[seedNumber[seedCount]];
                    gw = plant(groupID, i, ps, gw);
                    seedCount += 1;
                }
            }
        } else {
            event.getGroup().sendMessage("输入的种子不在种子库中。");
        }
        return gw;
    }
}
