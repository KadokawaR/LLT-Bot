package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;

public class BroadcastSystem {

    public static void sendToAllGroups(MessageEvent event, String message) throws InterruptedException {
        sendToCertainGroups(event, message, event.getBot().getGroups());
    }

    public static void sendToCertainGroups(MessageEvent event, String message, ContactList<Group> groupContactList) throws InterruptedException {
        Iterator<Group> it = groupContactList.iterator();
        while (it.hasNext()){
            Objects.requireNonNull(event.getBot().getGroup(it.next().getId())).sendMessage(message);
            Thread.sleep(6000);
        }
    }

    //测试广播消息
    public static void testSendToGroup(FriendMessageEvent event) throws InterruptedException {
        String message = event.getMessage().contentToString();
        AdministrativeAccountChecker aac = new AdministrativeAccountChecker();
        if (message.contains("/broadcast") && aac.checkIdentity(event)){
            //message = message.replace("/broadcast ","");
            sendToAllGroups(event, "目前正在广播七筒的测试消息。请无视。");
        }
    }
}
