package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

public interface MessageMatcher<T extends MessageEvent> {
    boolean matches(T event);
}
