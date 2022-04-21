package lielietea.mirai.plugin.core.responder.basic;


import lielietea.mirai.plugin.core.responder.RespondTask;
import lielietea.mirai.plugin.core.responder.MessageResponder;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AntiOverwatch implements MessageResponder<MessageEvent> {
    static final List<MessageType> TYPES = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));

    static final List<Pattern> REG_PATTERN = new ArrayList<>();

    static {
        {
            REG_PATTERN.add(Pattern.compile(".*" + "([Oo])verwatch" + ".*"));
            REG_PATTERN.add(Pattern.compile(".*" + "守望((先锋)|(屁股))" + ".*"));
            REG_PATTERN.add(Pattern.compile(".*" + "([玩打])((OW)|(ow))" + ".*"));
        }
    }

    @Override
    public boolean match(String content) {
        for (Pattern pattern : REG_PATTERN) {
            if (pattern.matcher(content).matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RespondTask handle(MessageEvent event) {
        return RespondTask.of(event, AutoReplyLinesCluster.reply(AutoReplyLinesCluster.ReplyType.ANTI_OVERWATCH_GAME), this);
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return TYPES;
    }


    @Override
    public String getName() {
        return "自动回复：反OW";
    }
}
