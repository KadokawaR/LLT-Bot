package lielietea.mirai.plugin.admintools;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.messageresponder.MessageRespondCenter;
import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StatisticController {
    //还要多考虑一下这个统计应该怎么做
    public static final Table<Long,UUID,Integer> data = HashBasedTable.create();
    public static final Map<Long,Integer> minuteCount = new HashMap<>();
    static boolean resetStartFlag = false;
    static final int MAX_THRESHOLD = 10;

    public static void countIn(long groupID, UUID serviceID){
        if (data.contains(groupID,serviceID)) {
            data.put(groupID,serviceID, data.get(groupID,serviceID)+1);
        } else {
            data.put(groupID,serviceID,1);
        }
    }

    /**
     * 增加每分钟计数
     */
    public static void addMinuteCount(long groupID){
        if(!minuteCount.containsKey(groupID)){
            minuteCount.put(groupID,1);
        } else {
            minuteCount.put(groupID,minuteCount.get(groupID)+1);
        }
    }

    /**
     * 检测是否每分钟超过 MAX_THRESHOLD
     */
    public static boolean checkGroupCount(GroupMessageEvent event){
        long groupID = event.getGroup().getId();
        if(!minuteCount.containsKey(groupID)){
            return true;
        }
        return minuteCount.get(groupID) <= MAX_THRESHOLD;
    }

    /**
     * 重置分钟计数器，只启动一次
     */
    public static void resetMinuteCount(){
        if (!resetStartFlag) {
            Timer t = new Timer();
            resetStartFlag = true;
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    minuteCount.clear();
                    System.out.println("分钟计数器已重置");
                }
            }, 60 * 1000, 60 * 1000);
        }
    }

    /**
     * 读取statistics
     */
    public static void getStatistics(FriendMessageEvent event){
        AdministrativeAccountChecker accountChecker = new AdministrativeAccountChecker();
        if (accountChecker.checkIdentity(event)&&event.getMessage().contentToString().contains("/statistics")){
            event.getSubject().sendMessage(String.valueOf(MessageRespondCenter.getINSTANCE().getGroupStatistics(false)));
        }
    }
}
