package lielietea.mirai.plugin;



import lielietea.mirai.plugin.messageresponder.MessageRespondCenter;
import lielietea.mirai.plugin.admintools.AdminTools;
import lielietea.mirai.plugin.messageresponder.fursona.FursonaPunk;
import lielietea.mirai.plugin.messageresponder.mahjong.MahjongRiddle;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.viponly.GrandVIPServiceDepartment;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;

import java.io.IOException;
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

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("lielietea.lielietea-bot", "0.1.0")
                .info("LieLieTea QQ Group Bot")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");

        MessageRespondCenter.getINSTANCE().ini();

        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Optional.ofNullable(event.getBot().getGroup(578984285)).ifPresent(group->group.sendMessage("老子来了"));
            BotChecker.addBotToBotList(event.getBot().getId());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, event -> BotChecker.removeBotFromBotList(event.getBot().getId()));

        //自动通过好友请求
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, NewFriendRequestEvent::accept);

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, event -> Optional.ofNullable(event.getBot().getFriend(event.getFriend().getId())).ifPresent(friend->friend.sendMessage(
                "你好，这是QQ机器人自动发送的验证消息"
        )));

        //自动通过拉群请求
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, BotInvitedJoinGroupRequestEvent::accept);

        //应该是入群须知+简单介绍，现在先占位一下
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, event ->{
            event.getGroup().sendMessage("你好，这里是烈烈茶店长七筒。");
        });


        //Bot获得了权限之后发送一句话（中二 or 须知 or sth else 都可以）
        GlobalEventChannel.INSTANCE.subscribeAlways(BotGroupPermissionChangeEvent.class, event ->{
            if (event.getGroup().getBotPermission().equals(MemberPermission.OWNER)||(event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR))){
                event.getGroup().sendMessage("谢谢，你将获得更多的乐趣。");
            }
        });

        //群名改变之后发送消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupNameChangeEvent.class, event ->{
            event.getGroup().sendMessage("好名字。");
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {

            //处理所有需要回复的消息
            //包括自动打招呼，关键词触发，指令
            MessageRespondCenter.getINSTANCE().handleGroupMessageEvent(event);

            //VIP待遇
            GrandVIPServiceDepartment.handleMessage(event);

            //test for image
            try {
                MahjongRiddle.sendTileImage(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {

            //处理所有需要回复的消息
            //包括自动打招呼，关键词触发，指令
            MessageRespondCenter.getINSTANCE().handleFrinedMessageEvent(event);

            //临时的 管理员账号输入”/group“获得bot所在的所有群的详细列表
            //由于AdministrativeAccountChecker里面的非static方法我调用不了 我临时在AdminTools中写了个
            //潜在崩溃可能：Mirai支持的单条消息长度为5000字，如果超过可能要分消息
            //所以每条消息返回的群数量不大于特定数值（比如50个群？）
            //虽然估计也不会有这么多群，但以防万一
            if (event.getMessage().contentToString().contains("/group")) {
                AdminTools.getGroupList(event);
            }

            if (event.getMessage().contentToString().contains("/friend")) {
                AdminTools.getFriendList(event);
            }

        });
    }

    @Override
    public void onDisable(){
        MessageRespondCenter.getINSTANCE().close();
    }
}