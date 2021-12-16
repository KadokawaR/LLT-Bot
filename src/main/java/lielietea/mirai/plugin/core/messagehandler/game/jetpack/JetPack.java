package lielietea.mirai.plugin.core.messagehandler.game.jetpack;

import lielietea.mirai.plugin.core.messagehandler.responder.mahjong.FortuneTeller;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.fileutils.Write;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil.*;

public class JetPack extends BaiduAPI {

    static final Map<Long, JetPackUtil.locationRecord> isInLaunchProcess = new HashMap<>();
    static final List<Long> hasCancelled = new ArrayList<>();
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    static final String JETPACK_INTRO_PATH = "/pics/jetpack/jetpack.png";

    public static void start(MessageEvent event) throws Exception {
        if (event.getMessage().contentToString().contains("/jetpack") || event.getMessage().contentToString().equals("/yes") || event.getMessage().contentToString().equals("/no") || event.getMessage().contentToString().equals("/location") || event.getMessage().contentToString().equals("/abort") || event.getMessage().contentToString().equals("/landing") || event.getMessage().contentToString().equals("/record")) {
            List<JetPackUtil.locationRecord> recordMap = JetPackUtil.readRecord();
            Date arrivalTime;
            Location loc1;
            Location loc2;
            loc1 = new Location(recordMap.get(recordMap.size() - 2).lng, recordMap.get(recordMap.size() - 2).lat);
            loc2 = new Location(recordMap.get(recordMap.size() - 1).lng, recordMap.get(recordMap.size() - 1).lat);
            arrivalTime = JetPackUtil.sdf.parse(JetPackUtil.getEstimatedArrivalTime(loc1, loc2, recordMap.get(recordMap.size() - 1).departureTime));

            Date dateNow = new Date();

            //用/location查询七筒目前所在的位置
            if (event.getMessage().contentToString().contains("/location")) {
                Location currentLocation = JetPackUtil.getCurrentLocation(loc1, loc2, recordMap.get(recordMap.size() - 1).departureTime);
                String currentLocationStr = C2AToString(currentLocation);
                String destinationName = recordMap.get(recordMap.size() - 1).locationName;
                //判断是否还在飞行途中
                if (dateNow.after(arrivalTime)) {
                    if (!currentLocationStr.contains("目前暂不清楚七筒的位置。")) {
                        ImageSender.sendImageFromBufferedImage(event.getSubject(), MapDrawer.drawAvatar(getStaticImage(currentLocation, zoomLevelCalculatorS(currentLocation))));
                    }
                    event.getSubject().sendMessage(currentLocationStr);
                } else {
                    if (!currentLocationStr.contains("目前暂不清楚七筒的位置。")) {
                        ImageSender.sendImageFromBufferedImage(event.getSubject(), MapDrawer.drawTrack(loc1, loc2, currentLocation, JetPackUtil.zoomLevelCalculator(loc1, loc2), getStaticImage(currentLocation, JetPackUtil.zoomLevelCalculator(loc1, loc2))));
                    }
                    event.getSubject().sendMessage(currentLocationStr + "。" + "七筒正在前往" + destinationName + "，预计抵达时间：" + JetPackUtil.sdf.format(arrivalTime));
                }
            }

            //初始化的jetpack指令
            if (event.getMessage().contentToString().equals("/jetpack") || event.getMessage().contentToString().equals("/jetpack ")) {
                event.getSubject().sendMessage("喷气背包时间到！\n\n输入/jetpack+空格+任意地点，七筒便可以直接飞行到目的地。输入/location查询七筒目前所在的位置，或者当前的飞行路线。");
                try (InputStream img = JetPack.class.getResourceAsStream(JETPACK_INTRO_PATH)) {
                    assert img != null;
                    event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //用/jetpack + 地址来尝试构建新飞行
            if (event.getMessage().contentToString().contains("/jetpack ")) {
                if (dateNow.after(arrivalTime)) {
                    String addressRough = event.getMessage().contentToString().replace("/jetpack ", "");
                    if (getCoord(addressRough) != null) {
                        if (Objects.requireNonNull(getCoord(addressRough)).result.comprehension >= 80) {
                            Location destinationLocation = Objects.requireNonNull(getCoord(addressRough)).result.location;
                            ImageSender.sendImageFromBufferedImage(event.getSubject(), getStaticImage(destinationLocation, zoomLevelCalculatorS(destinationLocation)));
                            event.getSubject().sendMessage("是否设定这里为七筒的飞行目的地？如果确定请输入/yes ，如果取消这次飞行请输入/no。");
                            JetPackUtil.locationRecord lr = new JetPackUtil.locationRecord(destinationLocation.lng, destinationLocation.lat, addressRough, JetPackUtil.sdf.format(dateNow));
                            isInLaunchProcess.put(event.getSender().getId(), lr);

                            //180s清空isInLaunchProcess标记
                            executor.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (isInLaunchProcess.containsKey(event.getSender().getId())) {
                                        event.getSubject().sendMessage("由于确认时间超时，本次飞行已关闭。");
                                    }
                                    isInLaunchProcess.remove(event.getSender().getId());
                                }
                            }, 180,TimeUnit.SECONDS);

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
                    String destinationName = isInLaunchProcess.get(event.getSender().getId()).locationName;
                    long flyingDuration = (long) (JetPackUtil.getFlightDuration(loc2, loc3) * 60 * 1000);
                    long flyingDurationM = (long) (JetPackUtil.getFlightDuration(loc2, loc3));
                    Write.append(JetPackUtil.convertLocationRecord(isInLaunchProcess.get(event.getSender().getId())), TXT_PATH);
                    event.getSubject().sendMessage("七筒正在使用喷气背包前往" + destinationName + "，预计抵达时间：" + JetPackUtil.getEstimatedArrivalTime(loc2, loc3, sdf.format(dateNow)));
                    isInLaunchProcess.remove(event.getSender().getId());
                    //抵达之后告知
                    executor.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(hasCancelled.contains(event.getSubject().getId())){
                                hasCancelled.remove(event.getSubject().getId());
                                return;
                            }
                            event.getSubject().sendMessage("七筒已经抵达飞行目的地：" + destinationName + "，飞行时长为" + (int) flyingDurationM + "分钟");
                        }
                    }, flyingDuration, TimeUnit.MILLISECONDS);
                } else {
                    event.getSubject().sendMessage("噢，看样子有人抢先飞行了。");
                }
                isInLaunchProcess.remove(event.getSender().getId());
            }

            if (event.getMessage().contentToString().contains("/no") && isInLaunchProcess.containsKey(event.getSender().getId())) {
                isInLaunchProcess.remove(event.getSender().getId());
                event.getSubject().sendMessage("已取消本次飞行。");
            }

            //后门
            //直接抵达 重新写入一遍最后一条记录
            if (event.getMessage().contentToString().contains("/abort") && IdentityUtil.isAdmin(event)) {
                JetPackUtil.locationRecord newRecord = new JetPackUtil.locationRecord(recordMap.get(recordMap.size() - 1).lng, recordMap.get(recordMap.size() - 1).lat, recordMap.get(recordMap.size() - 1).locationName, JetPackUtil.sdf.format(dateNow));
                Write.append(JetPackUtil.convertLocationRecord(newRecord), TXT_PATH);
                event.getSubject().sendMessage("飞行已经被终止，七筒提前抵达目的地。");
                return;
            }
            //迫降在当地 写入两条记录
            if (event.getMessage().contentToString().contains("/landing") && IdentityUtil.isAdmin(event)) {
                Location currentLocation = JetPackUtil.getCurrentLocation(loc1, loc2, recordMap.get(recordMap.size() - 1).departureTime);
                JetPackUtil.locationRecord newRecord = new JetPackUtil.locationRecord(currentLocation.lng, currentLocation.lat, "迫降点", JetPackUtil.sdf.format(dateNow));
                //写入两次
                Write.append(JetPackUtil.convertLocationRecord(newRecord), TXT_PATH);
                Write.append(JetPackUtil.convertLocationRecord(newRecord), TXT_PATH);
                if (!C2AToString(currentLocation).contains("目前暂不清楚七筒的位置。")) {
                    event.getSubject().sendMessage(C2AToString(currentLocation) + "，已经成功迫降。");
                    hasCancelled.add(event.getSubject().getId());
                    return;
                } else {
                    event.getSubject().sendMessage("七筒已经成功迫降。");
                    hasCancelled.add(event.getSubject().getId());
                    return;
                }
            }
            //获得最后25条飞行记录
            if (event.getMessage().contentToString().contains("/record")) {
                StringBuilder recordStr = new StringBuilder();
                List<locationRecord> lrList = JetPackUtil.readRecord();
                int lrListSize = lrList.size();
                int index = 0;
                for (locationRecord lr : lrList) {
                    if(index>=lrListSize-10) recordStr.append(lr.locationName).append(",").append((double) Math.round(lr.lng * 100) / 100).append(",").append((double) Math.round(lr.lat * 100) / 100).append("\n");
                    index+=1;
                }
                event.getSubject().sendMessage(recordStr.toString());
            }
        }
    }

    public enum jetpackType {Help, Location, FlyStart, Nothing}
}