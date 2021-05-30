package lielietea.mirai.plugin.repeater;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.HashMap;
import java.util.Map;

public class RepeaterManager {

    static RepeaterManager instance = new RepeaterManager();
    Map<Long,Repeater> repeaterMap= new HashMap<>();

    RepeaterManager(){}

    public static RepeaterManager getInstance(){
        return instance;
    }

    /**
     * 根据不同的群调用不同的复读器的{@link Repeater#handleMessage(GroupMessageEvent)}
     * @param event 群消息事件
     * @return <code>true</code> 需要复读 <code>false</code> 无需复读
     */
    public boolean handleMessage(GroupMessageEvent event){
        if(repeaterMap.containsKey(event.getGroup().getId())){
            return repeaterMap.get(event.getGroup().getId()).handleMessage(event);
        } else {
            repeaterMap.put(event.getGroup().getId(),new Repeater());
            return repeaterMap.get(event.getGroup().getId()).handleMessage(event);
        }
    }

}
