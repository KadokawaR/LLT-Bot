package lielietea.mirai.plugin.messageresponder.lotterywinner;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryBummerMessageHandler implements MessageHandler<GroupMessageEvent> {

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP_PERMISSION_REQUIRED));

    final MessageMatcher<MessageEvent> lotteryBummerMatcher;

    public LotteryBummerMessageHandler(MessageMatcher<MessageEvent> lotteryBummerMatcher) {
        this.lotteryBummerMatcher = lotteryBummerMatcher;
    }


    @Override
    public boolean handleMessage(GroupMessageEvent event) {
        if(lotteryBummerMatcher.matches(event)){
            LotteryMachine.okBummer(event);
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
    public String getName() {
        return "彩票：Bummer";
    }
}
