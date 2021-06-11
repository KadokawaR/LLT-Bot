package lielietea.mirai.plugin.messageresponder.autoreply;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Greeting {

    public static void handleMessage(GroupMessageEvent event){
        if(event.getMessage().contentToString().equals("hi")){
            //群内发送
            event.getGroup().sendMessage("hi");
            //向发送者私聊发送消息
            event.getSender().sendMessage("hi");
        }
    }
}
