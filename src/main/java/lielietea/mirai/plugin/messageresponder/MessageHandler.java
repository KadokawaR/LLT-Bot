package lielietea.mirai.plugin.messageresponder;

import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * 回复处理器接口，如果要让 {@link MessageRespondCenter} 对回复处理器进行托管，那么必须实现该类并注册
 *
 * @param <T> 必须继承自MessageEvent
 */
public interface MessageHandler<T extends MessageEvent> {

    /**
     * 这个方法是用来处理传入回复处理器的消息事件的，是回复处理器的入口。
     * @param event 传入消息事件
     * @return 该类必须返回一个布尔值，代表传入的消息事件是否由该处理器处理。
     */
    boolean handleMessage(T event) throws IOException;

    /**
     * 这个方法返回该回复处理器可以处理的消息事件的类型。返回值不能为空。
     * {@link MessageType}
     * @return 一个消息事件类型组成的列表
     */
    @NotNull
    List<MessageType> types();

    /**
     * 返回该回复处理器的UUID
     * @return UUID
     */
    default UUID getUUID(){
        return UUID.nameUUIDFromBytes(this.getFunctionName().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回该回复处理器所代表的功能的名字
     * @return 功能名字
     */
    String getFunctionName();

    /**
     * 该回复处理器是否是测试特性？
     * 仅支持处理群消息事件
     * @return 如果是测试特性，那么返回true
     */
    default boolean isOnBeta(){
        return false;
    }

    /**
     * 该回复处理器是否需要管理员权限？
     * 仅支持处理群消息事件
     * @return 如果需要权限，那么返回true
     */
    default boolean isPermissionRequired() {
        return false;
    }

    default void onclose(){ }

    enum MessageType{
        GROUP,
        FRIEND,
        STRANGER,
        TEMP
    }
}
