package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.messageresponder.Reloadable;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiOverwatchMessageHandler implements MessageHandler<MessageEvent>, Reloadable {
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    final MessageMatcher<MessageEvent> overwatchMatcher;

    public AntiOverwatchMessageHandler(MessageMatcher<MessageEvent> overwatchMatcher) {
        this.overwatchMatcher = overwatchMatcher;

    }

    public boolean handleMessage(MessageEvent event){
        if(overwatchMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME);
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
        return "自动回复：反OW";
    }


    @Override
    public boolean reload() {
        return AutoReplyLinesCluster.loadReplyLinesFromPreset();
    }
}
