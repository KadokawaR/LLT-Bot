package lielietea.mirai.plugin.core.messagehandler.responder.help;

import lielietea.mirai.plugin.core.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import lielietea.mirai.plugin.core.messagehandler.responder.mahjong.FortuneTeller;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctTemporary implements MessageResponder<MessageEvent> {

    static final List<MessageType> types = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));

    @Override
    public boolean match(MessageEvent event){
        return event.getMessage().contentToString().contains("/funct")||event.getMessage().contentToString().contains("/功能");
    }

    @Override
    public MessageChainPackage handle(MessageEvent event){
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event, this);
        if (match(event)){
            send(event);
        }
        return builder.build();
    }

    @Override
    public String getName() {
        return "功能";
    }

    public static void send(MessageEvent event){
        final String functionPicPath = "/pics/help/Function.png";
        try (InputStream img = FortuneTeller.class.getResourceAsStream(functionPicPath)) {
            assert img != null;
            event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return types;
    }
}
