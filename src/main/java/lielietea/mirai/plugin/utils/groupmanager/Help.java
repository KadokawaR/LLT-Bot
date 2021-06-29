package lielietea.mirai.plugin.utils.groupmanager;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Help {
    public static void getInGroup(GroupMessageEvent event){
        if (event.getMessage().contentToString().equals("/help")||event.getMessage().contentToString().equals("帮助")){
            event.getSubject().sendMessage(Speech.help);
        }
        if (event.getMessage().contentToString().equals("/funct")||event.getMessage().contentToString().equals("帮助")){
            event.getSubject().sendMessage(Speech.funct);
        }
        if (event.getMessage().contentToString().equals("/conta")||event.getMessage().contentToString().equals("帮助")){
            event.getSubject().sendMessage(Speech.conta);
        }
        if (event.getMessage().contentToString().equals("/intro")||event.getMessage().contentToString().equals("帮助")){
            event.getSubject().sendMessage(Speech.intro);
        }
    }
}
