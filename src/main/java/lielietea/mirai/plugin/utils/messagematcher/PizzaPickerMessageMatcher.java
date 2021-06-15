package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class PizzaPickerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("(/[Pp]izza)|([oO]k [Pp]izza)");


    @Override
    public boolean matches(MessageEvent event) {
        if(regPattern.matcher(event.getMessage().contentToString()).matches()){
            return true;
        }
        return false;
    }

}
