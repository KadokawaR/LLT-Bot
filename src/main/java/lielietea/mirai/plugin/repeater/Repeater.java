package lielietea.mirai.plugin.repeater;

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
        if(content.equals(event.getMessage().contentToString()) && !isBot(event.getSender().getId())){
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

    static boolean isBot(Long senderID){
        Long bot_1 = 2955808839L;
        return senderID == bot_1;
    }
}
