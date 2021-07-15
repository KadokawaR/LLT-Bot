package lielietea.mirai.plugin.messageresponder;

import lielietea.mirai.plugin.admintools.statistic.StatisticController;
import lielietea.mirai.plugin.broadcast.foodie.Foodie;
import lielietea.mirai.plugin.game.jetpack.JetPack;
import lielietea.mirai.plugin.game.mahjongriddle.MahjongRiddle;
import lielietea.mirai.plugin.utils.groupmanager.Help;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class FakeRespondCenter {
    public static void handle(GroupMessageEvent event) throws Exception {
        BotChecker bc = new BotChecker();
        if (StatisticController.checkGroupCount(event)&&(!bc.checkIdentity(event))){
            MahjongRiddle.riddleStart(event);
            Help.detect(event);
            Foodie.send(event);
            JetPack.start(event);
        }



    }
}
