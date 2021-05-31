package lielietea.mirai.plugin.listener;

import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotEvent;

public class SetChannel {
    /**
     * 在GlobalEventChannel 全局监听中使用过滤器
     * 使用 qqID 注册一个 EventChannel
     */
    public static EventChannel set(long qqID){
         return GlobalEventChannel.INSTANCE.filter(ev -> ev instanceof BotEvent && ((BotEvent) ev).getBot().getId() == qqID);
    }

    
}
