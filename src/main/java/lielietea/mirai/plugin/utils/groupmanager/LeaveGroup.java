package lielietea.mirai.plugin.utils.groupmanager;

import lielietea.mirai.plugin.utils.idchecker.GroupID;
import net.mamoe.mirai.event.events.BotLeaveEvent;

import java.util.Objects;

public class LeaveGroup {
    public static void cancelFlag(BotLeaveEvent event) {
        JoinGroup.joinGroupEventFlag.remove(event.getGroupId());
        String leaveMessage = "七筒已经从" + String.valueOf(event.getGroupId()) + "-" + event.getGroup().getName() + " 离开。";
        Objects.requireNonNull(event.getBot().getGroup(GroupID.DEV)).sendMessage(leaveMessage);
    }
}
