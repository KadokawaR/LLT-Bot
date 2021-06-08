package lielietea.mirai.plugin.overwatch;


import lielietea.mirai.plugin.utils.MessageChecker;
import net.mamoe.mirai.event.events.MessageEvent;


public class HeroLinesManager {
    public static void handleMessage(MessageEvent event){
        //我先按照在主类里的临时写一个哈
        if(MessageChecker.isHeroLines(event.getMessage().contentToString())){
            HeroLinesCluster.reply(event);
        }
    }

    /**
     * 从默认配置中重载台词
     */
    public static void reloadReplyLinesFromPreset(){
        HeroLinesCluster.loadReplyLinesFromPreset();
    }
}
