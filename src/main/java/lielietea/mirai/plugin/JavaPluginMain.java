package lielietea.mirai.plugin;


import lielietea.mirai.plugin.admintools.AdminTools;
import lielietea.mirai.plugin.admintools.StatisticController;
import lielietea.mirai.plugin.core.broadcast.BroadcastSystem;
import lielietea.mirai.plugin.core.dispatcher.MessageDispatcher;
import lielietea.mirai.plugin.core.messagehandler.feedback.FeedBack;
import lielietea.mirai.plugin.core.messagehandler.game.GameCenter;
import lielietea.mirai.plugin.core.messagehandler.responder.ResponderManager;
import lielietea.mirai.plugin.core.messagehandler.responder.help.Speech;
import lielietea.mirai.plugin.utils.groupmanager.JoinGroup;
import lielietea.mirai.plugin.utils.groupmanager.LeaveGroup;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.GroupID;
import lielietea.mirai.plugin.viponly.GrandVIPServiceDepartment;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
    static final Logger logger = LogManager.getLogger(JavaPluginMain.class);

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("lielietea.lielietea-bot", "0.1.1")
                .info("LieLieTea QQ Group Bot")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");

        ResponderManager.getINSTANCE().ini();

        StatisticController.resetMinuteCount();

        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Optional.ofNullable(event.getBot().getGroup(GroupID.DEV)).ifPresent(group -> group.sendMessage("老子来了"));
            BotChecker.addBotToBotList(event.getBot().getId());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, event -> BotChecker.removeBotFromBotList(event.getBot().getId()));

        //自动通过好友请求
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, NewFriendRequestEvent::accept);

        //加为好友之后发送简介与免责声明
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, event -> {
            event.getFriend().sendMessage(Speech.JOIN_GROUP);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            event.getFriend().sendMessage(Speech.DISCLAIMER);
        });

        //自动通过拉群请求
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, BotInvitedJoinGroupRequestEvent::accept);

        //入群须知
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, event -> {
            try {
                JoinGroup.sendNotice(event);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        });

        //Bot离群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.class, LeaveGroup::cancelFlag);

        //Bot获得了权限之后发送一句话（中二 or 须知 or sth else 都可以）
        GlobalEventChannel.INSTANCE.subscribeAlways(BotGroupPermissionChangeEvent.class, event -> {
            if (event.getGroup().getBotPermission().equals(MemberPermission.OWNER) || (event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR))) {
                event.getGroup().sendMessage("谢谢，各位将获得更多的乐趣。");
            }
        });

        //群名改变之后发送消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupNameChangeEvent.class, event -> event.getGroup().sendMessage("好名字。"));

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {

            try {
                //所有消息之后都集中到这个地方处理
                MessageDispatcher.getINSTANCE().handleMessage(event);
                GameCenter.handle(event);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //VIP待遇
            //GrandVIPServiceDepartment.handleMessage(event);

        });

        //群成员入群自动欢迎
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, memberJoinEvent -> memberJoinEvent.getGroup().sendMessage("欢迎。"));

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {

            //所有消息之后都集中到这个地方处理
            MessageDispatcher.getINSTANCE().handleMessage(event);

            //管理员功能
            AdminTools.getINSTANCE().handleAdminCommand(event);


            //BroadcastSystem
            try {
                BroadcastSystem.sendToAllGroups(event);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            BroadcastSystem.directlySendToGroup(event);

            try {
                BroadcastSystem.sendToAllFriends(event);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            BroadcastSystem.broadcastHelper(event);

            StatisticController.getStatistics(event);

            FeedBack.get(event);

        });
    }

    @Override
    public void onDisable() {
        ResponderManager.getINSTANCE().close();
        MessageDispatcher.getINSTANCE().close();
    }
}