package lielietea.mirai.plugin.utils.groupmanager;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Help {
    public static void getInGroup(GroupMessageEvent event){
        if (event.getMessage().contentToString().equals("/help")){
            event.getSubject().sendMessage("七爷来了。");
        }
    }
}
