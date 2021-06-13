package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.messageresponder.Reloadable;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoReplyMessageHandler implements MessageHandler<MessageEvent>, Reloadable {
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    final MessageMatcher<MessageEvent> overwatchMatcher;
    final MessageMatcher<MessageEvent> dirtyWordMatcher;
    final MessageMatcher<MessageEvent> goodbyeMatcher;

    public AutoReplyMessageHandler(MessageMatcher<MessageEvent> overwatchMatcher, MessageMatcher<MessageEvent> dirtyWordMatcher, MessageMatcher<MessageEvent> goodbyeMatcher) {
        this.overwatchMatcher = overwatchMatcher;
        this.dirtyWordMatcher = dirtyWordMatcher;
        this.goodbyeMatcher = goodbyeMatcher;
    }

    public boolean handleMessage(MessageEvent event){
        if(overwatchMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME);
            return true;
        }
        else if(dirtyWordMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_DIRTY_WORDS);
            return true;
        }
        else if(goodbyeMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.GOODBYE);
            return true;
        }
        else{
            return false;
        }
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }


    @Override
    public void reload() {
        //目前只能从默认json重载
        //更多功能还需要编辑
        AutoReplyLinesCluster.loadReplyLinesFromPreset();
    }
}
