package lielietea.mirai.plugin.overwatch;


import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.RequestDrinkMessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;


public class HeroLinesManager {
    static MessageMatcher<MessageEvent> requestHeroLineMater = new RequestDrinkMessageMatcher();

    public static void handleMessage(MessageEvent event){
        if(requestHeroLineMater.matches(event)){
            HeroLinesCluster.reply(event);
        }
    }

    /**
     * 从默认配置中重载台词
     */
    public static void reloadReplyLinesFromPreset(){
        HeroLinesCluster.reloadReplyLinesFromPreset();
    }
}
