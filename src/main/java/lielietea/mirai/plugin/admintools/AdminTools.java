package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Iterator;

public class AdminTools {
    static String addGroupInfo(Iterator<Group> listIter,String allGroupInfo){
        Group next = listIter.next();
        String allGroupInfo2 = allGroupInfo + "\n群ID " + next.getId() + "\n群名称 " + next.getName() + "\n群主ID " + next.getOwner().getId() + "\n群主昵称 " + next.getOwner().getNick() + "\n机器人权限 " + next.getBotPermission().name()+"\n";
        allGroupInfo2 = allGroupInfo2 + "----------\n";
        return allGroupInfo2;
    }

    static IdentityChecker<MessageEvent> administrativeAccountChecker = new AdministrativeAccountChecker();

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
}
