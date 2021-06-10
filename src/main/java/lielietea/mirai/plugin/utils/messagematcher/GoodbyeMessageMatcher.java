package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GoodbyeMessageMatcher implements MessageMatcher<MessageEvent>{
    static final List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile(".*"+"下线了"+".*"));
            regPattern.add(Pattern.compile(".*"+"我走了"+".*"));
            regPattern.add(Pattern.compile(".*"+"拜拜"+".*"));
        }
    }

    @Override
    public boolean matches(MessageEvent event) {
        for(Pattern pattern: regPattern){
            if(pattern.matcher(event.getMessage().contentToString()).matches()){
                return true;
            }
        }
        return false;
    }
}
