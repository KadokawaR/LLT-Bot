package lielietea.mirai.plugin.core.game.zeppelin.processor;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.UIUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoodsGenerator {

    public static String name() {
        String res = "啊"+ UIUtils.randomAircraftName(4);
        try {
            res = URLEncoder.encode(res,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int value(Coordinate departure, Coordinate destination, long playerID) {
        double time = RadarUtils.distance(departure,destination)/RadarUtils.speed(playerID);
        return (int) (time * Config.MONEY_PER_MINUTE);
    }

}
