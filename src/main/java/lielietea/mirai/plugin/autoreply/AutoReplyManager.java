package lielietea.mirai.plugin.autoreply;

import lielietea.mirai.plugin.utils.MessageChecker;
import net.mamoe.mirai.event.events.MessageEvent;

public class AutoReplyManager {
    public static void handleMessage(MessageEvent event){
        if(MessageChecker.isTalkOverwatch(event.getMessage().contentToString())){
            AutoAntiOverwatch.reply(event);
        }
        if(MessageChecker.isDirtyWord(event.getMessage().contentToString())){
            AutoAntiDirtyWords.reply(event);
        }
        if(MessageChecker.isGoodbye(event.getMessage().contentToString())){
            AutoSayGoodbye.reply(event);
        }
    }
}
