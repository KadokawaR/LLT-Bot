package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiDirtyWordMessageHandler implements MessageHandler<MessageEvent>{
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    final MessageMatcher<MessageEvent> dirtyWordMatcher;

    public AntiDirtyWordMessageHandler(MessageMatcher<MessageEvent> dirtyWordMatcher) {
        this.dirtyWordMatcher = dirtyWordMatcher;
    }

    public boolean handleMessage(MessageEvent event){
        if(dirtyWordMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_DIRTY_WORDS);
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
        return "自动回复：反脏话";
    }
}
