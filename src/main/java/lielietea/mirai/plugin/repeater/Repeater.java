package lielietea.mirai.plugin.repeater;

import lielietea.mirai.plugin.utils.IDChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Repeater {
    String content;
    int count;

    public Repeater(){
        this.content = "";
        count = 0;
    }

    /**
     * 处理消息，并根据情况进行复读
     * @param event 群消息事件
     * @return <code>true</code> 需要复读 <code>false</code> 无需复读
     */
    public boolean handleMessage(GroupMessageEvent event){
        if(content.equals(event.getMessage().contentToString()) && !IDChecker.isBot(event)){
            count++;
        } else {
            count=0;
            content=event.getMessage().contentToString();
        }

        if (count==2){
            event.getSubject().sendMessage(content);
            count=0;
            return true;
        }
        return false;
    }
}
