package lielietea.mirai.plugin.game.garden;

import com.google.gson.Gson;
import lielietea.mirai.plugin.game.garden.propertyenum.Fruits;
import lielietea.mirai.plugin.game.garden.propertyenum.PlantSeed;
import lielietea.mirai.plugin.game.jetpack.JetPackUtil;

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
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        return gson.fromJson(br,HarvestList.class);
    }

    //根据Harvest.json里面的chance数组来计算最终获得的物品的数量
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

    public static List<Warehouse> put(List<Warehouse> warehouseList, Fruits fruit, int num){
        for (Warehouse warehouse : warehouseList){
            if(warehouse.id==fruit.ordinal()){
                warehouse.num += num;
                return warehouseList;
            }
        }
        warehouseList.add(new Warehouse(fruit.ordinal(),num));
        return warehouseList;
    }

    public static GroupGarden pick(GroupGarden gg,HarvestList hl){
        Random random = new Random();
        Date date = new Date();
        long datenow = date.getTime();
        List<GardenStatus> list = convertToGS(gg);
        for (int i=0;i<gg.layout.size();i++){
            if (list.get(i).isMature){
                gg.layout.get(i).stamp=datenow;
                gg.layout.get(i).object=0;
                for(HarvestRes hr : hl.harvestlist){
                    if (hr.seed.ordinal()==gg.layout.get(i).object){
                        for( FruitDetail fd : hr.fruitDetails){
                            gg.warehouse=put(gg.warehouse,fd.fruit,fruitCalculator(fd.chance));
                        }
                    }
                }
            }
        }
        return gg;
    }



}
