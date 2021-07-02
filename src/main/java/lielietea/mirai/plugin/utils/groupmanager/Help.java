package lielietea.mirai.plugin.utils.groupmanager;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class Help {
    public static void detect(MessageEvent event){
        if (event.getMessage().contentToString().equals("/help")||event.getMessage().contentToString().equals("/帮助")){
            event.getSubject().sendMessage(Speech.help);
        }
        else if (event.getMessage().contentToString().equals("/funct")||event.getMessage().contentToString().equals("/功能")){
            event.getSubject().sendMessage(Speech.funct);
        }
        else if (event.getMessage().contentToString().equals("/conta")||event.getMessage().contentToString().equals("/联系作者")){
            event.getSubject().sendMessage(Speech.conta);
        }
        else if (event.getMessage().contentToString().equals("/intro")||event.getMessage().contentToString().equals("/介绍")){
            event.getSubject().sendMessage(Speech.intro);
        }
        else if (event.getMessage().contentToString().equals("/discl")||event.getMessage().contentToString().equals("/免责协议")) {
            event.getSubject().sendMessage(Speech.disclaimer);
        }
        else if (event.getMessage().contentToString().equals("/usage")||event.getMessage().contentToString().equals("/用法")) {
            event.getSubject().sendMessage(Speech.usage);
        }
    }
}
