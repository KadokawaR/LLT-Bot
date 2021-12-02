package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Date;

public class MPSEStatistics extends MPSEProcessor{

    public static Data getXHourMPSEData(int hour){
        DataList dl = MessagePostSendEventHandler.getINSTANCE().dataList;
        Date now = new Date();
        Date hourAgo = updateMinutesByGetTime(now,-(hour*60+5));
        int friendMessageCount=0;
        int groupMessageCount=0;
        int failedMessageCount=0;
        for(Data dt : dl.datas){
            if(dt.getDate().after(hourAgo)){
                friendMessageCount += dt.getFriendMessage();
                groupMessageCount += dt.getGroupMessage();
                failedMessageCount += dt.getFailedMessage();
            }
        }
        friendMessageCount += MessagePostSendEventHandler.getINSTANCE().friendMessageCount;
        groupMessageCount += MessagePostSendEventHandler.getINSTANCE().groupMessageCount;
        failedMessageCount += MessagePostSendEventHandler.getINSTANCE().failedMessageCount;
        return new Data(friendMessageCount,groupMessageCount,failedMessageCount);
    }

    public static Data getAllMPSEData(){
        DataList dl = MessagePostSendEventHandler.getINSTANCE().dataList;
        int friendMessageCount=0;
        int groupMessageCount=0;
        int failedMessageCount=0;
        for(Data dt : dl.datas){
            friendMessageCount += dt.getFriendMessage();
            groupMessageCount += dt.getGroupMessage();
            failedMessageCount += dt.getFailedMessage();
        }
        friendMessageCount += MessagePostSendEventHandler.getINSTANCE().friendMessageCount;
        groupMessageCount += MessagePostSendEventHandler.getINSTANCE().groupMessageCount;
        failedMessageCount += MessagePostSendEventHandler.getINSTANCE().failedMessageCount;
        return new Data(friendMessageCount,groupMessageCount,failedMessageCount);
    }

    public static String fromDataToString(Data data){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("好友").append("\t").append(data.getFriendMessage()).append("\t");
        stringBuilder.append("群组").append("\t").append(data.getGroupMessage()).append("\t");
        stringBuilder.append("失败").append("\t").append(data.getFailedMessage()).append("\t");
        return stringBuilder.toString();
    }

    public static void getMPSEStatistics(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if(event.getMessage().contentToString().equals("/total")||event.getMessage().contentToString().equals("统计数据")){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("过去一周\n").append(fromDataToString(getAllMPSEData())).append("\n\n");
            stringBuilder.append("过去三天\n").append(fromDataToString(getXHourMPSEData(24*3))).append("\n\n");
            stringBuilder.append("过去一天\n").append(fromDataToString(getXHourMPSEData(24))).append("\n\n");
            stringBuilder.append("过去12小时\n").append(fromDataToString(getXHourMPSEData(12))).append("\n\n");
            stringBuilder.append("过去6小时\n").append(fromDataToString(getXHourMPSEData(6))).append("\n\n");
            stringBuilder.append("过去1小时\n").append(fromDataToString(getXHourMPSEData(1))).append("\n\n");
            event.getSubject().sendMessage(stringBuilder.toString());
        }
    }
}
