package lielietea.mirai.plugin.utils.messagematcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public class MealPickerMessageMatcher implements MessageMatcher<MessageEvent>{
    static final Pattern regPattern = Pattern.compile("(/[Mm]eal)|(/[Dd]inner)|(/吃啥)|([oO][kK] [Mm]eal)|([oO][kK] [Dd]inner)|(((早饭)|(午饭)|(晚饭)|(夜宵)|(今天)|(今晚)|(早茶)|(宵夜))吃什么)");


    @Override
    public boolean matches(MessageEvent event) {
        return regPattern.matcher(event.getMessage().contentToString()).matches();
    }

}
