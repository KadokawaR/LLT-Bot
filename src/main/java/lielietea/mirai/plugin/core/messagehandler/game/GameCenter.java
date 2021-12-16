package lielietea.mirai.plugin.core.messagehandler.game;

import lielietea.mirai.plugin.administration.statistics.StatisticController;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPack;
import lielietea.mirai.plugin.core.messagehandler.game.mahjongriddle.MahjongRiddleHandler;
import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.regex.Pattern;

public class GameCenter {

    static final Pattern TEMP_SOLUTION_REG = Pattern.compile(".*(([Tt][Aa][Nn][Kk])|([Ff][Uu][Cc][Kk])|([Mm][Aa][Ss]*[Aa][Cc][Rr][Ee])|(坦克)|([屠杀])|([兽人控])|([的批])|([男同])|([女同])).*");


    public static void handle(GroupMessageEvent event) {

        //TODO 这个threshold不好，需要改
        if (StatisticController.checkGroupCount(event) && (!IdentityUtil.isBot(event))) {
            try{
                MahjongRiddleHandler.handle(event);
                //Foodie.send(event);

                //FIXME 需要一个黑名单，这个地图api有大问题
                // - 这里是临时解决方案
                if (!TEMP_SOLUTION_REG.matcher(event.getMessage().contentToString()).matches())
                    JetPack.start(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
