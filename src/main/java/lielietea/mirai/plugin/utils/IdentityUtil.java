package lielietea.mirai.plugin.utils;

import com.google.common.collect.ImmutableSet;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Set;

public class IdentityUtil {
    static final Set<Long> botList = ImmutableSet.of(
            340865180L, // 本体
            3628496803L, // 欧洲测试服务
            384087036L, // 客服：引号
            3621269439L, //wymTbot 维护者 1875018140
            736951095L //未知
    );

    static final Set<Long> adminList = ImmutableSet.of(
            2955808839L, //KADOKAWA
            1811905537L, //MARBLEGATE
            493624254L   //感谢咕咕科提供账号
    );

    public static boolean isBot(long id){
        return botList.contains(id);
    }

    public static boolean isBot(MessageEvent event){
        return isBot(event.getSender().getId());
    }

    public static boolean isAdmin(long id){
        return adminList.contains(id);
    }

    public static boolean isAdmin(MessageEvent event){
        return isAdmin(event.getSender().getId());
    }

    public enum DevGroup{
        DEFAULT(578984285L);

        long groupId;

        DevGroup(long groupId) {
            this.groupId = groupId;
        }

        public long getID(){
            return groupId;
        }
    }
}
