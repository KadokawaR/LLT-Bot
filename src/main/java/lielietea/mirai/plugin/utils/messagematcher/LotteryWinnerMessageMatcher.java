package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class LotteryWinnerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("((/[Ww]inner)|([oO][kK] [Ww]inner))|(/(([Ll]ottery)|(乐透)|(彩票)))");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
