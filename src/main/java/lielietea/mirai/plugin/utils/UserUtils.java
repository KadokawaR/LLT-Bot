package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class UserUtils {
    /**
     * 检测是否某个发言账号是Bot本身
     * @param senderID 发言账号ID
     * @return <code>true</code> 是Bot本身 <code>false</code> 不是Bot本身
     */
    public static boolean isBot(long senderID){
        long bot_1 = 2955808839L;
        return senderID == bot_1;
    }
}

