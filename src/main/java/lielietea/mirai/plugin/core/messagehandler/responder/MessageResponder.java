package lielietea.mirai.plugin.core.messagehandler.responder;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.MessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 回复处理器接口，如果要让 {@link ResponderManager} 对回复处理器进行托管，那么必须实现该类并注册
 */
public interface MessageResponder<T extends MessageEvent> extends MessageHandler<T> {

    @NotNull
    List<MessageType> types();

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
