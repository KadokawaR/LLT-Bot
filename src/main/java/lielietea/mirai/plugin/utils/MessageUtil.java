package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.List;

public class MessageUtil {
    /**
     * 向开发群发送消息通知
     */
    public static void notifyDevGroup(String content){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Group group = bot.getGroup(IdentityUtil.DevGroup.DEFAULT.getID());
            if (group != null) group.sendMessage(content);
        }
    }

    /**
     * 指定某个Bot，向开发群发送消息通知
     */
    public static void notifyDevGroup(String content, long botId){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            if(bot.getId() == botId){
                Group group = bot.getGroup(IdentityUtil.DevGroup.DEFAULT.getID());
                if (group != null) group.sendMessage(content);
            }
        }
    }

}
