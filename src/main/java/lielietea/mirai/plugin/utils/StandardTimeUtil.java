package lielietea.mirai.plugin.utils;

import java.util.Calendar;
import java.util.Date;

public class StandardTimeUtil {
    public static Date getStandardFirstTime(int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        if (date.after(new Date())) calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static int getPeriodLengthInMS(int day, int hour, int min, int sec) {
        return (((day * 24 + hour) * 60 + min) * 60 + sec) * 1000;
    }
}