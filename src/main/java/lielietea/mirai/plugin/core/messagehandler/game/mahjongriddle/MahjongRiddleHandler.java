package lielietea.mirai.plugin.core.messagehandler.game.mahjongriddle;

import lielietea.mirai.plugin.admintools.StatisticController;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.IOException;

public class MahjongRiddleHandler extends StatisticController {
    public static void handle(GroupMessageEvent event) throws IOException {
        long groupID = event.getGroup().getId();
        MahjongRiddle.riddleType rt = MahjongRiddle.riddleStart(event);
        if (rt == MahjongRiddle.riddleType.Start) {
            addMinuteCount(groupID);
            countIn(groupID, MahjongRiddle.getUUID("猜麻将开始"));
        }
        if (rt == MahjongRiddle.riddleType.Congratulation) {
            addMinuteCount(groupID);
            countIn(groupID, MahjongRiddle.getUUID("猜麻将猜完"));
        }
        if (rt == MahjongRiddle.riddleType.Get) {
            addMinuteCount(groupID);
            countIn(groupID, MahjongRiddle.getUUID("猜麻将猜中"));
        }
    }
}
