package lielietea.mirai.plugin.core.messagehandler.responder.help;

import lielietea.mirai.plugin.core.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisclTemporary implements MessageResponder<MessageEvent> {

    static final List<MessageType> types = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));
    @Override
    public boolean match(MessageEvent event){
        return event.getMessage().contentToString().contains("/discl")||event.getMessage().contentToString().contains("/免责协议");
    }

    @Override
    public MessageChainPackage handle(MessageEvent event){
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event, this);
        if (match(event)){
            send(event.getSubject());
        }
        return builder.build();
    }

    @Override
    public String getName() {
        return "声明";
    }

    public static void send(Contact contact){
        final String disclaimerPicPath = "/pics/help/Disclaimer.png";
        try (InputStream img = DisclTemporary.class.getResourceAsStream(disclaimerPicPath)) {
            assert img != null;
            contact.sendMessage(Contact.uploadImage(contact, img));
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
