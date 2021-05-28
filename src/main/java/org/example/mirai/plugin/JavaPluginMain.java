package org.example.mirai.plugin;


import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Random;

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
        super(new JvmPluginDescriptionBuilder("org.example.mirai-example", "0.1.0")
                .info("EG")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, g -> {
            //监听群消息
            getLogger().info(g.getMessage().contentToString());


            long groupNumber = 578984285;//监听烈烈茶测试群
            if (g.getGroup().getId() == groupNumber) { //仅在此群测试
                if (g.getSender().getId() == 459405942) { //川川的QQ
                    g.getSubject().sendMessage(new MessageChainBuilder()
                            .append(new QuoteReply(g.getMessage()))//回复
                            .append(g.getSender().getNameCard())//群名片
                            .append("\n")
                            .append(g.getSender().getNick())//昵称
                            .append("\n")
                            .append(g.getSender().getSpecialTitle())//特殊头衔
                            .append("\n")
                            .append("you just said:\n'")
                            .append(g.getMessage())//复读
                            .append("'")
                            .build()
                    );
                }

                int bound = 12;
                String message = g.getMessage().contentToString();
                if (dice.check(bound,message)){
                    g.getSubject().sendMessage(new MessageChainBuilder()
                            .append("你骰出的点数是")
                            .append(dice.getNum(bound))
                            .build()
                    );
                }
                ////////////////////////////抄来的但是没有测试成功
                if (g.getMessage().contentToString().startsWith("复读")) {
                    g.getSubject().sendMessage(g.getMessage().contentToString().replace("复读", ""));
                }

                if (g.getMessage().contentToString().equals("hi")) {
                    //群内发送
                    g.getSubject().sendMessage("hi");
                    //向发送者私聊发送消息
                    g.getSender().sendMessage("hi");
                    //不继续处理
                }
                //////////////////////////////////////////////////////////////
            }
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, (event) -> {
            //监听好友消息
            getLogger().info(event.getMessage().contentToString());
            long yourQQNumber = 340865180;
            if (event.getSender().getId() == yourQQNumber) {
                event.getSubject().sendMessage(new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append("Hi, you just said: '")
                        .append(event.getMessage())
                        .append("'")
                        .build()
                );
            }
        });
    }
}