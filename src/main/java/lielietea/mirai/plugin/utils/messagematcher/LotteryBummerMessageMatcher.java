package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class LotteryBummerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("(/[Bb]ummer)|([oO][kK] [Bb]ummer)");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
