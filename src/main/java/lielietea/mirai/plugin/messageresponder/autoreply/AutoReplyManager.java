package lielietea.mirai.plugin.messageresponder.autoreply;


import lielietea.mirai.plugin.utils.messagematcher.DirtyWordMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.GoodbyeMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.MentionOverwatchMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;


public class AutoReplyManager {
    static final MessageMatcher<MessageEvent> overwatchMater = new MentionOverwatchMessageMatcher();
    static final MessageMatcher<MessageEvent> dirtyWordMater = new DirtyWordMessageMatcher();
    static final MessageMatcher<MessageEvent> goodbyeMatcher = new GoodbyeMessageMatcher();

    public static void handleMessage(MessageEvent event){
        if(overwatchMater.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME);
        }
        if(dirtyWordMater.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_DIRTY_WORDS);
        }
        if(goodbyeMatcher.matches(event)){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.GOODBYE);
        }
    }

    /**
     * 从默认配置中重载台词
     */
    public static void reloadReplyLinesFromPreset(){
        AutoReplyLinesCluster.loadReplyLinesFromPreset();
    }
}
