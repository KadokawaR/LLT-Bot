package lielietea.mirai.plugin.messageresponder.lotterywinner;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryWinnerMessageHandler implements MessageHandler<GroupMessageEvent> {

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    final MessageMatcher<MessageEvent> lotteryWinnerMatcher;
    final MessageMatcher<MessageEvent> lotteryBummerMatcher;
    final MessageMatcher<MessageEvent> lotteryC4rMatcher;

    public LotteryWinnerMessageHandler(MessageMatcher<MessageEvent> lotteryWinnerMatcher, MessageMatcher<MessageEvent> lotteryBummerMatcher, MessageMatcher<MessageEvent> lotteryC4rMatcher) {
        this.lotteryWinnerMatcher = lotteryWinnerMatcher;
        this.lotteryBummerMatcher = lotteryBummerMatcher;
        this.lotteryC4rMatcher = lotteryC4rMatcher;
    }


    @Override
    public boolean handleMessage(GroupMessageEvent event) {
        if(lotteryWinnerMatcher.matches(event)){
            LotteryWinner.okWinner(event);
            return true;
        } else if(lotteryBummerMatcher.matches(event)){
            LotteryWinner.okBummer(event);
            return true;
        } else if(lotteryC4rMatcher.matches(event)){
            LotteryWinner.okC4(event);
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }
}
