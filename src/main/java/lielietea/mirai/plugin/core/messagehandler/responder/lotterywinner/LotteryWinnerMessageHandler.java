package lielietea.mirai.plugin.core.messagehandler.responder.lotterywinner;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class LotteryWinnerMessageHandler implements MessageResponder<GroupMessageEvent> {

    static final List<MessageType> TYPES = new ArrayList<>(Collections.singletonList(MessageType.GROUP));
    static final Pattern REG_PATTERN = Pattern.compile("((/[Ww]inner)|([oO][kK] [Ww]inner))|(/(([Ll]ottery)|(乐透)|(彩票)))");


    @Override
    public boolean match(GroupMessageEvent event) {
        return REG_PATTERN.matcher(event.getMessage().contentToString()).matches();
    }

    @Override
    public MessageChainPackage handle(GroupMessageEvent event) {
        return LotteryMachine.okWinner(event, new MessageChainPackage.Builder(event, this));
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return TYPES;
    }

    @Override
    public String getName() {
        return "Winner";
    }
}