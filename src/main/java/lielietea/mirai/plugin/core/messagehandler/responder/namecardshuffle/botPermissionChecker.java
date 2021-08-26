package lielietea.mirai.plugin.core.messagehandler.responder.namecardshuffle;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class botPermissionChecker {
    public static boolean check(GroupMessageEvent event) {
        return ((event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR)) || (event.getGroup().getBotPermission().equals(MemberPermission.OWNER)));
    }
}
