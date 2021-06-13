package lielietea.mirai.plugin.utils.admintools;

import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AdminTools {
    static String addGroupInfo(Iterator<Group> listIter,String allGroupInfo){
        Group next = listIter.next();
        String allGroupInfo2 = allGroupInfo + "\n群ID " + next.getId() + "\n群名称 " + next.getName() + "\n群主ID " + next.getOwner().getId() + "\n群主昵称 " + next.getOwner().getNick() + "\n机器人权限 " + next.getBotPermission().name()+"\n";
        allGroupInfo2 = allGroupInfo2 + "----------\n";
        return allGroupInfo2;
    }

    public static boolean AdminChecker(MessageEvent event){
        final List<Long> adminList = new ArrayList<>(Arrays.asList(
                2955808839L,
                1811905537L,
                459405942L
        ));
        for(Long qqID : adminList){
            if(qqID == event.getSender().getId()) return true;
        }
        return false;
    }

    public static void getGroupList(FriendMessageEvent event){
        if (AdminChecker(event)){
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
