package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NudgeEvent;

public class Nudge {
    public static void returnNudge(NudgeEvent event){
        if (event.getTarget().equals(event.getBot())){
            event.getFrom().nudge().sendTo(event.getSubject());
            event.getSubject().sendMessage("啥事？");
        }
    }

    public static void mentionNudge(GroupMessageEvent event){
        if (event.getMessage().contentToString().contains(String.valueOf(event.getBot().getId()))){
            event.getSender().nudge().sendTo(event.getSubject());
        }
    }
}
