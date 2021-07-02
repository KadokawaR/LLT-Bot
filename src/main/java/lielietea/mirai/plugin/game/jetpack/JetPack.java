package lielietea.mirai.plugin.game.jetpack;

import lielietea.mirai.plugin.game.mahjongriddle.MahjongRiddle;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class JetPack extends BaiduAPI{

    static Timer timer = new Timer(true);
    static Map<Long, Boolean> isInLaunchProcess = new HashMap<>();

    public static void start(GroupMessageEvent event) throws Exception {

        Map<Integer, JetPackUtil.locationRecord> recordMap = JetPackUtil.readRecord();
        Location loc1 = new Location(recordMap.get(recordMap.size()-1).lat,recordMap.get(recordMap.size()-1).lng);
        Location loc2 = new Location(recordMap.get(recordMap.size()).lat,recordMap.get(recordMap.size()).lng);
        Date dateNow = new Date();
        Date actualArriveTime = JetPackUtil.sdf.parse(JetPackUtil.getEstimatedArrivalTime(loc1,loc2,recordMap.get(recordMap.size()).departureTime));

        //用/location查询七筒目前所在的位置
        if (event.getMessage().contentToString().contains("/location")){
            Location currentLocation = JetPackUtil.getCurrentLocation(loc1,loc2,recordMap.get(recordMap.size()).departureTime);
            String currentLocationStr = C2AToString(currentLocation);
            if (!currentLocationStr.contains("目前暂不清楚七筒的位置。")){
                ImageSender.sendImageFromBufferedImage(event.getSubject(),getStaticImage(currentLocation,ZOOM_LEVEL));
            }
            event.getSubject().sendMessage(currentLocationStr);
        }

        //用/jetpack + 地址来尝试构建新飞行
        if (event.getMessage().contentToString().contains("/jetpack ")){
            String addressRough = event.getMessage().contentToString().replace("/jetpack ","");
            if (getCoord(addressRough)!=null){
                if (getCoord(addressRough).result.confidence>=80){
                    Location destinationLocation = getCoord(addressRough).result.location;
                    ImageSender.sendImageFromBufferedImage(event.getSubject(),getStaticImage(destinationLocation,ZOOM_LEVEL));
                    event.getSubject().sendMessage("是否设定这里为七筒的飞行目的地？");
                }
                else{
                    event.getSubject().sendMessage("七筒觉得你的地址有点不太靠谱，请重新输入。");
                }
            }
            else{
                event.getSubject().sendMessage("七筒似乎找不到你的地址，请重新输入。");
            }
        }

    }
}

