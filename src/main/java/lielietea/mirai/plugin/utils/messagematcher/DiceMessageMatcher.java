package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DiceMessageMatcher implements MessageMatcher<MessageEvent>{
    static final List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})"));
            regPattern.add(Pattern.compile("\\.([1-9]\\d{0,2})([dD])[1-9][0-9]{1,7}"));
            regPattern.add(Pattern.compile("\\.([dD])[1-9][0-9]{1,7}"));
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
