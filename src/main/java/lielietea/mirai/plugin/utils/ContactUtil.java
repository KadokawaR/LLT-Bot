package lielietea.mirai.plugin.utils;

import lielietea.mirai.plugin.core.messagehandler.responder.help.DisclTemporary;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContactUtil {
    public static final String JOIN_GROUP = "七爷来了！这里是七筒，很高兴为您服务。\n\n在使用本 bot 之前，请仔细阅读下方的免责协议，如有任何问题请与开发者联系。";
    public static final String DISCLAIMER = "本项目仅限学习使用，不涉及到任何商业或者金钱用途，禁止用于非法行为。您的使用行为将被视为对本声明全部内容的认可。本声明在您邀请该账号（QQ账号：340865180）进入任何腾讯QQ群聊时生效。\n" +
            "\n" +
            "本项目在运作时，不可避免地会使用到您的QQ号、QQ昵称、群号、群昵称等信息。后台不会收集具体的聊天内容，如果您对此有所疑问，请停止使用本项目。基于维持互联网秩序的考量，请勿恶意使用本项目。本项目有权停止对任何对象的服务，任何解释权均归本项目开发组所有。\n" +
            "\n" +
            "本项目涉及或使用到的开源项目有：基于 AGPLv3 协议的 Mirai (https://github.com/mamoe/mirai) ，基于 Apache License 2.0 协议的谷歌 Gson (https://github.com/google/gson) ，清华大学开放中文词库 (http://thuocl.thunlp.org/) ，动物图片来自互联网开源动物图片API Shibe.online(shibes as a service)、Dog.ceo (The internet's biggest collection of open source dog pictures.)、random.dog (Hello World, This Is Dog)。\n";

    // 决定是否接收加群邀请
    public static void handleGroupInvitation(BotInvitedJoinGroupRequestEvent event) {
        if(IdentityUtil.isAdmin(Objects.requireNonNull(event.getInvitor()).getId())) {
            event.accept();
            return;
        }
        if (!MultiBotHandler.canAcceptGroup(event.getBot().getId())) {
            event.getInvitor().sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
            event.ignore();
        } else {
            event.accept();
        }

    }

    // 决定是否接收好友请求
    public static void handleFriendRequest(NewFriendRequestEvent event) {
        if(IdentityUtil.isAdmin(event.getFromId())) event.accept();
        if (!MultiBotHandler.canAcceptFriend(event.getBot().getId())) {
            event.reject(false);
            return;
        }
        event.accept();
    }

    // 处理加群事件
    public static void handleJoinGroup(BotJoinGroupEvent event) {
        if(!MultiBotHandler.canAcceptGroup(event.getBot().getId())){
            ((BotJoinGroupEvent.Invite) event).getInvitor().sendMessage(MultiBotHandler.rejectInformation(event.getBot().getId()));
            event.getGroup().quit();
            String content = "由于目前Bot不接受添加群聊，已从 "+event.getGroup().getName()+"("+event.getGroup().getId()+")"+"出逃。";
            MessageUtil.notifyDevGroup(content,event.getBot().getId());
            return;
        }

        // 正常通过群邀请加群
        sendNoticeWhenJoinGroup(event);
        notifyDevWhenJoinGroup(event, JoinGroupSourceType.INVITE);

        event.getGroup().sendMessage(JOIN_GROUP);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DisclTemporary.send(event.getGroup());

    }

    // 处理退群事件
    public static void handleLeaveGroup(BotLeaveEvent event) {
        // 通知开发者群
        notifyDevWhenLeaveGroup(event);
    }

    // 处理加为好友事件
    public static void handleAddFriend(FriendAddEvent event) {
        Executors.newScheduledThreadPool(1).schedule(() -> {
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
            //  TODO 问题来了，退群要给群内发通知吗?
            if (group != null) group.quit();
        }
    }

    /**
     * 尝试删除好友
     */
    public static void tryDeleteFriend(long id) {
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Friend friend = bot.getFriend(id);
            //  TODO 问题来了，删除好友需要告知被删除对象吗？
            if (friend != null) friend.delete();
        }
    }

    // 加群后发送Bot提示
    static void sendNoticeWhenJoinGroup(BotJoinGroupEvent event) {
        event.getGroup().getOwner().sendMessage("您好，七筒已经加入了您的群" + event.getGroup().getName() + " - " + event.getGroup().getId() + "，请在群聊中输入/help 以获取相关信息。如果七筒过于干扰群内秩序，请将七筒从您的群中移除。");
    }

    // 向开发者发送加群提醒
    static void notifyDevWhenJoinGroup(BotJoinGroupEvent event, JoinGroupSourceType source) {
        MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）,邀请人为 "
                + ((BotJoinGroupEvent.Invite) event).getInvitor().getNick() + "（" + ((BotJoinGroupEvent.Invite) event).getInvitor().getId() + "）。", event.getBot().getId());
    }

    // 向开发者发送退群提醒
    static void notifyDevWhenLeaveGroup(BotLeaveEvent event) {
        MessageUtil.notifyDevGroup("七筒已经从 " + event.getGroup().getName() + "（" + event.getGroupId() + "） 离开。",event.getBot().getId());
    }

    enum JoinGroupSourceType {
        INVITE,
        RETRIEVE,
        ACTIVE
    }
}
