package lielietea.mirai.plugin.core.messagehandler.responder.help;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import lielietea.mirai.plugin.utils.exception.NoHandlerMethodMatchException;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;


public class Help implements MessageResponder<MessageEvent> {
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));
    static final Map<Predicate<MessageEvent>, String> MAP = new HashMap<>();

    static {
        {
            MAP.put(event -> event.getMessage().contentToString().equals("/help") || event.getMessage().contentToString().equals("/帮助"), Speech.HELP);
            MAP.put(event -> event.getMessage().contentToString().equals("/funct") || event.getMessage().contentToString().equals("/功能"), Speech.FUNCT);
            MAP.put(event -> event.getMessage().contentToString().equals("/conta") || event.getMessage().contentToString().equals("/联系作者"), Speech.CONTA);
            MAP.put(event -> event.getMessage().contentToString().equals("/intro") || event.getMessage().contentToString().equals("/介绍"), Speech.INTRO);
            MAP.put(event -> event.getMessage().contentToString().equals("/discl") || event.getMessage().contentToString().equals("/免责协议"), Speech.DISCLAIMER);
            MAP.put(event -> event.getMessage().contentToString().equals("/usage") || event.getMessage().contentToString().equals("/用法"), Speech.USAGE);
        }
    }

    @Override
    public boolean match(MessageEvent event) {
        for (Predicate<MessageEvent> predicate : MAP.keySet()) {
            if (predicate.test(event)) return true;
        }
        return false;
    }

    @Override
    public MessageChainPackage handle(MessageEvent event) throws NoHandlerMethodMatchException {
        for (Map.Entry<Predicate<MessageEvent>, String> entry : MAP.entrySet()) {
            if (entry.getKey().test(event))
                return MessageChainPackage.getDefaultImpl(event, entry.getValue(), this);
        }
        throw new NoHandlerMethodMatchException();
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }


    @Override
    public String getName() {
        return "帮助";
    }
}
