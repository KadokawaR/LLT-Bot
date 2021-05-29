package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DrinkWhat {
    static ArrayList<String> tea_array = new ArrayList<String>(Arrays.asList(
            "铁观音奶茶",
            "大红袍奶茶",
            "四季奶青",
            "柠檬养乐多",
            "芒果杨枝甘露",
            "多肉葡萄",
            "多肉莓莓",
            "芝芝莓莓",
            "草莓奶昔",
            "巧克力奶昔"
    ));

    static ArrayList<String> boba_array = new ArrayList<String>(Arrays.asList(
            "加珍珠",
            "加波霸",
            "加布丁",
            "加龟苓",
            "加脆波波",
            "加寒天",
            "加椰果",
            "加红豆",
            "加三兄弟",
            "加咖啡冻"
    ));

    static ArrayList<String> sugar_array = new ArrayList<String>(Arrays.asList(
            "全糖",
            "半糖",
            "三分糖",
            "无糖",
            "七分糖",
            "少糖",
            "少少糖",
            "少少少糖",
            "不额外加糖"
    ));

    int[] randomTea = new int[3];

    public static int[] setRandomTea(MessageEvent event){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//获得当前时间
        long qqid = event.getSender().getId();
        long fullDate = year+month*10000+date*1000000+hour*100000000L;//用时间和小时构成一个10位数
        long getFixedRandomNum = fullDate*1000000L/qqid;//日期除以QQ号
        long getSixNum = getFixedRandomNum % 1000000L;//获得这个数的最后六位
        long firstTwoNum = getSixNum / 10000;
        long middleTwoNum = (getSixNum % 10000) / 100;
        long lastTwoNum = getSixNum % 100; //获得这个数的三组两位数；

        int[] randomTea = new int[3]; //定义返回数组
        randomTea[0] = Math.toIntExact(firstTwoNum % tea_array.size());
        randomTea[1] = Math.toIntExact(middleTwoNum % boba_array.size());
        randomTea[2] = Math.toIntExact(lastTwoNum % sugar_array.size());
        return randomTea;
    }

    public static String mixDrink(int[] randomTea){
        String result = tea_array.get(randomTea[0])+boba_array.get(randomTea[1])+sugar_array.get(randomTea[2]);
        return result;
    }

    public static void sendDrink(MessageEvent event, String word){
        int result = event.getMessage().contentToString().indexOf(word);
        if (result != -1){
            event.getSubject().sendMessage(mixDrink(setRandomTea(event)));
        }
    }

    public static void createDrink(MessageEvent event){
        sendDrink(event, "喝点什么");
        sendDrink(event, "奶茶");
        sendDrink(event, "喝什么");
        sendDrink(event, "喝了什么");
        sendDrink(event, "有点渴");
        sendDrink(event, "好渴");
        sendDrink(event, "来一杯");
    }


}
