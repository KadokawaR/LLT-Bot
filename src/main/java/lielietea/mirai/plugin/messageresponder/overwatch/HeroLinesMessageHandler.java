package lielietea.mirai.plugin.messageresponder.overwatch;


import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.messageresponder.Reloadable;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.RequestDrinkMessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HeroLinesMessageHandler implements MessageHandler<GroupMessageEvent>, Reloadable {
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


    @Override
    public void reload() {
        //目前只能从默认json重载
        //更多功能还需要编辑
        HeroLinesCluster.reloadReplyLinesFromPreset();
    }
}
