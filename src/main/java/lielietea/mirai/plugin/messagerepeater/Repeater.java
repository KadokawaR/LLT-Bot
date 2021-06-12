package lielietea.mirai.plugin.messagerepeater;

import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;

class Repeater {
    final IdentityChecker<GroupMessageEvent> botChecker;
    String content;
    int count;

    public Repeater(){
        botChecker = new BotChecker();
        this.content = "";
        count = 0;
    }

    //处理消息，并根据情况进行复读
    public void handleMessage(GroupMessageEvent event){
        if(content.equals(event.getMessage().contentToString()) && botChecker.checkIdentity(event)){
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
}
