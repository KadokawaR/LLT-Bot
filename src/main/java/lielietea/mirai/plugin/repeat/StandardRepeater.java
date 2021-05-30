package lielietea.mirai.plugin.repeat;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class StandardRepeater {
    String content;
    int count;

    public StandardRepeater(){
        this.content = "";
        count = 0;
    }

    public void check(GroupMessageEvent event){
        if(content.equals(event.getMessage().contentToString()) && !isBot(event)){
            count++;
        } else {
            count=0;
            content=event.getMessage().contentToString();
        }

        if (count==2){
            event.getSubject().sendMessage(content);
            count=0;
        }
    }

    public static boolean isBot(GroupMessageEvent event){
        Long bot_1 = 2955808839L;
        Long bot_2 = 2285463810L;
        return event.getSender().getId() == bot_1 || event.getSender().getId() == bot_2;
    }
}
