package lielietea.mirai.plugin.core.messagehandler.game.montecarlo;

import net.mamoe.mirai.event.events.MessageEvent;

public class BlackJack{


    //判定消息里面是否有触发关键词
    public boolean isBlackJack(MessageEvent event){
        return event.getMessage().contentToString().equals("/blackjack")||event.getMessage().contentToString().equals("二十一点");
    }



}
