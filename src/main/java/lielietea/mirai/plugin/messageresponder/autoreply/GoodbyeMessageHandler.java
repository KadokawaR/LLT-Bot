package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoodbyeMessageHandler implements MessageHandler<MessageEvent>{
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    final MessageMatcher<MessageEvent> goodbyeMatcher;

    public GoodbyeMessageHandler(MessageMatcher<MessageEvent> goodbyeMatcher) {
        this.goodbyeMatcher = goodbyeMatcher;
    }

    public boolean handleMessage(MessageEvent event){
        if(goodbyeMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.GOODBYE);
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
    public String getFunctionName() {
        return null;
    }
}
