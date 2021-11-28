package lielietea.mirai.plugin.utils;

import lielietea.mirai.plugin.administration.blacklist.BlacklistManager;
import lielietea.mirai.plugin.core.messagehandler.responder.help.DisclTemporary;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.*;

import java.util.List;

public class ContactUtil {
    public static final String JOIN_GROUP = "七爷来了！这里是烈烈茶店长七筒。\n\n在使用本 bot 之前，请仔细阅读下方的免责协议，如有任何问题请与开发者联系。";
    public static final String DISCLAIMER = "本项目仅限学习使用，不涉及到任何商业或者金钱用途，禁止用于非法行为。您的使用行为将被视为对本声明全部内容的认可。本声明在您邀请该账号（QQ账号：340865180）进入任何腾讯QQ群聊时生效。\n" +
            "\n" +
            "本项目在运作时，不可避免地会使用到您的QQ号、QQ昵称、群号、群昵称等信息。后台不会收集具体的聊天内容，如果您对此有所疑问，请停止使用本项目。基于维持互联网秩序的考量，请勿恶意使用本项目。本项目有权停止对任何对象的服务，任何解释权均归本项目开发组所有。\n" +
            "\n" +
            "本项目涉及或使用到的开源项目有：基于 AGPLv3 协议的 Mirai (https://github.com/mamoe/mirai) ，基于 Apache License 2.0 协议的谷歌 Gson (https://github.com/google/gson) ，清华大学开放中文词库 (http://thuocl.thunlp.org/) ，动物图片来自互联网开源动物图片API Shibe.online(shibes as a service)、Dog.ceo (The internet's biggest collection of open source dog pictures.)、random.dog (Hello World, This Is Dog)。\n";

    //todo:存成configuration，通过console交互进行更改和报警
    public static final int GroupNumberLimit = 600;

    // 决定是否接收加群邀请
    public static void handleGroupInvitation(BotInvitedJoinGroupRequestEvent event){
        if (BlacklistManager.getInstance().contains(event.getGroupId(), true)) {
            //提醒邀请者该群在黑名单中
            event.getInvitor().sendMessage("不好意思，此群在茶铺黑名单中。如果您觉得这是个错误，请联系开发组。");
            //提醒开发者，有人尝试将Bot拉入黑名单中的群
            MessageUtil.notifyDevGroup("老板们，刚 "
                    + event.getInvitorNick() + "(" + event.getInvitorId()
                    + ") 尝试将我拉入群 " + event.getGroupName() + "(" + event.getGroupId() + ")。该群在我们的黑名单中。\n"
                    + BlacklistManager.getInstance().getSpecificInform(event.getGroupId(), true), event.getBot().getId());
        } else {
            //todo:更改最大群聊数量上限
            if(event.getBot().getGroups().getSize()>=GroupNumberLimit){
                event.getInvitor().sendMessage("七筒的群聊数量已经接近上限，请稍后再尝试。");
            } else {
                event.accept();
            }
        }
    }

    // 决定是否接收好友请求
    public static void handleFriendRequest(NewFriendRequestEvent event){
        if (BlacklistManager.getInstance().contains(event.getFromId(), true)) {
            // 拒绝该好友请求，但不加入黑名单
            event.reject(false);
            //提醒开发者，有在黑名单中的人尝试加Bot为好友
            MessageUtil.notifyDevGroup("老板们，刚 "
                    + event.getFromNick() + "(" + event.getFromId() + ") 尝试将加我为好友。该用户在我们的黑名单中。\n"
                    + ((event.getFromGroupId()) == 0L ? "该好友请求并非来自群关系。" : "该好友请求来自群 " + event.getFromGroupId() + "。\n")
                    + BlacklistManager.getInstance().getSpecificInform(event.getFromId(), false), event.getBot().getId());
        } else {
            event.accept();
        }
    }

    // 处理加群事件
    public static void handleJoinGroup(BotJoinGroupEvent event){
        // 正常通过群邀请加群
        if(event instanceof BotJoinGroupEvent.Invite){
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
        /*
        通过恢复原来群主身份并入群, Bot 是原群主，
        我实在想不到这里有什么可能会触发，
        但是我们还是给Dev群发一个提醒。
         */
        else if(event instanceof BotJoinGroupEvent.Retrieve){
            notifyDevWhenJoinGroup(event, JoinGroupSourceType.RETRIEVE);
        }
        /*这个地方很奇怪，Mirai 自己说这个来源是
        - 不确定, 已知的来源:
        - Bot 在其他客户端创建群聊而同步到 Bot 客户端.
        按理说我们Bot不会触发此项内容，但我们在这里还是简单给Dev群发一个提醒*/
        else if(event instanceof  BotJoinGroupEvent.Active){
            //这里有问题
            //notifyDevWhenJoinGroup(event, JoinGroupSourceType.ACTIVE);
        }

    }

    // 处理退群事件
    public static void handleLeaveGroup(BotLeaveEvent event){
        // 通知开发者群
        notifyDevWhenLeaveGroup(event);
    }

    // 处理加为好友事件
    public static void handleAddFriend(FriendAddEvent event){
        event.getFriend().sendMessage(JOIN_GROUP);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DisclTemporary.send(event.getFriend());
    }

    /**
     * 尝试退出某个群
     */
    public static void tryQuitGroup(long id){
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
    public static void tryDeleteFriend(long id){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Friend friend = bot.getFriend(id);
            //  TODO 问题来了，删除好友需要告知被删除对象吗？
            if (friend != null) friend.delete();
        }
    }

    // 加群后发送Bot提示
    static void sendNoticeWhenJoinGroup(BotJoinGroupEvent event){
        event.getGroup().getOwner().sendMessage("您好，七筒已经加入了您的群" + event.getGroup().getName() + " - " + event.getGroup().getId() + "，请在群聊中输入/help 以获取相关信息。如果七筒过于干扰群内秩序，请将七筒从您的群中移除。");
    }

    // 向开发者发送加群提醒
    static void notifyDevWhenJoinGroup(BotJoinGroupEvent event, JoinGroupSourceType source){
        if(source==JoinGroupSourceType.INVITE) MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）,邀请人为 "
                +((BotJoinGroupEvent.Invite) event).getInvitor().getNick()+"（"+((BotJoinGroupEvent.Invite) event).getInvitor().getId()+"）。" ,event.getBot().getId());
        else if(source == JoinGroupSourceType.RETRIEVE){
            MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）,这件事不应该发生，因为七筒是通过恢复群主身份入群的，请开发组检查此事！");
        } else if(source == JoinGroupSourceType.ACTIVE){
            //todo:不清楚为什么会是这个样子，先注释掉了
            //MessageUtil.notifyDevGroup("七筒已加入 " + event.getGroup().getName() + "（" + event.getGroupId() + "）,七筒是通过在其他客户端创建群聊入群的，这是否是开发组的调试？");
        }
    }

    // 向开发者发送退群提醒
    static void notifyDevWhenLeaveGroup(BotLeaveEvent event){
        MessageUtil.notifyDevGroup("七筒已经从 " + event.getGroup().getName() + "（" + event.getGroupId() + "） 离开。");
    }

    enum JoinGroupSourceType{
        INVITE,
        RETRIEVE,
        ACTIVE
    }
}
