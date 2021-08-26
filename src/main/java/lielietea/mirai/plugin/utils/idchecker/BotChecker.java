package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 检测某条群消息是否是由Bot发出的
 */
public class BotChecker implements IdentityChecker<GroupMessageEvent> {
    static final List<Long> botList = new ArrayList<>(Arrays.asList(
            340865180L,
            384087036L,
            3621269439L,
            1402585596L
    ));

    @Override
    public boolean checkIdentity(GroupMessageEvent event) {
        for (Long botID : botList) {
            if (botID == event.getSender().getId()) return true;
        }
        return false;
    }

    public static void addBotToBotList(long botID) {
        botList.add(botID);
    }

    public static void removeBotFromBotList(long botID) {
        botList.remove(botID);
    }
}
