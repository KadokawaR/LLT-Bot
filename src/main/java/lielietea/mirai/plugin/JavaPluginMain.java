package lielietea.mirai.plugin;


import lielietea.mirai.plugin.messageresponder.autoreply.AutoReplyManager;
import lielietea.mirai.plugin.messageresponder.autoreply.Greeting;
import lielietea.mirai.plugin.messageresponder.dice.DiceCommandManager;
import lielietea.mirai.plugin.messageresponder.feastinghelper.DrinkPicker;
import lielietea.mirai.plugin.messageresponder.overwatch.HeroLinesManager;
import lielietea.mirai.plugin.repeater.RepeaterManager;
import lielietea.mirai.plugin.utils.idchecker.AccountChecker;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.GroupChecker;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;

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
            //监听群消息
            getLogger().info(event.getMessage().contentToString());

            if (testGroupChekcer.checkIdentity(event) && kawaaharaChecker.checkIdentity(event)) {   //一点点VIP待遇
                event.getSubject().sendMessage("老唐最帅！");
            }

            if (testGroupChekcer.checkIdentity(event)){
                //扔骰子
                DiceCommandManager.handleMessage(event);

                //召唤屁股
                HeroLinesManager.handleMessage(event);

                //点饮料
                DrinkPicker.handleMessage(event);

                //复读
                RepeaterManager.getInstance().handleMessage(event);

                //自动回复
                AutoReplyManager.handleMessage(event);

                //打招呼,要有礼貌
                Greeting.handleMessage(event);


            }
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            //监听好友消息
            getLogger().info(event.getMessage().contentToString());


            if (kawaaharaChecker.checkIdentity(event)) {
                event.getSubject().sendMessage("老唐最帅！");    //一点点VIP待遇
            }

            //扔骰子
            DiceCommandManager.handleMessage(event);

            //点饮料
            DrinkPicker.handleMessage(event);

            //自动回复
            AutoReplyManager.handleMessage(event);

        });
    }
}