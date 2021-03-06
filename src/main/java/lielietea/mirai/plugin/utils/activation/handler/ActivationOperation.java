package lielietea.mirai.plugin.utils.activation.handler;

import lielietea.mirai.plugin.administration.blacklist.Blacklist;
import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.utils.ContactUtil;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.MessageUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ActivationOperation {

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void autoClear(BotOnlineEvent event){
        executor.scheduleAtFixedRate(new ActivationRunnable.AutoClear(event.getBot()),1,24, TimeUnit.HOURS);
    }

    static void permit(MessageEvent event,String message){

        if(!IdentityUtil.isAdmin(event)) return;

        message = message.toLowerCase();
        if(!message.startsWith("/permit")) return;

        long userID = 0L;
        boolean isAt = false;

        if(message.contains("@")) {
            if (event instanceof GroupMessageEvent) {
                for (SingleMessage sm : event.getMessage()) {
                    if (sm instanceof At) {
                        userID = ((At) sm).getTarget();
                        isAt = true;
                        break;
                    }
                }
            }
        }

        if(userID==0L){
            String number = Pattern.compile("[^0-9]").matcher(message.replace("/permit","").replaceAll(" ","")).replaceAll(" ").trim();
            try{
                userID = Long.parseLong(number);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(userID==0L) {
            event.getSubject().sendMessage("?????????????????????????????????");
            return;
        }

        if(Blacklist.isBlocked(userID, Blacklist.BlockKind.Friend)){
            event.getSubject().sendMessage("???????????????????????????");
            return;
        }

        ActivationDatabase.addUser(userID,event.getBot());
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.append("?????????");
        if(isAt) {
            mcb.append(new At(userID));
        } else {
            mcb.append(String.valueOf(userID));
        }
        mcb.append("?????????????????????????????????????????????????????????");

        event.getSubject().sendMessage(mcb.asMessageChain());

    }

    static void activate(GroupMessageEvent event,String message){

        if(!message.equals("????????????")) return;

        if(!IdentityUtil.isAdmin(event)) {
            if (!ActivationDatabase.containsUser(event.getSender().getId(),event.getBot())) {
                if (!ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())) {
                    if (Harbor.isReachingPortLimit(event)) return;
                    MessageChainBuilder mcb = new MessageChainBuilder().append(new At(event.getSender().getId()))
                            .append("??????????????????????????????????????????????????? 932617537 ????????????????????????????????????????????????????????????????????????");
                    event.getSubject().sendMessage(mcb.asMessageChain());
                    Harbor.count(event);
                    return;
                }
            }

            if (ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())) {
                if (Harbor.isReachingPortLimit(event)) return;
                event.getSubject().sendMessage("??????????????????????????????????????????");
                Harbor.count(event);
                return;
            }
        }

        activateActions(event);

    }

    //???????????????
    static void activateActions(GroupMessageEvent event){
        ActivationDatabase.combinedActivationOperation(event.getGroup().getId(),event.getSender().getId(),event.getBot());

        ContactUtil.handlePostActivation(event.getGroup());

        MessageUtil.notifyDevGroup("?????????????????????"+event.getGroup().getName()+
                "???"+event.getGroup().getId()+")????????????????????????"+
                event.getSender().getNick()+"("+event.getSender().getId()+")???");
    }

    static void activateActions(Group group){
        ActivationDatabase.combinedActivationOperation(group.getId(),0L,group.getBot());

        ContactUtil.handlePostActivation(group);

        MessageUtil.notifyDevGroup("?????????????????????"+group.getName()+
                "???"+group.getId()+")????????????");
    }

    static void activateActions(long groupID, Bot bot){

        Group group = bot.getGroup(groupID);

        if(group!=null) {

            ActivationDatabase.combinedActivationOperation(group.getId(), 0L, group.getBot());

            ContactUtil.handlePostActivation(group);

            MessageUtil.notifyDevGroup("?????????????????????" + group.getName() +
                    "???" + group.getId() + ")????????????");

        }
    }

    static void deactivateAction(Group group){
        executor.schedule(new ActivationRunnable.QuitGroup(group),1,TimeUnit.SECONDS);
    }

    static void align(MessageEvent event, String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.equalsIgnoreCase("/align -g")) return;

        ContactList<Group> currentGroups = event.getBot().getGroups();
        List<Long> currentGroupID = new ArrayList<>();

        for(Group group:currentGroups){
            if(!ActivationDatabase.isActivated(group.getId(), event.getBot())) currentGroupID.add(group.getId());
        }

        ActivationDatabase.addGroups(currentGroupID,event.getBot());

        event.getSubject().sendMessage("??????????????????????????????????????????????????????????????????"+currentGroupID.size()+"????????????");

    }

    static void deactivateByAdmin(MessageEvent event, String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.toLowerCase().startsWith("/deactivate")) return;

        String strID = Pattern.compile("[^0-9]").matcher(message.replace("/deactivate","").replaceAll(" ","")).replaceAll(" ").trim();

        long groupID = 0L;

        try{
            groupID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(groupID!=0L){

            if(event.getBot().getGroup(groupID)==null){

                // ????????????bot????????????????????????????????????????????????
                for(Bot bot: Bot.getInstances()){
                    for(Group group:bot.getGroups()){
                        if(group.getId()==groupID) return;
                    }
                }

                event.getSubject().sendMessage("????????????????????????????????????");
                return;
            }

            if(ActivationDatabase.isActivated(groupID,event.getBot())){
                ActivationDatabase.deleteGroup(groupID,event.getBot());

                deactivateAction(Objects.requireNonNull(event.getBot().getGroup(groupID)));

                event.getSubject().sendMessage("????????????????????????"+groupID);
            } else {
                event.getSubject().sendMessage("????????????????????????????????????");
            }

        } else {

            event.getSubject().sendMessage("???????????????????????????????????????");

        }

    }

    static void activateByAdmin(MessageEvent event, String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.toLowerCase().startsWith("/activate")) return;

        String strID = Pattern.compile("[^0-9]").matcher(message.replace("/activate","").replaceAll(" ","")).replaceAll(" ").trim();

        long groupID = 0L;

        try{
            groupID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(groupID!=0L){

            if(event.getBot().getGroup(groupID)==null){

                // ????????????bot????????????????????????????????????????????????
                for(Bot bot: Bot.getInstances()){
                    for(Group group:bot.getGroups()){
                        if(group.getId()==groupID) return;
                    }
                }

                event.getSubject().sendMessage("???????????????????????????????????????");
                return;
            }

            if(!ActivationDatabase.isActivated(groupID,event.getBot())){
                ActivationDatabase.addGroup(groupID,event.getBot());
                activateActions(Objects.requireNonNull(event.getBot().getGroup(groupID)));
                event.getSubject().sendMessage("??????????????????"+groupID);

            } else {
                event.getSubject().sendMessage("???????????????????????????");

            }

        } else {

            event.getSubject().sendMessage("???????????????????????????????????????");

        }

    }

    public static boolean match(GroupMessageEvent event){
        String message = event.getMessage().contentToString();
        return IdentityUtil.isAdmin(event)||ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())||message.equals("????????????");
    }

    public static void handle(MessageEvent event){
        String message = event.getMessage().contentToString();
        permit(event,message);
        align(event,message);
        deactivateByAdmin(event,message);
        activateByAdmin(event,message);
        clearByAdmin(event,message);
        if(event instanceof GroupMessageEvent) activate((GroupMessageEvent) event,message);
    }

    public static void autoClearActions(Bot bot){
        List<Long> currentGroupIDs = new ArrayList<>();

        for(Group group:bot.getGroups()){
            currentGroupIDs.add(group.getId());
        }

        ActivationDatabase.updateActivationDataGroupList(bot,currentGroupIDs);
        ActivationDatabase.updateEntryRecordList(bot,currentGroupIDs);
        ActivationDatabase.quitOutOfDateGroups(bot);
    }

    public static void clearByAdmin(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.equalsIgnoreCase("/reset activation")) return;
        autoClearActions(event.getBot());
        event.getSubject().sendMessage("????????????Activation???");
    }

}
