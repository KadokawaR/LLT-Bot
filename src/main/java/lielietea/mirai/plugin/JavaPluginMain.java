package lielietea.mirai.plugin;


import lielietea.mirai.plugin.administration.AdminCommandDispatcher;
import lielietea.mirai.plugin.administration.statistics.MPSEHandler.MessagePostSendEventHandler;
import lielietea.mirai.plugin.core.game.zeppelin.Zeppelin;
import lielietea.mirai.plugin.core.responder.ResponderCenter;
import lielietea.mirai.plugin.utils.Nudge;
import lielietea.mirai.plugin.utils.ContactUtil;
import lielietea.mirai.plugin.core.broadcast.BroadcastSystem;
import lielietea.mirai.plugin.core.game.GameCenter;
import lielietea.mirai.plugin.core.responder.ResponderManager;
import lielietea.mirai.plugin.utils.GroupPolice;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
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
        super(new JvmPluginDescriptionBuilder("lielietea.lielietea-bot", "1.1.1")
                .info("LieLieTea QQ Group Bot")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");

        GroupPolice.getINSTANCE().ini();
        ResponderManager.getINSTANCE().ini();

        Zeppelin.ini();

        // 上线事件
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Optional.ofNullable(event.getBot().getGroup(IdentityUtil.DevGroup.DEFAULT.getID())).ifPresent(group -> group.sendMessage("老子来了"));
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
            if (event.getGroup().getBotPermission().equals(MemberPermission.OWNER) || (event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR))) {
                event.getGroup().sendMessage("谢谢，各位将获得更多的乐趣。");
            }
        });

        // 群名改变之后发送消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupNameChangeEvent.class, event -> event.getGroup().sendMessage("好名字。"));

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {

            if(!MultiBotHandler.canAnswerGroup(event)) return;
            if(IdentityUtil.isBot(event)) return;
            if(MessagePostSendEventHandler.botHasTriggeredBreak(event)) return;

            //ResponderCenter
            ResponderCenter.getINSTANCE().handleMessage(event);
            //管理员功能
            AdminCommandDispatcher.getInstance().handleMessage(event);
            //GameCenter
            GameCenter.handle(event);

        });

        //被人戳一戳了
        GlobalEventChannel.INSTANCE.subscribeAlways(NudgeEvent.class, Nudge::returnNudge);

        //群成员入群自动欢迎
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, memberJoinEvent -> memberJoinEvent.getGroup().sendMessage("欢迎。"));


        //计数
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessagePostSendEvent.class, MessagePostSendEventHandler::handle);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessagePostSendEvent.class, MessagePostSendEventHandler::handle);

        //临时消息
        GlobalEventChannel.INSTANCE.subscribeAlways(StrangerMessageEvent.class, event -> {return;});
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, event -> {return;});

        //好友消息
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {

            if(!MultiBotHandler.canAnswerFriend(event)) return;
            if(IdentityUtil.isBot(event)) return;

            //ResponderCenter
            ResponderCenter.getINSTANCE().handleMessage(event);
            //管理员功能
            AdminCommandDispatcher.getInstance().handleMessage(event);
            //GameCenter
            GameCenter.handle(event);
            //广播
            BroadcastSystem.handle(event);

        });
    }

    @Override
    public void onDisable() {
        ResponderManager.getINSTANCE().close();
        ResponderCenter.getINSTANCE().close();
        AdminCommandDispatcher.getInstance().close();
    }
}