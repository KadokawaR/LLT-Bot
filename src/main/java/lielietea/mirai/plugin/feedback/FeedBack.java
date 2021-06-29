package lielietea.mirai.plugin.feedback;

import lielietea.mirai.plugin.utils.idchecker.GroupID;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public class FeedBack {
    public static void get(FriendMessageEvent event){
        String message = event.getMessage().contentToString();
        //todo 黑名单系统
        if (message.contains("意见反馈")){
            message = "来自"+String.valueOf(event.getSubject().getId())+" - "+event.getSubject().getNick()+"的反馈意见：\n\n"+message;
            event.getBot().getGroup(GroupID.DEV).sendMessage(message);
            event.getSubject().sendMessage("您的意见我们已经收到。");
        }
    }
}
