package org.example.mirai.plugin;

import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Random;

public class dice {
    public static String getNum(int bound){
        Random r = new Random();
        int ran1 = r.nextInt(bound);
        ran1++;
        return String.valueOf(ran1);
    }

    public static String toString(int bound){
        return String.valueOf(getNum(bound));
    }

    public static boolean check(int bound, String input){
        StringBuilder sb = new StringBuilder();
        String str = String.valueOf(sb.append("/dice").append(bound));
        return (str.equals(input));
    }

}
