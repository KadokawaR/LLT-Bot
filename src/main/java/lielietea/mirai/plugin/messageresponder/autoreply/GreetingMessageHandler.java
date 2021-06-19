package lielietea.mirai.plugin.messageresponder.autoreply;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GreetingMessageHandler implements MessageHandler<GroupMessageEvent> {
    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    @Override
    public boolean handleMessage(GroupMessageEvent event){
        if(event.getMessage().contentToString().equals("hi")){
            //群内发送
            event.getGroup().sendMessage("hi");
            //向发送者私聊发送消息
            //event.getSender().sendMessage("hi");
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }

    @Override
    public String getName() {
        return "自动回复：打招呼";
    }
}
