package lielietea.mirai.plugin.core.messagehandler.responder.help;

import lielietea.mirai.plugin.core.messagehandler.responder.mahjong.FortuneTeller;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.IOException;
import java.io.InputStream;

public class FunctTemporary {
    public static boolean match(MessageEvent event){
        return event.getMessage().contentToString().contains("/funct")||event.getMessage().contentToString().contains("/功能");
    }

    public static void handle(MessageEvent event){
        if (match(event)){
            send(event);
        }
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
}
