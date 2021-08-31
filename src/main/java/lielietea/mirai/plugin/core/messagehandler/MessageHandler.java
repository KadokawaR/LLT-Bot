package lielietea.mirai.plugin.core.messagehandler;

import lielietea.mirai.plugin.core.MessageChainPackage;
import net.mamoe.mirai.event.events.MessageEvent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface MessageHandler<T extends MessageEvent> {

    boolean match(T event);

    MessageChainPackage handle(T event);

    /**
     * 功能模块的UUID，默认根据名字自动生成
     */
    default UUID getUUID() {
        return UUID.nameUUIDFromBytes(this.getName().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 功能模块的名字
     */
    String getName();
}
