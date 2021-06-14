package lielietea.mirai.plugin.messagerepeater;

import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.HashMap;
import java.util.Map;

public class RepeaterManager {
    Map<Long,Repeater> repeaterMap= new HashMap<>();

    static final RepeaterManager INSTANCE = new RepeaterManager();

    RepeaterManager(){}

    public static RepeaterManager getInstance(){
        return INSTANCE;
    }

    /**
     * 根据不同的群调用不同的复读器的{@link Repeater#handleMessage(GroupMessageEvent)}
     * @param event 群消息事件
     */
    public void handleMessage(GroupMessageEvent event){
        if (!repeaterMap.containsKey(event.getGroup().getId())) {
            repeaterMap.put(event.getGroup().getId(), new Repeater());
        }
        repeaterMap.get(event.getGroup().getId()).handleMessage(event);
    }

}
