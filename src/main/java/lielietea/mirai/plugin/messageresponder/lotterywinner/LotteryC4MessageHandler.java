package lielietea.mirai.plugin.messageresponder.lotterywinner;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryC4MessageHandler implements MessageHandler<GroupMessageEvent> {

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    final MessageMatcher<MessageEvent> lotteryC4rMatcher;

    public LotteryC4MessageHandler(MessageMatcher<MessageEvent> lotteryC4rMatcher) {
        this.lotteryC4rMatcher = lotteryC4rMatcher;
    }


    @Override
    public boolean handleMessage(GroupMessageEvent event) {
        if(lotteryC4rMatcher.matches(event)){
            LotteryMachine.okC4(event);
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
    public String getFunctionName() {
        return "\"彩票：C4\"";
    }

    @Override
    public boolean isPermissionRequired() {
        return true;
    }


}
