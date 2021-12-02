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
        stringBuilder.append(data.getFriendMessage()).append("\t");
        stringBuilder.append(data.getGroupMessage()).append("\t");
        stringBuilder.append(data.getFailedMessage()).append("\t");
        return stringBuilder.toString();
    }

    public static void getMPSEStatistics(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if(event.getMessage().contentToString().equals("/total")||event.getMessage().contentToString().equals("统计数据")){
            event.getSubject().sendMessage(buildMPSEStatistics());
        }
    }

    public static String buildMPSEStatistics(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MPSE").append("\t好友\t群组\t失败\n");
        stringBuilder.append("7 days\t").append(fromDataToString(getAllMPSEData())).append("\n");
        stringBuilder.append("3 days\t").append(fromDataToString(getXHourMPSEData(24*3))).append("\n");
        stringBuilder.append("24H\t\t").append(fromDataToString(getXHourMPSEData(24))).append("\n");
        stringBuilder.append("12H\t\t").append(fromDataToString(getXHourMPSEData(12))).append("\n");
        stringBuilder.append("6H\t\t").append(fromDataToString(getXHourMPSEData(6))).append("\n");
        stringBuilder.append("1H\t\t").append(fromDataToString(getXHourMPSEData(1)));
        return stringBuilder.toString();
    }
}
