package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class DogSamoyedMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("((/[Ss]amoyed)|([oO][kK] 萨摩耶))|(((来点)|/)(萨摩耶))");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
