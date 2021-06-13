package lielietea.mirai.plugin;


import lielietea.mirai.plugin.messagerepeater.RepeaterManager;
import lielietea.mirai.plugin.messageresponder.MessageRespondCenter;
import lielietea.mirai.plugin.utils.idchecker.AccountChecker;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.GroupChecker;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    //检查器，确保目前只处理来自Bot测试群的消息
    static final GroupChecker testGroupChekcer = new GroupChecker(578984285L);
    //给老唐的特殊待遇
    static final AccountChecker kawaaharaChecker = new AccountChecker(459405942L);

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

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {

            if (testGroupChekcer.checkIdentity(event) && kawaaharaChecker.checkIdentity(event)) {   //一点点VIP待遇
                event.getSubject().sendMessage("老唐最帅！");
            }

            if (testGroupChekcer.checkIdentity(event)){
                //处理所有需要回复的消息
                //包括自动打招呼，关键词触发，指令
                MessageRespondCenter.getINSTANCE().handleGroupMessageEvent(event);

                //复读功能
                RepeaterManager.getInstance().handleMessage(event);

            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {


            if (kawaaharaChecker.checkIdentity(event)) {
                event.getSubject().sendMessage("老唐最帅！");    //一点点VIP待遇
            }
            //处理所有需要回复的消息
            //包括自动打招呼，关键词触发，指令
            MessageRespondCenter.getINSTANCE().handleFrinedMessageEvent(event);

        });
    }
}