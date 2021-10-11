package lielietea.mirai.plugin.core.messagehandler.responder.autoreply;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NudgeEvent;

public class NudgeBack {
    public static void returnNudge(NudgeEvent event){
        if (event.getTarget().equals(event.getBot())){
            event.getTarget().nudge();
        }
    }

    public static void mentionNudge(GroupMessageEvent event){
        if (event.getMessage().contentToString().contains("七筒")){
            event.getSender().nudge();
        }
    }
}
