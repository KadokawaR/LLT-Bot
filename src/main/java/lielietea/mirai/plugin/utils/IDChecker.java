package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class IDChecker {
    public static boolean isThisQQID(MessageEvent event, long qqID) {
        return qqID == event.getSubject().getId();
    }

    public static boolean isThisQQGroup(GroupMessageEvent event, long groupID) {
        return groupID == event.getSubject().getId();
    }

    public static boolean isThisQQMember(GroupMessageEvent event, long groupID, long qqID) {
        return isThisQQGroup(event, groupID) && isThisQQID(event, qqID);
    }
}