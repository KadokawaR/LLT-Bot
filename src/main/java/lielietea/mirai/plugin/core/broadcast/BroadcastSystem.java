package lielietea.mirai.plugin.core.broadcast;

import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BroadcastSystem {

    enum BroadcastType{
        Friend,
        Group
    }

    static class AdminContact{
        Contact contact;
        Long adminID;
        AdminContact(Contact contact,Long adminID){
            this.contact = contact;
            this.adminID = adminID;
        }
    }

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    Map<AdminContact,BroadcastType> BroadcastModeList = new HashMap<>();

    BroadcastSystem() {}
    private static final BroadcastSystem INSTANCE;
    static { INSTANCE = new BroadcastSystem(); }

    public static BroadcastSystem getINSTANCE() {
        return INSTANCE;
    }

    public static void handle(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        String message = event.getMessage().contentToString();
        broadcast(event,message);
    }

    static BroadcastType getBT(MessageEvent event){
        for(AdminContact ac:getINSTANCE().BroadcastModeList.keySet()){
            if(ac.adminID==event.getSender().getId()){
                return getINSTANCE().BroadcastModeList.get(ac);
            }
        }
        return null;
    }

    static boolean isInBroadcastMode(MessageEvent event){
        for(AdminContact adminContact:getINSTANCE().BroadcastModeList.keySet()){
            if(adminContact.contact.equals(event.getSubject())&&adminContact.adminID==event.getSender().getId()) return true;
        }
        return false;
    }

    static void quitBroadcastMode(MessageEvent event){
        List<AdminContact> clonedKeySet = new ArrayList<AdminContact>(){{addAll(getINSTANCE().BroadcastModeList.keySet());}};
        for(AdminContact ac:clonedKeySet){
            if(ac.contact.equals(event.getSubject())&&ac.adminID==event.getSender().getId()) {
                getINSTANCE().BroadcastModeList.remove(ac);
                return;
            }
        }
    }

    static void broadcast(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;

        //??????????????????
        if(isInBroadcastMode(event)){

            BroadcastType bt = getBT(event);

            if(bt==null){
                event.getSubject().sendMessage("????????????????????????????????????");
                quitBroadcastMode(event);
                return;
            }

            switch(bt){
                case Group:
                    executor.schedule(new SendMessage<>(event.getBot().getGroups(), event.getMessage(),new AdminContact(event.getSubject(), event.getSender().getId())),1, TimeUnit.SECONDS);
                    break;
                case Friend:
                    executor.schedule(new SendMessage<>(event.getBot().getFriends(),event.getMessage(),new AdminContact(event.getSubject(), event.getSender().getId())),1,TimeUnit.SECONDS);
                    break;
            }

            quitBroadcastMode(event);

        } else {

            if(message.toLowerCase().contains("/broadcast")){
                enterBroadcastMode(event,message);
            }

        }

    }

    static MessageChain addAt(Group group, SingleMessage message){
        MessageChainBuilder result = new MessageChainBuilder();
        String[] splitMessage = message.contentToString().split("@");
        for(String s:splitMessage){
            if(s.startsWith("Admin")||s.startsWith("admin")){
                for(NormalMember nm:group.getMembers()){
                    if(nm.getPermission().equals(MemberPermission.ADMINISTRATOR)||nm.getPermission().equals(MemberPermission.OWNER)){
                        result.add(new At(nm.getId()));
                    }
                }
                s = s.replaceFirst("Admin|admin","");
            }

            if(s.startsWith("Owner")||s.startsWith("owner")){
                result.add(new At(group.getOwner().getId()));
                s = s.replaceFirst("Owner|owner","");
            }

            result.add(s);
        }
        return result.asMessageChain();
    }

    static MessageChain processAt(Group group, MessageChain rawMessage){
        MessageChainBuilder result = new MessageChainBuilder();
        for(SingleMessage sm:rawMessage){
            if(sm instanceof PlainText){
                if(sm.contentToString().toLowerCase().contains("@admin")||sm.contentToString().toLowerCase().contains("@owner")){
                    result.add(addAt(group,sm));
                    continue;
                }
            }
            result.add(sm);
        }
        return result.asMessageChain();
    }

    static void enterBroadcastMode(MessageEvent event, String message){

        String rawString = message.toLowerCase().replace("/broadcast", "").replace(" ", "").replace("-", "");

        switch (rawString) {
            case "f":
            case "friend":
                getINSTANCE().BroadcastModeList.put(new AdminContact(event.getSubject(), event.getSender().getId()), BroadcastType.Friend);
                break;
            case "g":
            case "group":
                getINSTANCE().BroadcastModeList.put(new AdminContact(event.getSubject(), event.getSender().getId()), BroadcastType.Group);
                break;
            default:
                event.getSubject().sendMessage("?????????????????????????????????????????????/broadcast ??? -f ?????? -g ?????????????????????");
                return;
        }

        event.getSubject().sendMessage("?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? @owner @admin ?????????????????????????????????");

        //90???????????????????????????????????????
        executor.schedule(new quitBroadcastModeAutomatically(event),90, TimeUnit.SECONDS);

    }

    static class quitBroadcastModeAutomatically implements Runnable{

        MessageEvent event;

        public quitBroadcastModeAutomatically(MessageEvent event){
            this.event = event;
        }

        @Override
        public void run(){
            if(isInBroadcastMode(event)) {
                quitBroadcastMode(event);
                event.getSubject().sendMessage("????????????????????????");
            }
        }
    }

    static class SendMessage<T extends Contact> implements Runnable{

        ContactList<T> contactList;
        MessageChain mc;
        AdminContact ac;

        public SendMessage(ContactList<T> contactList,MessageChain mc,AdminContact ac){
            this.contactList=contactList;
            this.mc=mc;
            this.ac=ac;
        }

        @Override
        public void run(){

            boolean isGroup = false;
            boolean hasJudged = false;

            for(Contact c:contactList){

                MessageChain temp = mc;

                if(!hasJudged){
                    if(c instanceof Group) isGroup=true;
                    hasJudged = true;
                }

                if(c instanceof Group){
                    temp = processAt((Group) c,mc);
                }

                c.sendMessage(temp);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            String notification = "????????????????????????";

            if(isGroup){
                notification+="?????????";
            } else {
                notification+="?????????";
            }

            ac.contact.sendMessage(notification);

        }
    }


}