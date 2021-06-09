package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.Event;

public interface IIdentityChecker<T extends Event> {
    boolean checkIdentity(T event);
}
