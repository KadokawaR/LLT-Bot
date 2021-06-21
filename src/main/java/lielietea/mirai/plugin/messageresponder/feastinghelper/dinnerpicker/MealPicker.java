package lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个类似于”今天吃什么“的类
 * 会给用户推送随机加入3-10种配料的披萨
 */
public class MealPicker implements MessageHandler<MessageEvent> {

    final MessageMatcher<MessageEvent> requestMealMatcher;

    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    public MealPicker(MessageMatcher<MessageEvent> requestMealMatcher) {
        this.requestMealMatcher = requestMealMatcher;
    }


    @Override
    public boolean handleMessage(MessageEvent event){
        if(requestMealMatcher.matches(event)){
            FoodCluster.reply(event, FoodCluster.Mode.COMMON);
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }

    @Override
    public String getName() {
        return "今天吃什么";
    }
}
