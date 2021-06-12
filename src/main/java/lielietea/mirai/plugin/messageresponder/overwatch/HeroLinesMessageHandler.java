package lielietea.mirai.plugin.messageresponder.overwatch;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.RequestDrinkMessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HeroLinesMessageHandler implements MessageHandler<GroupMessageEvent> {
    static final MessageMatcher<MessageEvent> requestHeroLineMater = new RequestDrinkMessageMatcher();

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    @Override
    public boolean handleMessage(GroupMessageEvent event){
        if(requestHeroLineMater.matches(event)){
            HeroLinesCluster.reply(event);
            return true;
        }
        return false;
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
        HeroLinesCluster.reloadReplyLinesFromPreset();
    }


}
