package lielietea.mirai.plugin.core.game.montecarlo;

import lielietea.mirai.plugin.core.game.montecarlo.blackjack.BlackJack;
import lielietea.mirai.plugin.core.game.montecarlo.minesweeper.Minesweeper;
import lielietea.mirai.plugin.core.game.montecarlo.roulette.Roulette;
import lielietea.mirai.plugin.core.game.montecarlo.taisai.TaiSai;
import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionData;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionDatabase;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.InputStream;

public class CasinoCroupier {

    static TaiSai taisai = new TaiSai();
    static Minesweeper minesweeper = new Minesweeper();

    public static void handle(MessageEvent event){

        String message = event.getMessage().contentToString();
        introduction(event,message);
        flush(event,message);

        switch(croupierStatus(event)){
            case 0:
                if(Harbor.isReachingPortLimit(event)) return;
                BlackJack.go(event);
                Roulette.go(event);
                taisai.handle(event);
                if(event instanceof GroupMessageEvent) {
                    if (SecretFunctionDatabase.getINSTANCE().secretFunctionData.canDoSecretFunction((GroupMessageEvent) event)) {
                        minesweeper.handle(event);
                    }
                }
                return;

            case 1:
                BlackJack.go(event);
                return;

            case 2:
                Roulette.go(event);
                return;

            case 3:
                taisai.handle(event);
                return;

            case 4:
                if(event instanceof GroupMessageEvent) {
                    if (SecretFunctionDatabase.getINSTANCE().secretFunctionData.canDoSecretFunction((GroupMessageEvent) event)) {
                        minesweeper.handle(event);
                    }
                }

                return;

        }

    }

    //重置所有Casino
    static void flush(MessageEvent event,String message){
        if(!message.equalsIgnoreCase("/endgame")&&!message.equalsIgnoreCase("/endcasino")) return;
        BlackJack.cancelMark(event);
        Roulette.cancelMark(event);
        TaiSai.util.clear(event.getSubject());
        Minesweeper.mineUtil.clear(event.getSubject());
        event.getSubject().sendMessage("已经重置娱乐游戏。");
    }

    static void introduction(MessageEvent event,String message){
        if(message.equals("扫雷说明书")||message.equalsIgnoreCase("minesweeper introduction")||message.equalsIgnoreCase("minesweeper -h")||message.equalsIgnoreCase("扫雷 -h")){
            try {
                InputStream img = BlackJack.class.getResourceAsStream("/pics/casino/minesweeper/MineSweeper.png");
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static int croupierStatus (MessageEvent event){
        if(BlackJack.isInGamingProcess(event)) return 1;
        if(Roulette.isInGamingProcess(event)) return 2;
        if(taisai.isInGamingProcess(event)) return 3;
        if(minesweeper.isInGamingProcess(event)) return 4;
        return 0;
    }
}
