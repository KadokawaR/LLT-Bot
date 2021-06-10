package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RequestOverwatchHeroLineMessageMatcher implements MessageMatcher<MessageEvent>{
    static List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile("/大招"));
            regPattern.add(Pattern.compile("/英雄不朽"));
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
