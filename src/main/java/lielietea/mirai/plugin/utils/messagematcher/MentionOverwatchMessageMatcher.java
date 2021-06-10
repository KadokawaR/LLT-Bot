package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MentionOverwatchMessageMatcher implements MessageMatcher<MessageEvent>{
    static List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile(".*"+"(O|o)verwatch"+".*"));
            regPattern.add(Pattern.compile(".*"+"守望((先锋)|(屁股))"+".*"));
            regPattern.add(Pattern.compile(".*"+"(玩|打)((OW)|(ow))"+".*"));
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
