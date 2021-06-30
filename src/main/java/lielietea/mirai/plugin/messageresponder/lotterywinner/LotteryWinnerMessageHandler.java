package lielietea.mirai.plugin.messageresponder.lotterywinner;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryWinnerMessageHandler implements MessageHandler<GroupMessageEvent> {

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    final MessageMatcher<MessageEvent> lotteryWinnerMatcher;

    public LotteryWinnerMessageHandler(MessageMatcher<MessageEvent> lotteryWinnerMatcher) {
        this.lotteryWinnerMatcher = lotteryWinnerMatcher;
    }


    @Override
    public boolean handleMessage(GroupMessageEvent event) throws IOException {
        if(lotteryWinnerMatcher.matches(event)){
            LotteryMachine.okWinner(event);
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
        return "彩票：Winner";
    }
}
