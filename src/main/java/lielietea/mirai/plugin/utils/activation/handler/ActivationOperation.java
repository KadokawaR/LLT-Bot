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
        executor.scheduleAtFixedRate(new ActivationRunnable.AutoClear(event.getBot()),1,3, TimeUnit.HOURS);
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
            event.getSubject().sendMessage("添加失败，请检查格式。");
            return;
        }

        if(Blacklist.isBlocked(userID, Blacklist.BlockKind.Friend)){
            event.getSubject().sendMessage("该账号已经被拉黑。");
            return;
        }

        ActivationDatabase.addUser(userID,event.getBot());
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.append("已授权");
        if(isAt) {
            mcb.append(new At(userID));
        } else {
            mcb.append(String.valueOf(userID));
        }
        mcb.append("在群聊内进行激活，该权限只可使用一次。");

        event.getSubject().sendMessage(mcb.asMessageChain());

    }

    static void activate(GroupMessageEvent event,String message){

        if(!message.equals("激活七筒")) return;

        if(!IdentityUtil.isAdmin(event)) {
            if (!ActivationDatabase.containsUser(event.getSender().getId(),event.getBot())) {
                if (!ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())) {
                    if (Harbor.isReachingPortLimit(event)) return;
                    MessageChainBuilder mcb = new MessageChainBuilder().append(new At(event.getSender().getId()))
                            .append("您不在授权列表内，请添加公众聊天群 932617537 并按照提示激活。七筒在被激活前不会响应任何消息。");
                    event.getSubject().sendMessage(mcb.asMessageChain());
                    Harbor.count(event);
                    return;
                }
            }

            if (ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())) {
                if (Harbor.isReachingPortLimit(event)) return;
                event.getSubject().sendMessage("该群已经激活，请勿重复激活。");
                Harbor.count(event);
                return;
            }
        }

        activateActions(event);

    }

    //由人来激活
    static void activateActions(GroupMessageEvent event){
        ActivationDatabase.combinedActivationOperation(event.getGroup().getId(),event.getSender().getId(),event.getBot());

        ContactUtil.handlePostActivation(event.getGroup());

        MessageUtil.notifyDevGroup("七筒已经在群组"+event.getGroup().getName()+
                "（"+event.getGroup().getId()+")被激活，激活人为"+
                event.getSender().getNick()+"("+event.getSender().getId()+")。");
    }

    static void activateActions(Group group){
        ActivationDatabase.combinedActivationOperation(group.getId(),0L,group.getBot());

        ContactUtil.handlePostActivation(group);

        MessageUtil.notifyDevGroup("七筒已经在群组"+group.getName()+
                "（"+group.getId()+")被激活。");
    }

    static void activateActions(long groupID, Bot bot){

        Group group = bot.getGroup(groupID);

        if(group!=null) {

            ActivationDatabase.combinedActivationOperation(group.getId(), 0L, group.getBot());

            ContactUtil.handlePostActivation(group);

            MessageUtil.notifyDevGroup("七筒已经在群组" + group.getName() +
                    "（" + group.getId() + ")被激活。");

        }
    }

    static void deactivateAction(Group group){
        ActivationDatabase.deleteGroup(group.getId(),group.getBot());
        group.sendMessage("该群已经被取消激活，将会自动退群。如有问题请联系开发者。");
        ContactUtil.tryQuitGroup(group.getId());
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

        event.getSubject().sendMessage("激活数据库已经与目前的群聊列表同步，目前共有"+currentGroupID.size()+"个群聊。");

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

                // 允许在多bot存在的群里进行操作而不会重复提示
                for(Bot bot: Bot.getInstances()){
                    for(Group group:bot.getGroups()){
                        if(group.getId()==groupID) return;
                    }
                }

                event.getSubject().sendMessage("该七筒没有添加进此群聊。");
                return;
            }

            if(ActivationDatabase.isActivated(groupID,event.getBot())){
                ActivationDatabase.deleteGroup(groupID,event.getBot());

                deactivateAction(Objects.requireNonNull(event.getBot().getGroup(groupID)));

                event.getSubject().sendMessage("已经停止激活群聊"+groupID);
            } else {
                event.getSubject().sendMessage("该群组并未在激活列表内。");
            }

        } else {

            event.getSubject().sendMessage("输入账号格式错误，请检查。");

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

                // 允许在多bot存在的群里进行操作而不会重复提示
                for(Bot bot: Bot.getInstances()){
                    for(Group group:bot.getGroups()){
                        if(group.getId()==groupID) return;
                    }
                }

                event.getSubject().sendMessage("没有任何七筒添加进此群聊。");
                return;
            }

            if(!ActivationDatabase.isActivated(groupID,event.getBot())){
                ActivationDatabase.addGroup(groupID,event.getBot());
                activateActions(Objects.requireNonNull(event.getBot().getGroup(groupID)));
                event.getSubject().sendMessage("已经激活群聊"+groupID);

            } else {
                event.getSubject().sendMessage("该群组已经被激活。");

            }

        } else {

            event.getSubject().sendMessage("输入账号格式错误，请检查。");

        }

    }

    public static boolean match(GroupMessageEvent event){
        String message = event.getMessage().contentToString();
        return IdentityUtil.isAdmin(event)||ActivationDatabase.isActivated(event.getGroup().getId(),event.getBot())||message.equals("激活七筒");
    }

    public static void handle(MessageEvent event){
        String message = event.getMessage().contentToString();
        permit(event,message);
        align(event,message);
        deactivateByAdmin(event,message);
        activateByAdmin(event,message);
        if(event instanceof GroupMessageEvent) activate((GroupMessageEvent) event,message);
    }

}
