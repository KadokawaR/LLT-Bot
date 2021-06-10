package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * 检测某条消息是否是在某个群内发出
 */
public class GroupChecker implements IdentityChecker<GroupMessageEvent> {
    long targetGroupID;

    public GroupChecker(long targetGroupID){
       this.targetGroupID = targetGroupID;
    }

    @Override
    public boolean checkIdentity(GroupMessageEvent event) {
        return false;
    }
}
