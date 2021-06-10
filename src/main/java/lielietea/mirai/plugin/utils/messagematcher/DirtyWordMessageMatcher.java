package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DirtyWordMessageMatcher implements MessageMatcher<MessageEvent>{
    static final List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile(".*"+"([日干操艹草滚])([你尼泥])([妈马麻])"+".*"));
            regPattern.add(Pattern.compile(".*"+"([Mm])otherfucker"+".*"));
            regPattern.add(Pattern.compile(".*"+"([Ff])uck ([Yy])ou"+".*"));
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
