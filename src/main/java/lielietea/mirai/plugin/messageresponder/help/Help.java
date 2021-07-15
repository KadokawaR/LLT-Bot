package lielietea.mirai.plugin.messageresponder.help;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.groupmanager.Speech;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Help implements MessageHandler<MessageEvent> {
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    @Override
    public boolean handleMessage(MessageEvent event) {
        if (event.getMessage().contentToString().equals("/help")||event.getMessage().contentToString().equals("/帮助")){
            event.getSubject().sendMessage(Speech.help);
            return true;
        }
        else if (event.getMessage().contentToString().equals("/funct")||event.getMessage().contentToString().equals("/功能")){
            event.getSubject().sendMessage(Speech.funct);
            return true;
        }
        else if (event.getMessage().contentToString().equals("/conta")||event.getMessage().contentToString().equals("/联系作者")){
            event.getSubject().sendMessage(Speech.conta);
            return true;
        }
        else if (event.getMessage().contentToString().equals("/intro")||event.getMessage().contentToString().equals("/介绍")){
            event.getSubject().sendMessage(Speech.intro);
            return true;
        }
        else if (event.getMessage().contentToString().equals("/discl")||event.getMessage().contentToString().equals("/免责协议")) {
            event.getSubject().sendMessage(Speech.disclaimer);
            return true;
        }
        else if (event.getMessage().contentToString().equals("/usage")||event.getMessage().contentToString().equals("/用法")) {
            event.getSubject().sendMessage(Speech.usage);
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
        return "帮助";
    }
}
