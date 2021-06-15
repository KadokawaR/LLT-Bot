package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DrinkPickerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final List<Pattern> regPattern = new ArrayList<>();

    static{
        {
            regPattern.add(Pattern.compile(".*"+"喝点什么"+".*"));
            regPattern.add(Pattern.compile(".*"+"奶茶"+".*"));
            regPattern.add(Pattern.compile(".*"+"喝了什么"+".*"));
            regPattern.add(Pattern.compile(".*"+"喝什么"+".*"));
            regPattern.add(Pattern.compile(".*"+"有点渴"+".*"));
            regPattern.add(Pattern.compile(".*"+"好渴"+".*"));
            regPattern.add(Pattern.compile(".*"+"来一杯"+".*"));
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
