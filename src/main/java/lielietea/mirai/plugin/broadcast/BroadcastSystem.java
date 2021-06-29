package lielietea.mirai.plugin.broadcast;

import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;

public class BroadcastSystem {

    static AdministrativeAccountChecker aac = new AdministrativeAccountChecker();
    public static void sendToAllGroups(MessageEvent event, String message) throws InterruptedException {
        sendToCertainGroups(event, message, event.getBot().getGroups());
    }

    public static void sendToCertainGroups(MessageEvent event, String message, ContactList<Group> groupContactList) throws InterruptedException {
        Iterator<Group> it = groupContactList.iterator();
        while (it.hasNext()){
            Objects.requireNonNull(event.getBot().getGroup(it.next().getId())).sendMessage(message);
            Thread.sleep(3000);
        }
    }

    //测试广播消息
    public static void testSendToAllGroups(FriendMessageEvent event) throws InterruptedException {
        String message = event.getMessage().contentToString();
        if (message.contains("/broadcast_emergency ") && aac.checkIdentity(event)){
            message = message.replace("/broadcast_emergency ","");
            sendToAllGroups(event, message);
        }
    }

    public static void directlySendToGroup(FriendMessageEvent event) throws InterruptedException{
        String message = event.getMessage().contentToString();
        if (message.contains("/broadcast") && aac.checkIdentity(event)) {
            String[] splitMessage = message.split(" ");
            if (splitMessage.length!=3){
                event.getSubject().sendMessage("请使用空格分割/broadcast指示器、群号和消息");
                return;
            }
            if (!splitMessage[0].equals("/broadcast")){
                event.getSubject().sendMessage("/broadcast指示器使用不正确");
                return;
            }
            Objects.requireNonNull(event.getBot().getGroup(Long.parseLong(splitMessage[1]))).sendMessage(splitMessage[2]);
        }
    }

    public static void sendToAllFriends(FriendMessageEvent event) throws InterruptedException {
        String message = event.getMessage().contentToString();
        if (message.contains("/broadcast2f ") && aac.checkIdentity(event)) {
            message = message.replace("/broadcast2f ","");
            sendToCertainFriends(event,message,event.getBot().getFriends());
        }
    }

    public static void sendToCertainFriends(MessageEvent event, String message, ContactList<Friend> friendContactList) throws InterruptedException {
        Iterator<Friend> it = friendContactList.iterator();
        while (it.hasNext()){
            Objects.requireNonNull(event.getBot().getGroup(it.next().getId())).sendMessage(message);
            Thread.sleep(3000);
        }
    }
}
