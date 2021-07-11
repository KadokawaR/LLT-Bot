package lielietea.mirai.plugin.game.garden;

import com.google.gson.Gson;
import lielietea.mirai.plugin.game.garden.propertyenum.GardenType;
import lielietea.mirai.plugin.game.garden.propertyenum.PlantSeed;
import lielietea.mirai.plugin.messageresponder.fursona.FursonaPunk;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GardenUtils {

    /**
     * 用于记录所有群所有花园信息的类
     */
    static class GardenWorld{
        List<GroupGarden> group;
    }
    static class GroupGarden{
        long id;
        long money;
        List<Warehouse> warehouse;
        List<GardenTiles> layout;
    }
    static class GardenTiles{
        int loc;
        int object;
        long stamp;
    }
    static class GardenStatus{
        int loc;
        int object;
        boolean isMature;

        public GardenStatus(int loc1, int object1, boolean isMature1){
            loc = loc1;
            object = object1;
            isMature = isMature1;
        }
    }
    static class Warehouse{
        int id;
        int num;
        Warehouse(int id1, int num1){
            id = id1;
            num = num1;
        }
    }

    static final String GARDEN_PATH = System.getProperty("user.dir")+ File.separator+"data"+File.separator+"gardenworld.json";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取json文件中的内容。
     */
    public static GardenWorld getGardenWorld() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(GARDEN_PATH)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        Gson gson = new Gson();
        return gson.fromJson(sb.toString(),GardenWorld.class);
    }

    /**
     * 保存进度。
     */
    public static void saveGardenWorld(GardenWorld gw){
        Gson gson = new Gson();
        Write.cover(gson.toJson(gw),GARDEN_PATH);
    }

    /**
     * 根据群聊ID获得花园的基本信息，每个群聊的花园属性不变。
     */
    public static GardenType getGardenInfo(GroupMessageEvent event){
        Random random = new Random(event.getSubject().getId());
        return GardenType.values()[random.nextInt(GardenType.values().length)];
    }

    /**
     * 查询当前群
     */
    public static int getGroupGarden(GroupMessageEvent event,GardenWorld gw){
        for (int i=0;i<gw.group.size();i++){
            if (gw.group.get(i).id==event.getSubject().getId()){
                return i;
            }
        }
        return -1;
    }

    /**
     * 把一个layout中的有关种子的信息转换成GardenStatus
     */
    public static List<GardenStatus> convertToGS(GroupGarden gg){
        List<GardenStatus> list = new ArrayList<>();
        Date date = new Date();
        long now = date.getTime();
        for(int i=0;i<gg.layout.size();i++){
            list.add(new GardenStatus(gg.layout.get(i).loc,gg.layout.get(i).object,(now>=getMatureTime(gg.layout.get(i)))));
        }
        return list;
    }

    /**
     * 根据种植时间和种类的返回果实成熟的时间戳
     */
    public static long getMatureTime(GardenTiles gt){
        PlantSeed ps = PlantSeed.values()[gt.object];
        return (gt.stamp+ (long) ps.seedTime[ps.ordinal()] * 60 * 1000);
    }

    /**
     *
     */
}
