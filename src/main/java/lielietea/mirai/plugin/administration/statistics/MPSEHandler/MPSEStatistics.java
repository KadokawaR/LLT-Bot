package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Date;

public class MPSEStatistics extends MPSEProcessor{

    public static Data getXHourMPSEData(int hour,long botID){
        DataList dl = MessagePostSendEventHandler.getINSTANCE().dataList;
        Date now = new Date();
        Date hourAgo = updateMinutesByGetTime(now,-(hour*60+5));
        int friendMessageCount=0;
        int groupMessageCount=0;
        int failedMessageCount=0;
        for(Data dt : dl.datas){
            if(dt.getBn().getValue()!=botID) continue;
            if(dt.getDate().after(hourAgo)){
                friendMessageCount += dt.getFriendMessage();
                groupMessageCount += dt.getGroupMessage();
                failedMessageCount += dt.getFailedMessage();
            }
        }
        friendMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.FriendMessage);
        groupMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.GroupMessage);
        failedMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.FailedMessage);
        return new Data(friendMessageCount,groupMessageCount,failedMessageCount, MultiBotHandler.BotName.get(botID));
    }

    public static Data getAllMPSEData(long botID){
        DataList dl = MessagePostSendEventHandler.getINSTANCE().dataList;
        int friendMessageCount=0;
        int groupMessageCount=0;
        int failedMessageCount=0;
        for(Data dt : dl.datas){
            if (dt.getBn().getValue()!=botID) continue;
            friendMessageCount += dt.getFriendMessage();
            groupMessageCount += dt.getGroupMessage();
            failedMessageCount += dt.getFailedMessage();
        }
        friendMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.FriendMessage);
        groupMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.GroupMessage);
        failedMessageCount += MessagePostSendEventHandler.getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID), MessagePostSendEventHandler.MessageKind.FailedMessage);
        return new Data(friendMessageCount,groupMessageCount,failedMessageCount, MultiBotHandler.BotName.get(botID));
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
            event.getSubject().sendMessage(buildMPSEStatistics(event.getBot().getId()));
        }
    }

    public static String buildMPSEStatistics(long botID){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MPSE  \t").append("好友 \t群组 \t失败\n");
        stringBuilder.append("7 day \t").append(fromDataToString(getAllMPSEData(botID))).append("\n");
        stringBuilder.append("3 day \t").append(fromDataToString(getXHourMPSEData(24*3,botID))).append("\n");
        stringBuilder.append("1 day \t").append(fromDataToString(getXHourMPSEData(24,botID))).append("\n");
        stringBuilder.append("½ day \t").append(fromDataToString(getXHourMPSEData(12,botID))).append("\n");
        stringBuilder.append("6 hour\t").append(fromDataToString(getXHourMPSEData(6,botID))).append("\n");
        stringBuilder.append("3 hour\t").append(fromDataToString(getXHourMPSEData(3,botID))).append("\n");
        stringBuilder.append("1 hour\t").append(fromDataToString(getXHourMPSEData(1,botID)));
        return stringBuilder.toString();
    }
}
