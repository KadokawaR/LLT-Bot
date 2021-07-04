package lielietea.mirai.plugin.game.jetpack;

import lielietea.mirai.plugin.game.mahjongriddle.MahjongRiddle;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.IOException;
import java.util.*;

public class JetPack extends BaiduAPI{

    static Timer timer = new Timer(true);
    static Map<Long, JetPackUtil.locationRecord> isInLaunchProcess = new HashMap<>();

    public static void start(GroupMessageEvent event) throws Exception {
        if (event.getMessage().contentToString().contains("/jetpack") || event.getMessage().contentToString().contains("/yes") || event.getMessage().contentToString().contains("/no")||event.getMessage().contentToString().contains("/location")) {
            List<JetPackUtil.locationRecord> recordMap = JetPackUtil.readRecord();
            Date arrivalTime;
            Location loc1;
            Location loc2;
            if (recordMap.size() <= 1) {
                arrivalTime = JetPackUtil.sdf.parse(recordMap.get(recordMap.size()-1).departureTime);
                loc1 = new Location(recordMap.get(recordMap.size()-1).lng, recordMap.get(recordMap.size()-1).lat);
                loc2 = loc1;
            } else {
                loc1 = new Location(recordMap.get(recordMap.size() - 2).lat, recordMap.get(recordMap.size() - 2).lng);
                loc2 = new Location(recordMap.get(recordMap.size()-1).lat, recordMap.get(recordMap.size()-1).lng);
                arrivalTime = JetPackUtil.sdf.parse(JetPackUtil.getEstimatedArrivalTime(loc1, loc2, recordMap.get(recordMap.size()-1).departureTime));
            }

            Date dateNow = new Date();

            //用/location查询七筒目前所在的位置
            if (event.getMessage().contentToString().contains("/location")) {
                Location currentLocation = JetPackUtil.getCurrentLocation(loc1, loc2, recordMap.get(recordMap.size()-1).departureTime);
                String currentLocationStr = C2AToString(currentLocation,event);
                if (!currentLocationStr.contains("目前暂不清楚七筒的位置。")) {
                    ImageSender.sendImageFromBufferedImage(event.getSubject(), getStaticImage(currentLocation, ZOOM_LEVEL));
                }
                event.getSubject().sendMessage(currentLocationStr);
            }

            //用/jetpack + 地址来尝试构建新飞行
            if (event.getMessage().contentToString().contains("/jetpack ")) {
                if (dateNow.after(arrivalTime)) {
                    String addressRough = event.getMessage().contentToString().replace("/jetpack ", "");
                    if (getCoord(addressRough) != null) {
                        if (Objects.requireNonNull(getCoord(addressRough)).result.comprehension >= 80) {
                            Location destinationLocation = Objects.requireNonNull(getCoord(addressRough)).result.location;
                            ImageSender.sendImageFromBufferedImage(event.getSubject(), getStaticImage(destinationLocation, ZOOM_LEVEL));
                            event.getSubject().sendMessage("是否设定这里为七筒的飞行目的地？如果确定请输入/yes ，如果取消这次飞行请输入/no。");
                            JetPackUtil.locationRecord lr = new JetPackUtil.locationRecord(destinationLocation.lng, destinationLocation.lat, addressRough, JetPackUtil.sdf.format(dateNow));
                            isInLaunchProcess.put(event.getSender().getId(), lr);

                            //180s清空isInLaunchProcess标记
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    isInLaunchProcess.remove(event.getSender().getId());
                                }
                            }, 180 * 1000);

                        } else {
                            event.getSubject().sendMessage("七筒觉得你的地址有点不太靠谱，请重新输入。");
                        }
                    } else {
                        event.getSubject().sendMessage("七筒似乎找不到你的地址，请重新输入。");
                    }
                } else {
                    event.getSubject().sendMessage("七筒目前还没有抵达目的地，请稍后尝试。");
                }
            }

            if (event.getMessage().contentToString().contains("/yes") && isInLaunchProcess.containsKey(event.getSender().getId())) {
                if (dateNow.after(arrivalTime)) {
                    Location loc3 = new Location(isInLaunchProcess.get(event.getSender().getId()).lng, isInLaunchProcess.get(event.getSender().getId()).lat);
                    event.getSubject().sendMessage("七筒正在前往" + isInLaunchProcess.get(event.getSender().getId()).locationName + "，预计抵达时间：" + JetPackUtil.getEstimatedArrivalTime(loc2, loc3, JetPackUtil.sdf.format(dateNow)));
                    JetPackUtil.writeRecord(JetPackUtil.convertLocationRecord(isInLaunchProcess.get(event.getSender().getId())));
                } else {
                    event.getSubject().sendMessage("噢，看样子有人抢先飞行了。");
                }
                isInLaunchProcess.remove(event.getSender().getId());
            }

            if (event.getMessage().contentToString().contains("/no")) {
                isInLaunchProcess.remove(event.getSender().getId());
                event.getSubject().sendMessage("已取消本次飞行。");
            }
        }
    }
}

