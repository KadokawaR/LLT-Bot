package lielietea.mirai.plugin.autoreply;


import lielietea.mirai.plugin.utils.MessageChecker;
import net.mamoe.mirai.event.events.MessageEvent;


public class AutoReplyManager {
    public static void handleMessage(MessageEvent event){
        if(MessageChecker.isTalkOverwatch(event.getMessage().contentToString())){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME);
        }
        if(MessageChecker.isDirtyWord(event.getMessage().contentToString())){
            AutoReplyLinesCluster.reply(event, AutoReplyLinesCluster.ReplyType.ANTI_DIRTY_WORDS);
        }
        if(MessageChecker.isGoodbye(event.getMessage().contentToString())){
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
