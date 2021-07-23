package lielietea.mirai.plugin.core.messagehandler.game.garden;

import lielietea.mirai.plugin.core.messagehandler.game.garden.propertyenum.Fruits;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import static com.google.common.math.IntMath.pow;

public class Caravan extends GardenUtils{

    /**
     *检查该群是否已经抵达上限60格
     */
    public static boolean checkSize(GardenWorld gw,long groupID){
        return gw.group.get(getGroupGarden(groupID,gw)).layout.size() < 60;
    }

    /**
     *根据地的尺寸计算升级费用
     */
    public static long getUpgradeFee(GardenWorld gw, long groupID){
        int row = gw.group.get(getGroupGarden(groupID,gw)).layout.size()/6;
        return 100L *pow(2,(row-1));
    }

    /**
     *计算钱是否够升级
     */
    public static boolean checkMoney(GardenWorld gw,long groupID){
        return gw.group.get(getGroupGarden(groupID,gw)).money>=getUpgradeFee(gw,groupID);
    }

    /**
     *购买土地，更新gw
     */
    public static GardenWorld buyTerritory(GardenWorld gw, GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        if(checkMoney(gw,groupID)){
            if(checkSize(gw,groupID)){
                gw = addRow(gw,groupID);
                gw.group.get(getGroupGarden(groupID,gw)).money -= getUpgradeFee(gw,groupID);
            } else {
                event.getGroup().sendMessage("花园尺寸已经到达上限，无法再升级。");
            }
        } else {
            event.getGroup().sendMessage("金额不够，无法完成升级。");
        }
        return gw;
    }

    /**
     * 计算仓库里面单一物品的价值之和
     */
    public static int getMoney(Warehouse wh){
        Fruits f = Fruits.values()[wh.id];
        return f.value[wh.id]*wh.num;
    }

    /**
     * 销售所有的水果并获得钱
     */
    public static GardenWorld sellAllFruits(GardenWorld gw, GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        while(gw.group.get(getGroupGarden(groupID,gw)).warehouse.size()!=0){
            gw.group.get(getGroupGarden(groupID,gw)).money += getMoney(gw.group.get(getGroupGarden(groupID,gw)).warehouse.get(0));
            gw.group.get(getGroupGarden(groupID,gw)).warehouse.remove(0);
        }
        return gw;
    }
}
