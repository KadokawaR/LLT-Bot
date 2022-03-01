package lielietea.mirai.plugin.core.game;

import lielietea.mirai.plugin.core.bank.SenoritaCounter;
import lielietea.mirai.plugin.core.game.fish.Fishing;
import lielietea.mirai.plugin.core.game.mahjongriddle.MahjongRiddle;
import lielietea.mirai.plugin.core.game.montecarlo.CasinoCroupier;
import lielietea.mirai.plugin.core.game.jetpack.JetPack;
import lielietea.mirai.plugin.core.game.zeppelin.Zeppelin;
import lielietea.mirai.plugin.utils.Nudge;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class GameCenter {

    public static void handle(MessageEvent event){

        if(event instanceof GroupMessageEvent){
            MahjongRiddle.riddleStart((GroupMessageEvent) event);
            Nudge.mentionNudge((GroupMessageEvent) event);
        }

        JetPack.start(event);
        SenoritaCounter.go(event);
        //CasinoCroupier.handle(event);
        Fishing.go(event);

        //Zeppelin.start(event);
        //Zeppelin.test(event);
        //Foodie.send(event);


    }

}
