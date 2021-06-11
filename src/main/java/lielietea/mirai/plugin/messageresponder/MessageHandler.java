package lielietea.mirai.plugin.messageresponder;

import net.mamoe.mirai.event.events.MessageEvent;

public interface MessageHandler<T extends MessageEvent> {
    void handleMessage(T event);
}
