package lielietea.mirai.plugin.core.game;

import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;
import lielietea.mirai.plugin.core.bank.SenoritaCounter;
import lielietea.mirai.plugin.core.game.mahjongriddle.MahjongRiddle;
import lielietea.mirai.plugin.core.game.montecarlo.CasinoCroupier;
import lielietea.mirai.plugin.core.game.jetpack.JetPack;
import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class GameCenter {

    public static void handle(MessageEvent event){


        if(event instanceof GroupMessageEvent){
            if(GroupConfigManager.gameConfig((GroupMessageEvent) event) && ConfigHandler.getConfig(event).getGroupFC().isGame()) {
                MahjongRiddle.riddleStart((GroupMessageEvent) event);
                JetPack.start(event);
                if(GroupConfigManager.casinoConfig((GroupMessageEvent) event)&&ConfigHandler.getConfig(event).getGroupFC().isCasino()){
                    CasinoCroupier.handle(event);
                    SenoritaCounter.go(event);
                }
            }
        }

        if(event instanceof FriendMessageEvent){
            if(ConfigHandler.getConfig(event).getFriendFC().isGame()) {
                JetPack.start(event);
                if(ConfigHandler.getConfig(event).getFriendFC().isCasino()){
                    SenoritaCounter.go(event);
                    CasinoCroupier.handle(event);
                }
            }
        }


        //Zeppelin.start(event);
        //Zeppelin.test(event);
        //Foodie.send(event);

    }

}
