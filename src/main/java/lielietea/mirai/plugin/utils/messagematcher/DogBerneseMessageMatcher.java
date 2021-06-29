package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class DogBerneseMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("((/[Bb]ernese)|([oO][kK] 伯恩山))|(((来点)|/)((伯恩山)|(伯恩山犬)))");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
