package lielietea.mirai.plugin.core.responder.feastinghelper.dinnerpicker;

import lielietea.mirai.plugin.core.responder.RespondTask;
import lielietea.mirai.plugin.core.responder.MessageResponder;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class PizzaPicker implements MessageResponder<MessageEvent> {

    static final List<MessageType> TYPES = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));
    static final Pattern REG_PATTERN = Pattern.compile("(/[Pp]izza)|([oO][kK] [Pp]izza)");

    @Override
    public boolean match(String content) {
        return REG_PATTERN.matcher(content).matches();
    }

    @Override
    public RespondTask handle(MessageEvent event) {
        return RespondTask.of(event, FoodCluster.reply(event, FoodCluster.Mode.PIZZA), this);
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return TYPES;
    }

    @Override
    public String getName() {
        return "披萨";
    }
}
