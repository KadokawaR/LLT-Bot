package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DirtyWordMessageMatcher implements MessageMatcher<MessageEvent>{
    static List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile(".*"+"(日|干|操|艹|草|滚)(你|尼|泥)(妈|马|麻)"+".*"));
            regPattern.add(Pattern.compile(".*"+"(M|m)otherfucker"+".*"));
            regPattern.add(Pattern.compile(".*"+"(F|f)uck (Y|y)ou"+".*"));
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
