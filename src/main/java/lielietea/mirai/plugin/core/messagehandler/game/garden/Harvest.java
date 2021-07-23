package lielietea.mirai.plugin.core.messagehandler.game.garden;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.garden.propertyenum.Fruits;
import lielietea.mirai.plugin.core.messagehandler.game.garden.propertyenum.PlantSeed;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Harvest extends GardenUtils{
    static class HarvestList{
        List<HarvestRes> harvestlist;
    }

    static class HarvestRes{
        PlantSeed seed;
        List<FruitDetail> fruitDetails;
    }

    static class FruitDetail{
        Fruits fruit;
        double[] chance;
    }

    public static HarvestList getHarvestFromJson(){
        String HARVEST_PATH = "/cluster/harvest.json";
        InputStream is = JetPackUtil.class.getResourceAsStream(HARVEST_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        return gson.fromJson(br,HarvestList.class);
    }

    /**
     * 根据Harvest.json里面的chance数组来计算最终获得的物品的数量
     */
    public static int fruitCalculator(double[] chance){
        double[] realChance = new double[chance.length];
        int[] number = new int[chance.length];
        for (int i=0;i<chance.length;i++){
            realChance[i]=chance[i]-Math.floor(chance[i]);
            number[i]= (int) Math.floor(chance[i]);
        }
        double totalChance = 0;
        for (double j : realChance){
            totalChance += j;
        }
        Random random = new Random();
        double dice = random.nextDouble()*totalChance;
        for (int k=0;k<chance.length;k++){
            if(dice<=realChance[k]){
                return number[k];
            }
        }
        return number[0];
    }

    /**
     * 向仓库里面添加 fruits，如果没有这个种类则新建，如果有这个种类则更新数值
     * 如果数值超过99，则自动变成99
     */
    public static GardenWorld put(GardenWorld gw, long groupID, Fruits fruit, int num){
        for (int i = 0; i<gw.group.get(getGroupGarden(groupID,gw)).warehouse.size();i++){
            if(gw.group.get(getGroupGarden(groupID,gw)).warehouse.get(i).id==fruit.ordinal()){
                gw.group.get(getGroupGarden(groupID,gw)).warehouse.get(i).num += num;
                if (gw.group.get(getGroupGarden(groupID,gw)).warehouse.get(i).num>99){
                    gw.group.get(getGroupGarden(groupID,gw)).warehouse.get(i).num=99;
                }
                return gw;
            }
        }
        if (num!=0) {
            gw.group.get(getGroupGarden(groupID, gw)).warehouse.add(new Warehouse(fruit.ordinal(), num));
        }
        return gw;
    }

    /**
     * 采摘花园里全部的成熟植物
     */
    public static GardenWorld pick(GardenWorld gw, long groupID, HarvestList hl){
        Date date = new Date();
        long dateNow = date.getTime();
        List<GardenStatus> list = convertToGS(gw.group.get(getGroupGarden(groupID,gw)));
        for (int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).layout.size();i++){
            if (list.get(i).isMature){
                gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).stamp=dateNow;
                gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object=0;
                for(HarvestRes hr : hl.harvestlist){
                    if (hr.seed.ordinal()==gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object){
                        for( FruitDetail fd : hr.fruitDetails){
                            gw = put(gw, groupID,fd.fruit,fruitCalculator(fd.chance));
                        }
                    }
                }
            }
        }
        return gw;
    }

    /**
     * 龙卷风摧毁停车场
     */
    public static GardenWorld remove(GardenWorld gw, long groupID){
        for(int i=0;i<gw.group.get(getGroupGarden(groupID,gw)).layout.size();i++){
            gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object = 0;
        }
        return gw;
    }

}
