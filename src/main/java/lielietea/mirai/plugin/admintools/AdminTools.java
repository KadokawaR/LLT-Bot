package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Iterator;

public class AdminTools {

    static final IdentityChecker<MessageEvent> administrativeAccountChecker = new AdministrativeAccountChecker();

    static String addGroupInfo(Iterator<Group> listIter,String allGroupInfo){
        Group next = listIter.next();
        String allGroupInfo2 = allGroupInfo + "\n群ID " + next.getId() + "\n群名称 " + next.getName() + "\n群主ID " + next.getOwner().getId() + "\n群主昵称 " + next.getOwner().getNick() + "\n机器人权限 " + next.getBotPermission().name()+"\n";
        allGroupInfo2 = allGroupInfo2 + "----------\n";
        return allGroupInfo2;
    }

    public static void getGroupList(FriendMessageEvent event){
        if (administrativeAccountChecker.checkIdentity(event)){
            Iterator<Group> listIter = event.getBot().getGroups().stream().iterator();
            String allGroupInfo = "";
            while (listIter.hasNext()){
                allGroupInfo = addGroupInfo(listIter,allGroupInfo);
                //event.getSender().sendMessage(allGroupInfo);
            }
            event.getSubject().sendMessage(allGroupInfo);
        }
    }

    static String addFriendInfo(Iterator<Friend> listIter, String allFriendInfo){
        Friend next = listIter.next();
        return allFriendInfo + "\n好友ID " + next.getId() + "\n好友名称 " + next.getNick() + "\n";
    }

    public static void getFriendList(FriendMessageEvent event){
        if (administrativeAccountChecker.checkIdentity(event)){
            Iterator<Friend> listIter = event.getBot().getFriends().stream().iterator();
            String allFriendInfo = "";
            while (listIter.hasNext()){
                allFriendInfo = addFriendInfo(listIter,allFriendInfo);
                //event.getSender().sendMessage(allGroupInfo);
            }
            event.getSubject().sendMessage(allFriendInfo);
        }
    }

    public static void getFriendAvatar(FriendMessageEvent event){
        String message = event.getMessage().contentToString();
        if (administrativeAccountChecker.checkIdentity(event)&&message.contains("/avatar")){
            String[] splitMessage = message.split(" ");
            if (splitMessage.length!=2){
                event.getSubject().sendMessage("请使用空格分割/avatar指示器和QQ号");
                return;
            }
            if (!splitMessage[0].equals("/avatar")){
                event.getSubject().sendMessage("/avatar指示器使用不正确");
                return;
            }
            event.getSubject().sendMessage(event.getBot().getFriend(Long.parseLong(splitMessage[1])).getAvatarUrl());
        }
    }
}
