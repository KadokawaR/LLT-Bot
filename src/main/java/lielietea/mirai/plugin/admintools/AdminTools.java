package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.messageresponder.MessageRespondCenter;
import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;

public class AdminTools {

    static final IdentityChecker<MessageEvent> administrativeAccountChecker = new AdministrativeAccountChecker();

    static final AdminTools INSTANCE = new AdminTools();

    public static AdminTools getINSTANCE() {
        return INSTANCE;
    }

    public void handleAdminCommand(FriendMessageEvent event){
        if (event.getMessage().contentToString().contains("/group")) {
            try {
                getGroupList(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (event.getMessage().contentToString().contains("/friend")) {
            try {
                getFriendList(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    void getGroupList(FriendMessageEvent event) throws InterruptedException {
        if (administrativeAccountChecker.checkIdentity(event)){
            Iterator<Group> listIter = event.getBot().getGroups().stream().iterator();
            int size = event.getBot().getGroups().getSize();
            String[] allGroupInfo = new String[size/20+1];
            Arrays.fill(allGroupInfo, "");
            int count =0;
            int countString = 0;
            while (listIter.hasNext()){
                if (count>19){
                    count=0;
                    countString+=1;
                }
                allGroupInfo[countString] = addGroupInfo(listIter,allGroupInfo[countString]);
                count+=1;
            }
            for (int i=0;i<=countString;i++){
                event.getSubject().sendMessage(allGroupInfo[i]);
                Thread.sleep(1000);
            }
            event.getSubject().sendMessage("七筒目前的群数量是："+String.valueOf(size));
        }
    }

    String addFriendInfo(Iterator<Friend> listIter, String allFriendInfo){
        Friend next = listIter.next();
        return allFriendInfo + "\n好友ID " + next.getId() + "\n好友名称 " + next.getNick() + "\n";
    }

    void getFriendList(FriendMessageEvent event) throws InterruptedException {
        if (administrativeAccountChecker.checkIdentity(event)){
            Iterator<Friend> listIter = event.getBot().getFriends().stream().iterator();
            int size = event.getBot().getFriends().getSize();
            String[] allFriendInfo = new String[size/30+1];
            Arrays.fill(allFriendInfo, "");
            int count =0;
            int countString = 0;;
            while (listIter.hasNext()){
                if (count>29){
                    count=0;
                    countString+=1;
                }
                allFriendInfo[countString] = addFriendInfo(listIter,allFriendInfo[countString]);
                count+=1;
            }
            for (int i=0;i<=countString;i++){
                event.getSubject().sendMessage(allFriendInfo[i]);
                Thread.sleep(1000);
            }
            event.getSubject().sendMessage("七筒目前的好友数量是："+String.valueOf(size));
        }
    }


}
