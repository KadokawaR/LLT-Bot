package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.Event;

public interface IdentityChecker<T extends Event> {
    boolean checkIdentity(T event);
}
