package lielietea.mirao.plugin.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Random;

public class Dice {
    public static int getNum(int bound){ return new Random(System.nanoTime()).nextInt(bound)+1; }

    public static String getResultString(int bound){
        return String.valueOf(getNum(bound));
    }

    public static boolean isDiceCommand(int bound, String input){
        return new StringBuilder("/dice").append(bound).toString().equals(input);
    }

    public static void roll(GroupMessageEvent event, int bound){
        String message = event.getMessage().contentToString();
        if(isDiceCommand(bound,message)){
            event.getSubject().sendMessage(getResultString(bound));
        }
    }

}
