package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.FriendMessageEvent;

public class Console {

    public static boolean hostCheck(FriendMessageEvent event,long qqid){
        return event.getSender().getId() == (qqid);
    }


}

