package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.messageresponder.MessageRespondCenter;
import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Iterator;

public class AdminTools {

    static final IdentityChecker<MessageEvent> administrativeAccountChecker = new AdministrativeAccountChecker();

    static final AdminTools INSTANCE = new AdminTools();

    public static AdminTools getINSTANCE() {
        return INSTANCE;
    }

    public void handleAdminCommand(FriendMessageEvent event){
        //TODO：回复消息长度不能长于5000字
        if (event.getMessage().contentToString().contains("/group")) {
            getGroupList(event);
        }

        if (event.getMessage().contentToString().contains("/friend")) {
            getFriendList(event);
        }

        if (event.getMessage().contentToString().contains("/reload")) {
            MessageRespondCenter.getINSTANCE().reload(event);
        }
    }

    String addGroupInfo(Iterator<Group> listIter,String allGroupInfo){
        Group next = listIter.next();
        String allGroupInfo2 = allGroupInfo + "\n群ID " + next.getId() + "\n群名称 " + next.getName() + "\n群主ID " + next.getOwner().getId() + "\n群主昵称 " + next.getOwner().getNick() + "\n机器人权限 " + next.getBotPermission().name()+"\n";
        allGroupInfo2 = allGroupInfo2 + "----------\n";
        return allGroupInfo2;
    }

    void getGroupList(FriendMessageEvent event){
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

    String addFriendInfo(Iterator<Friend> listIter, String allFriendInfo){
        Friend next = listIter.next();
        return allFriendInfo + "\n好友ID " + next.getId() + "\n好友名称 " + next.getNick() + "\n";
    }

    void getFriendList(FriendMessageEvent event){
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


}
