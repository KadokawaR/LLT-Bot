package lielietea.mirai.plugin.utils.activation;

import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.utils.ContactUtil;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.MessageUtil;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ActivationHandler {

    static void register(MessageEvent event,String message){

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

        ActivationDatabase.addUser(userID);
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.append("已授权账号");
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
            if (!ActivationDatabase.containUser(event.getSender().getId())) {
                if (!ActivationDatabase.isActivated(event.getGroup().getId())) {
                    MessageChainBuilder mcb = new MessageChainBuilder().append(new At(event.getSender().getId()))
                            .append("您不在授权列表内，请添加公众聊天群 932617537 并按照提示激活。七筒在被激活前不会响应任何消息。");
                    event.getSubject().sendMessage(mcb.asMessageChain());
                    return;
                }
            }

            if (ActivationDatabase.isActivated(event.getGroup().getId())) {
                if (Harbor.isReachingPortLimit(event)) return;
                event.getSubject().sendMessage("该群已经激活，请勿重复激活。");
                Harbor.count(event);
                return;
            }
        }

        ActivationDatabase.addGroup(event.getGroup().getId());
        ActivationDatabase.deleteUser(event.getSender().getId());
        ActivationDatabase.addRecord(event.getGroup().getId(),event.getSender().getId());

        ContactUtil.handlePostActivation(event);

        MessageUtil.notifyDevGroup("七筒已经在群组"+event.getGroup().getName()+
                "（"+event.getGroup().getId()+")被激活，激活人为"+
                event.getSender().getNick()+"("+event.getSender().getId()+")。");

    }

    static void align(MessageEvent event, String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.equalsIgnoreCase("/align -g")) return;

        ContactList<Group> currentGroups = event.getBot().getGroups();
        List<Long> currentGroupID = new ArrayList<>();

        for(Group group:currentGroups){
            currentGroupID.add(group.getId());
        }

        ActivationDatabase.addGroup(currentGroupID);

        event.getSubject().sendMessage("激活数据库已经与目前的群聊列表同步。");

    }

    public static boolean match(GroupMessageEvent event){
        String message = event.getMessage().contentToString();
        return IdentityUtil.isAdmin(event)||ActivationDatabase.isActivated(event)||message.equals("激活七筒");
    }

    public static void handle(MessageEvent event){
        String message = event.getMessage().contentToString();
        register(event,message);
        align(event,message);
        if(event instanceof GroupMessageEvent) activate((GroupMessageEvent) event,message);
    }

}
