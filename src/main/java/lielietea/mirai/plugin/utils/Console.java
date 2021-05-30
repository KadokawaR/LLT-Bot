package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Console {

    public static boolean hostCheck(FriendMessageEvent event,long qqid){
        return event.getSender().getId() == (qqid);
    }
}

