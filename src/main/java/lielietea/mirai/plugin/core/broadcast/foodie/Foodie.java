package lielietea.mirai.plugin.core.broadcast.foodie;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;

// FIXME 这里总得处理一下
public class Foodie {
    static final Timer timer = new Timer(true);
    static final Map<Long, Boolean> foodieActivationFlags = new HashMap<>();
    static final String FOODIE_URL = "https://foodish-api.herokuapp.com/api/";

    static {
        //每日6点定时清空C4触发标记
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
        }
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               Foodie.foodieActivationFlags.clear();
                           }
                       },
                date,
                24 * 60 * 60 * 1000);
    }

    public static void send(MessageEvent event) throws Exception {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if ((hour >= 21) || (hour < 6)) {
            if (!foodieActivationFlags.containsKey(event.getSubject().getId())) {
                foodieActivationFlags.put(event.getSubject().getId(), false);
            }

            if (!foodieActivationFlags.get(event.getSubject().getId())) {
                event.getSubject().sendMessage("饿了么？要来点吃的吗？");
                foodieActivationFlags.put(event.getSubject().getId(), true);
                FoodieUtil.sendFoodieImage(event, FOODIE_URL);
            }
        }
    }
}
