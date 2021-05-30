package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Repeat {
    static String content0;
    static String content1;
    static String content2;
    boolean check;
    public static void check(GroupMessageEvent event){
        content0 = content1;
        content1 = content2;
        content2 = event.getMessage().contentToString();

        if ((content0.equals(content1)) && (content1.equals(content2))){
            event.getSubject().sendMessage(content2);
        }
    }
}
