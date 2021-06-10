package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.events.MessageEvent;

/**
 * 检测某条消息是否由某个账号发出
 */
public class AccountChecker implements IdentityChecker<MessageEvent> {
    final long targetQQID;

    public AccountChecker(long targetQQID){
       this.targetQQID = targetQQID;
    }

    @Override
    public boolean checkIdentity(MessageEvent event) {
        return event.getSender().getId() == targetQQID;
    }
}
