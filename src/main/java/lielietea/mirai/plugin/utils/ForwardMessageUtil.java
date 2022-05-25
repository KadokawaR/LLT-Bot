package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;

import java.util.Iterator;
import java.util.Objects;

public class ForwardMessageUtil {

    public static ForwardMessage create(Bot bot, MessageChainBuilder messageChainBuilder){

        ForwardMessageBuilder fmb = new ForwardMessageBuilder(bot.getAsFriend());
        Iterator<SingleMessage> iterator = messageChainBuilder.stream().iterator();

        while(iterator.hasNext()){
            fmb.add(bot,iterator.next());
        }

        return fmb.build();
    }

}
