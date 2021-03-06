package lielietea.mirai.plugin;


import lielietea.mirai.plugin.administration.AdminCommandDispatcher;
import lielietea.mirai.plugin.administration.TestMode;
import lielietea.mirai.plugin.administration.statistics.MPSEHandler.MessagePostSendEventHandler;
import lielietea.mirai.plugin.core.game.fish.Fishing;
import lielietea.mirai.plugin.core.game.zeppelin.Zeppelin;
import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.administration.blacklist.Blacklist;
import lielietea.mirai.plugin.core.responder.ResponderCenter;
import lielietea.mirai.plugin.core.responder.imageresponder.ImageResponder;
import lielietea.mirai.plugin.core.responder.universalrespond.URManager;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionHandler;
import lielietea.mirai.plugin.core.secretfunction.antiwithdraw.AntiWithdraw;
import lielietea.mirai.plugin.core.secretfunction.repeater.Repeater;
import lielietea.mirai.plugin.utils.*;
import lielietea.mirai.plugin.core.broadcast.BroadcastSystem;
import lielietea.mirai.plugin.core.game.GameCenter;
import lielietea.mirai.plugin.core.responder.ResponderManager;
import lielietea.mirai.plugin.utils.activation.handler.ActivationDatabase;
import lielietea.mirai.plugin.utils.activation.handler.ActivationOperation;
import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;


import java.util.Optional;
import java.util.concurrent.TimeUnit;

/*
使用java请把
src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin
文件内容改成"org.example.mirai.plugin.JavaPluginMain"也就是当前主类
使用java可以把kotlin文件夹删除不会对项目有影响

在settings.gradle.kts里改生成的插件.jar名称
build.gradle.kts里改依赖库和插件版本
在主类下的JvmPluginDescription改插件名称，id和版本
用runmiraikt这个配置可以在ide里运行，不用复制到mcl或其他启动器
 */

public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("lielietea.lielietea-bot", "2.0.1")
                .info("LieLieTea QQ Group Bot")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");

        InitializeUtil.initialize();

        // 上线事件
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Optional.ofNullable(event.getBot().getGroup(IdentityUtil.DevGroup.DEFAULT.getID())).ifPresent(group -> group.sendMessage("老子来了"));


            if(!TestMode.on()) {
                GroupPolice.executor.schedule(new GroupPolice.BotAutoClear(event.getBot()), 30, TimeUnit.SECONDS);
            }

            //如果Bot从未注册过则这为第一次注册
            ActivationDatabase.initialize(event);

            if(!BotOnlineUtil.hasInitialized(event.getBot())){
                // 上线之后开始进行Activation System的数据整理
                ActivationOperation.autoClear(event);
                BotOnlineUtil.setInitialized(event.getBot());
            }
        });

        // 处理好友请求
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, ContactUtil::handleFriendRequest);

        // 加为好友之后发送简介与免责声明
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, ContactUtil::handleAddFriend);

        // 处理拉群请求
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, ContactUtil::handleGroupInvitation);

        // 加群后处理加群事件
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.Invite.class, ContactUtil::handleJoinGroup);

        // 加群后处理加群事件
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.Active.class, ContactUtil::handleJoinGroup);

        // Bot被踢
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.Kick.class, ContactUtil::handleLeaveGroup);

        // Bot离群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.Active.class, ContactUtil::handleLeaveGroup);

        // Bot获得权限
        GlobalEventChannel.INSTANCE.subscribeAlways(BotGroupPermissionChangeEvent.class, event -> {

            if(MessagePostSendEventHandler.botHasTriggeredBreak(event.getBot(),0L)) return;

            if (event.getGroup().getBotPermission().equals(MemberPermission.OWNER) || (event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR))) {
                if(ActivationDatabase.isActivated(event.getGroupId(),event.getBot())) {
                    if(Harbor.isReachingPortLimit(event.getGroup())) {
                        event.getGroup().sendMessage("谢谢，各位将获得更多的乐趣。");
                        Harbor.count(event.getGroup());
                    }
                }
            }
        });

        // 群名改变之后发送消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupNameChangeEvent.class, event -> {

            if(MessagePostSendEventHandler.botHasTriggeredBreak(event.getBot(),0L)) return;

            if(ActivationDatabase.isActivated(event.getGroupId(),event.getBot())) {
                if(Harbor.isReachingPortLimit(event.getGroup())){
                    event.getGroup().sendMessage("好名字。");
                    Harbor.count(event.getGroup());
                }
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {


            //管理员功能
            AdminCommandDispatcher.getInstance().handleMessage(event);
            //广播
            BroadcastSystem.handle(event);
            //秘密功能
            SecretFunctionHandler.go(event);

            if(IdentityUtil.isBot(event)) return;

            if(!ActivationOperation.match(event)) return;

            //激活
            ActivationOperation.handle(event);

            if(MessagePostSendEventHandler.botHasTriggeredBreak(event)) return;

            if(!ConfigHandler.canAnswerGroup(event.getBot())) return;

            if(!GroupConfigManager.globalConfig(event)) return;
            if(GroupConfigManager.isBlockedUser(event)) return;

            if(Blacklist.isBlocked(event.getSender().getId(), Blacklist.BlockKind.Friend)) return;
            if(Blacklist.isBlocked(event.getGroup().getId(), Blacklist.BlockKind.Group)){
                ContactUtil.tryQuitGroup(event.getGroup().getId());
                return;
            }

            //GameCenter
            GameCenter.handle(event);

            if(Harbor.isReachingPortLimit(event)) return;

            //ResponderCenter
            if(GroupConfigManager.responderConfig(event) && ConfigHandler.getConfig(event).getGroupFC().isResponder()){

                //Nudge.mentionNudge(event);
                ResponderCenter.getINSTANCE().handleMessage(event);
                ImageResponder.handle(event);

                //钓鱼
                Fishing.go(event);
                //Universal Responder
                URManager.handle(event);
                //群管理功能
                GroupConfigManager.handle(event);
            }

        });

        //被人戳一戳了
        GlobalEventChannel.INSTANCE.subscribeAlways(NudgeEvent.class, event->{

            if(MessagePostSendEventHandler.botHasTriggeredBreak(event.getBot(),event.getFrom().getId())) return;

            if(!ConfigHandler.canAnswerGroup(event.getBot())) return;
            if(IdentityUtil.isBot(event.getFrom().getId())) return;

            if(Blacklist.isBlocked(event.getFrom().getId(), Blacklist.BlockKind.Friend)) return;

            //Nudge.returnNudge(event);

        });

        //群成员入群自动欢迎
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
            if(ActivationDatabase.isActivated(event.getGroupId(),event.getBot())) {
                if(Harbor.isReachingPortLimit(event.getGroup())){
                    event.getGroup().sendMessage("欢迎入群。");
                    Harbor.count(event.getGroup());
                }
            }
        });

        //撤回消息响应
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageRecallEvent.GroupRecall.class, AntiWithdraw::handle);

        //计数
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessagePostSendEvent.class, event ->{
            MessagePostSendEventHandler.handle(event);
            Repeater.flush(event.getTarget());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessagePostSendEvent.class, MessagePostSendEventHandler::handle);

        //临时消息
        GlobalEventChannel.INSTANCE.subscribeAlways(StrangerMessageEvent.class, event -> {return;});
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, event -> {return;});

        //好友消息
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {

            if(!ConfigHandler.canAnswerFriend(event.getBot())) return;
            if(IdentityUtil.isBot(event)) return;

            if(Blacklist.isBlocked(event.getSender().getId(), Blacklist.BlockKind.Friend)) return;

            //激活系统
            ActivationOperation.handle(event);
            //管理员功能
            AdminCommandDispatcher.getInstance().handleMessage(event);
            //GameCenter
            GameCenter.handle(event);
            //广播
            BroadcastSystem.handle(event);

            if(Harbor.isReachingPortLimit(event)) return;

            //ResponderCenter
            if(ConfigHandler.getConfig(event).getFriendFC().isResponder()) {
                ResponderCenter.getINSTANCE().handleMessage(event);
                URManager.handle(event);
                ImageResponder.handle(event);
                Fishing.go(event);
            }

        });
    }

    @Override
    public void onDisable() {
        ResponderManager.getINSTANCE().close();
        ResponderCenter.getINSTANCE().close();
        AdminCommandDispatcher.getInstance().close();
    }
}