package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class DogGSDMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("((/(([Gg]sd)|(GSD))|([oO][kK] 德牧))|(((来点)|/)((德牧)|(黑背))))");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
