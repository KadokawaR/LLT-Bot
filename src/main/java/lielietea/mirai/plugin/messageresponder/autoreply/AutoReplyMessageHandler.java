package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.DirtyWordMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.GoodbyeMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.MentionOverwatchMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoReplyMessageHandler implements MessageHandler<MessageEvent> {
    static final MessageMatcher<MessageEvent> overwatchMater = new MentionOverwatchMessageMatcher();
    static final MessageMatcher<MessageEvent> dirtyWordMater = new DirtyWordMessageMatcher();
    static final MessageMatcher<MessageEvent> goodbyeMatcher = new GoodbyeMessageMatcher();

    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    public boolean handleMessage(MessageEvent event){
        if(overwatchMater.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME);
            return true;
        }
        else if(dirtyWordMater.matches(event)){
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


    /**
     * 从默认配置中重载台词
     */
    public static void reloadReplyLinesFromPreset(){
        AutoReplyLinesCluster.loadReplyLinesFromPreset();
    }
}
