package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LotteryWinnerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("((/[Ww]inner)|([oO]k [Ww]inner))|(/(([Ll]otter)|(乐透)|(彩票)))");


    @Override
    public boolean matches(MessageEvent event) {
        if(regPattern.matcher(event.getMessage().contentToString()).matches()){
            return true;
        }
        return false;
    }

}
