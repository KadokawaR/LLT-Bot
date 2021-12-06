package lielietea.mirai.plugin.core.messagehandler.responder.help;

import lielietea.mirai.plugin.core.messagehandler.responder.mahjong.FortuneTeller;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import javax.security.auth.Subject;
import java.io.IOException;
import java.io.InputStream;

public class DisclTemporary {
    public static boolean match(MessageEvent event){
        return event.getMessage().contentToString().contains("/discl")||event.getMessage().contentToString().contains("/免责协议");
    }

    public static void handle(MessageEvent event){
        if (match(event)){
            send(event.getSubject());
        }
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
}
