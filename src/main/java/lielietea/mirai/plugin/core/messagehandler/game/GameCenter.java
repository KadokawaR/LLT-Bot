package lielietea.mirai.plugin.core.messagehandler.game;

import lielietea.mirai.plugin.admintools.StatisticController;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPack;
import lielietea.mirai.plugin.core.messagehandler.game.mahjongriddle.MahjongRiddleHandler;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class GameCenter {

    public static void handle(GroupMessageEvent event) throws Exception {

        BotChecker bc = new BotChecker();

        if (StatisticController.checkGroupCount(event) && (!bc.checkIdentity(event))) {
            MahjongRiddleHandler.handle(event);
            //Foodie.send(event);
            JetPack.start(event);
        }

    }
}
