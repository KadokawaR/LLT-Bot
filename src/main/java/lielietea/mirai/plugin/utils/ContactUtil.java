package lielietea.mirai.plugin.utils;

import lielietea.mirai.plugin.core.responder.help.DisclTemporary;
import lielietea.mirai.plugin.utils.activation.handler.ActivationDatabase;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ContactUtil {
    public static final String JOIN_GROUP = "七筒来了！很高兴为您服务。\n\n在使用本 bot 之前，请仔细阅读下方的免责协议，如有任何问题请与开发者联系。\n\n"+
            "如需要联系七筒的开发者和体验七筒功能，请添加公众聊天群：932617537。\n" +
            "如需要获得七筒的最新消息，防止在封号后与七筒走丢，请添加通知群：948979109。";

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    // 决定是否接收加群邀请
    public static void handleGroupInvitation(BotInvitedJoinGroupRequestEvent event) {
        int minute = new Random().nextInt(3)+1;
        executor.schedule(new AddGroup(event),minute,TimeUnit.MINUTES);
    }

    // 加群Runnable

    static class AddGroup implements Runnable{

        private final BotInvitedJoinGroupRequestEvent event;
        AddGroup(BotInvitedJoinGroupRequestEvent event){
            this.event = event;
        }
        @Override
        public void run() {

            if(IdentityUtil.isAdmin(Objects.requireNonNull(event.getInvitor()).getId())) {
                event.accept();
                return;
            }

            if (!MultiBotHandler.canAcceptGroup(event.getBot().getId())) {
                if(MultiBotHandler.canSendNotice(event.getBot())) {
                    Objects.requireNonNull(event.getInvitor()).sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
                }
                event.ignore();
            } else {
                event.accept();
                notifyDevWhenJoinGroup(event);
                ActivationDatabase.addRecord(event.getGroupId(),event.getInvitorId(),event.getBot());
            }
        }
    }

    // 决定是否接收好友请求
    public static void handleFriendRequest(NewFriendRequestEvent event) {
        int minute = new Random().nextInt(3)+5;
        executor.schedule(new AddFriend(event),minute,TimeUnit.MINUTES);
    }

    // 加好友Runnable
    static class AddFriend implements Runnable{
        private final NewFriendRequestEvent event;
        AddFriend(NewFriendRequestEvent event){
            this.event=event;
        }

        @Override
        public void run(){
            if(IdentityUtil.isAdmin(event.getFromId())) event.accept();
            if (!MultiBotHandler.canAcceptFriend(event.getBot().getId())) {
                event.reject(false);
                return;
            }
            event.accept();
        }
    }

    // 处理加群事件
    public static void handleJoinGroup(BotJoinGroupEvent.Invite event) {
        if(IdentityUtil.DevGroup.isDevGroup(event.getGroupId())){
            event.getGroup().sendMessage("反正是开发组，咱没话说。");
            return;
        }
        executor.schedule(() -> {
            //管理员判定
            if(!IdentityUtil.isAdmin((event).getInvitor().getId())) {
                if (!MultiBotHandler.canAcceptGroup(event.getBot().getId())) {
                    event.getGroup().sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
                    if(MultiBotHandler.canSendNotice(event.getBot())) {
                        (event).getInvitor().sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
                    }
                    event.getGroup().quit();
                    String content = "由于目前七筒不接受添加群聊，已经从 " + event.getGroup().getName() + "(" + event.getGroup().getId() + ")" + "离开。";
                    MessageUtil.notifyDevGroup(content, event.getBot().getId());
                    return;
                }

                if (event.getGroup().getMembers().getSize() < 7) {
                    event.getGroup().sendMessage("七筒目前不接受加入7人以下的群聊，将会自动退群。");
                    if(MultiBotHandler.canSendNotice(event.getBot())) {
                        (event).getInvitor().sendMessage("七筒目前不接受加入7人以下的群聊，将会自动退群。");
                    }
                    event.getGroup().quit();
                    String content = (event).getInvitor().getNick() + "(" + (event).getInvitor().getId() + ")尝试邀请七筒加入一个少于7人的群聊。";
                    MessageUtil.notifyDevGroup(content, event.getBot().getId());
                    return;
                }
            }

            //检测是否有其他七筒
            for(NormalMember nm:event.getGroup().getMembers()){
                for(Bot bot:Bot.getInstances()){
                    if (bot.getId()==(event.getBot().getId())) continue;
                    if (nm.getId()==bot.getId()){
                        if(MultiBotHandler.BotName.get(nm.getId()).ordinal()<MultiBotHandler.BotName.get(bot.getId()).ordinal()) continue;//很重要的判定！两个七筒只有一个退群！
                        event.getGroup().sendMessage("检测到其他在线七筒账户在此群聊中，本机器人将自动退群。");
                        executor.schedule(() -> event.getGroup().quit(),15,TimeUnit.SECONDS);
                        return;
                    }
                }
            }

            // 正常通过群邀请加群
            boolean containsOldCitung = IdentityUtil.containsUnusedBot(event.getGroup());

            notifyDevWhenJoinGroup(event);
            sendJoinGroupNotice(event,containsOldCitung);
            ActivationDatabase.addRecord(event.getGroupId(),event.getInvitor().getId(),event.getBot());

        },15,TimeUnit.SECONDS);

    }

    // 处理加群事件
    public static void handleJoinGroup(BotJoinGroupEvent.Active event) {
        if(IdentityUtil.DevGroup.isDevGroup(event.getGroupId())){
            event.getGroup().sendMessage("反正是开发组，咱没话说。");
            return;
        }
        executor.schedule(() -> {

                if (!MultiBotHandler.canAcceptGroup(event.getBot().getId())) {
                    event.getGroup().sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
                    event.getGroup().quit();
                    String content = "由于目前七筒不接受添加群聊，已经从 " + event.getGroup().getName() + "(" + event.getGroup().getId() + ")" + "出逃。";
                    MessageUtil.notifyDevGroup(content, event.getBot().getId());
                    return;
                }

                if (event.getGroup().getMembers().getSize() < 7) {
                    event.getGroup().sendMessage("七筒目前不接受加入7人以下的群聊。");
                    event.getGroup().quit();
                    String content = "有人尝试尝试邀请七筒加入一个少于7人的群聊 "+event.getGroup().getName()+"("+event.getGroup().getId()+")，七筒已经离开";
                    MessageUtil.notifyDevGroup(content, event.getBot().getId());
                    return;
                }


            //检测是否有其他七筒
            for(NormalMember nm:event.getGroup().getMembers()){
                for(Bot bot:Bot.getInstances()){
                    if (bot.getId()==(event.getBot().getId())) continue;
                    if (nm.getId()==bot.getId()){
                        if(MultiBotHandler.BotName.get(nm.getId()).ordinal()<MultiBotHandler.BotName.get(bot.getId()).ordinal()) continue;//很重要的判定！两个七筒只有一个退群！
                        event.getGroup().sendMessage("检测到其他在线七筒账户在此群聊中，本机器人将自动退群。");
                        executor.schedule(() -> event.getGroup().quit(),15,TimeUnit.SECONDS);
                        return;
                    }
                }
            }

            // 正常通过群邀请加群
            boolean containsOldCitung = IdentityUtil.containsUnusedBot(event.getGroup());

            notifyDevWhenJoinGroup(event);
            sendJoinGroupNotice(event,containsOldCitung);

        },10,TimeUnit.SECONDS);

    }

    // 处理退群事件
    public static void handleLeaveGroup(BotLeaveEvent.Kick event) {
        // 通知开发者群
        notifyDevWhenLeaveGroup(event);
        ActivationDatabase.deleteGroup(event.getGroupId(),event.getBot());
    }

    // 处理退群事件
    public static void handleLeaveGroup(BotLeaveEvent.Active event) {
        // 通知开发者群
        notifyDevWhenLeaveGroup(event);
        ActivationDatabase.deleteGroup(event.getGroupId(),event.getBot());
    }

    // 处理加为好友事件
    public static void handleAddFriend(FriendAddEvent event) {
        if(!MultiBotHandler.canSendNotice(event.getBot())) return;
        executor.schedule(() -> {
            event.getFriend().sendMessage(JOIN_GROUP);
            DisclTemporary.send(event.getFriend());
        },15, TimeUnit.SECONDS);
    }

    /**
     * 尝试退出某个群
     */
    public static void tryQuitGroup(long id) {
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Group group = bot.getGroup(id);
            if (group != null){
                group.quit();
                return;
            }
        }
    }

    public static void tryQuitGroup(long id,Bot bot) {
        Group group = bot.getGroup(id);
        if (group != null) group.quit();
    }

    /**
     * 尝试删除好友
     */
    public static void tryDeleteFriend(long id) {
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Friend friend = bot.getFriend(id);
            if (friend != null) friend.delete();
        }
    }

    public static void tryDeleteFriend(long id, Bot bot) {
        Friend friend = bot.getFriend(id);
        if (friend != null) friend.delete();
    }

    // 加群后发送Bot提示
    static void sendNoticeWhenJoinGroup(BotJoinGroupEvent.Active event,boolean containsOldChitung) {
        if(!MultiBotHandler.canSendNotice(event.getBot())) return;
        String message = "您好，七筒已经加入了您的群" + event.getGroup().getName() + " - " + event.getGroup().getId() + "，请在群聊中输入/help 以获取相关信息。如果七筒过于干扰群内秩序，请将七筒从您的群中移除。";
        if(containsOldChitung) message+="\n\n检测到您的群聊中有已经不再投入使用的七筒账号，可以移除。";
        event.getGroup().getOwner().sendMessage(message);
    }

    static void sendNoticeWhenJoinGroup(Group group, boolean containsOldChitung, Bot bot) {
        if(!MultiBotHandler.canSendNotice(bot)) return;
        String message = "您好，七筒已经加入了您的群" + group.getName() + " - " + group.getId() + "，请在群聊中输入/help 以获取相关信息。如果七筒过于干扰群内秩序，请将七筒从您的群中移除。";
        if(containsOldChitung) message+="\n\n检测到您的群聊中有已经不再投入使用的七筒账号，可以移除。";
        group.getOwner().sendMessage(message);
    }

    // 向开发者发送加群提醒
    static void notifyDevWhenJoinGroup(BotJoinGroupEvent.Invite event) {
        MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）,邀请人为 "
                + event.getInvitor().getNick() + "（" + event.getInvitor().getId() + "）。", event.getBot().getId());
    }

    static void notifyDevWhenJoinGroup(BotInvitedJoinGroupRequestEvent event) {
        String content = "七筒已加入 " + event.getGroupName() + "（" + event.getGroupId() + "）";

        try {
            MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroupName() + "（" + event.getGroupId() + "）,邀请人为 "
                    + Objects.requireNonNull(event.getInvitor()).getNick() + "（" + event.getInvitor().getId() + "）。", event.getBot().getId());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // 向开发者发送加群提醒
    static void notifyDevWhenJoinGroup(BotJoinGroupEvent.Active event) {
        MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）", event.getBot().getId());
    }

    // 向开发者发送退群提醒
    static void notifyDevWhenLeaveGroup(BotLeaveEvent.Kick event) {
        MessageUtil.notifyDevGroup("七筒已经从 " + event.getGroup().getName() + "（" + event.getGroupId() + "）离开，由管理员操作。", event.getBot().getId());
    }

    // 向开发者发送退群提醒
    static void notifyDevWhenLeaveGroup(BotLeaveEvent.Active event) {
        MessageUtil.notifyDevGroup("七筒已经从 " + event.getGroup().getName() + "（" + event.getGroupId() + "）主动离开。", event.getBot().getId());
    }

    // 激活之后发送消息
    public static void handlePostActivation(Group group){
        executor.schedule(new PostActivation(group),2,TimeUnit.SECONDS);

    }

    static class PostActivation implements Runnable{

        private final Group group;

        PostActivation(Group group){
            this.group = group;
        }

        @Override
        public void run(){

            group.sendMessage(JOIN_GROUP);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DisclTemporary.send(group);

        }
    }

    // 新的入群发言
    static void sendJoinGroupNotice(BotJoinGroupEvent.Invite event, boolean containsOldChitung){
        MessageChainBuilder mcb = new MessageChainBuilder();

        mcb.append("您好，受").append(new At(event.getInvitor().getId())).append("邀请。七筒已经加入本群聊，如果过于干扰群内秩序，请群主");
        if(event.getInvitor().getPermission()!= MemberPermission.OWNER) mcb.append(new At(event.getGroup().getOwner().getId()));
        mcb.append("移除。");
        if(containsOldChitung) mcb.append("检测到您的群聊中有已经不再投入使用的七筒账号，可以移除。\n\n");
        mcb.append("目前七筒还没有激活，请任意群成员添加公众聊天群 932617537 并按照提示激活。七筒在被激活前不会响应任何消息。\n如需要获得七筒的最新消息，请添加通知群：948979109。");

        event.getGroup().sendMessage(mcb.asMessageChain());
    }

    static void sendJoinGroupNotice(BotJoinGroupEvent.Active event,boolean containsOldChitung){
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.append("您好，").append("邀请，七筒已经加入本群聊。如果过于干扰群内秩序，请群主移除。");
        if(containsOldChitung) mcb.append("检测到您的群聊中有已经不再投入使用的七筒账号，可以移除。\n\n");
        mcb.append("目前七筒还没有激活，请任意群成员添加公众聊天群 932617537 并按照提示激活。七筒在被激活前不会响应任何消息。");
        event.getGroup().sendMessage(mcb.asMessageChain());
    }
    enum JoinGroupSourceType {
        INVITE,
        RETRIEVE,
        ACTIVE
    }
}
