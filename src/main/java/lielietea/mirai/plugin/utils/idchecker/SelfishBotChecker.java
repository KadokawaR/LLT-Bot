package lielietea.mirai.plugin.utils.idchecker;


import net.mamoe.mirai.event.events.GroupMessageEvent;

/**
 * 检测某条群消息是否是由收到该消息的Bot发出的
 */
public class SelfishBotChecker implements IIdentityChecker<GroupMessageEvent> {

    @Override
    public boolean checkIdentity(GroupMessageEvent event) {
        return event.getBot().getId() == event.getSender().getId();
    }
}
