package lielietea.mirai.plugin.utils.groupmanager;

import net.mamoe.mirai.event.events.BotJoinGroupEvent;

import java.util.HashMap;
import java.util.Map;

public class JoinGroup {
    
    static Map<Long, Boolean> joinGroupEventFlag = new HashMap<>();
    public static void sendNotice(BotJoinGroupEvent event){
        if (!joinGroupEventFlag.containsKey(event.getGroupId())){
            joinGroupEventFlag.put(event.getGroupId(), false);
        }
        if (!joinGroupEventFlag.get(event.getGroupId())){
            event.getGroup().sendMessage("你好，这里是烈烈茶店长七筒。");
            joinGroupEventFlag.put(event.getGroupId(), true);
        }
        else{
            joinGroupEventFlag.remove(event.getGroupId());
        }
    }
}
