package lielietea.mirai.plugin;


import lielietea.mirai.plugin.utils.*;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

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

        



        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
          event.getBot().getGroup(578984285).sendMessage("老子来了");
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
            event.accept(); //自动通过好友请求
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendAddEvent.class, event -> {
            event.getBot().getFriend(event.getFriend().getId()).sendMessage(
                    "你好，这是QQ机器人自动发送的验证消息"
            );
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            //监听群消息
            getLogger().info(event.getMessage().contentToString());

            long groupNumber = 578984285;//监听烈烈茶测试群
            if (event.getGroup().getId() == groupNumber) { //仅在此群测试
                if (event.getSender().getId() == 459405942) { //川川的QQ
                    event.getSubject().sendMessage("老唐最帅！");
                }


                Dice.roll(event);
                Echo.sendAll(event);
                DrinkWhat.createDrink(event);
                Repeat.check(event);

                if (event.getMessage().contentToString().equals("hi")) {
                    //群内发送
                    event.getSubject().sendMessage("hi");
                    //向发送者私聊发送消息
                    event.getSender().sendMessage("hi");
                    //不继续处理
                }
                //////////////////////////////////////////////////////////////
            }
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            //监听好友消息
            getLogger().info(event.getMessage().contentToString());


            long yourQQNumber = 340865180;
            if (event.getSender().getId() == yourQQNumber) {
                event.getSubject().sendMessage("老唐最帅！");
            }

            Dice.roll(event);
            Echo.sendAll(event);
            DrinkWhat.createDrink(event);
        });
    }
}