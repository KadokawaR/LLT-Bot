package lielietea.mirai.plugin.feastinghelper;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * 一个类似于”今天吃什么“的类
 *
 * <p>用{@link DrinkPicker#getPersonalizedHourlyDrink(MessageEvent)}来获取根据用户而变化的Hourly Random Drink</p>
 */
public class DrinkPicker {

    static ArrayList<String> drinkBase = new ArrayList<>(Arrays.asList(
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

    static ArrayList<String> topping = new ArrayList<>(Arrays.asList(
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

    static ArrayList<String> sugarLevel = new ArrayList<>(Arrays.asList(
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

    /**
     * 获取每小时变化的，根据用户而不同的随机饮品
     * @param event 接收饮品消息的事件
     */
    public static void getPersonalizedHourlyDrink(MessageEvent event){
        String drink = mixDrink(pickPersonalizedHourlyIngredients(event.getSender().getId()));
        serveDrink(event,drink);
    }

    static int[] pickPersonalizedHourlyIngredients(Long qqID){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//获得当前时间
        long fullDate = year+month*10000+date*1000000+hour*100000000L;//用时间和小时构成一个10位数
        long getSixNum = fullDate*1000000L/qqID % 1000000L;//除以QQ号之后获得这个数的最后六位
        long firstTwoNum = getSixNum / 10000;
        long middleTwoNum = (getSixNum % 10000) / 100;
        long lastTwoNum = getSixNum % 100; //获得这个数的三组两位数；

        int[] randomTea = new int[3]; //定义返回数组
        randomTea[0] = Math.toIntExact(firstTwoNum % drinkBase.size());
        randomTea[1] = Math.toIntExact(middleTwoNum % topping.size());
        randomTea[2] = Math.toIntExact(lastTwoNum % sugarLevel.size());
        return randomTea;
    }

    static String mixDrink(int[] randomTea){
        String result = drinkBase.get(randomTea[0])+topping.get(randomTea[1])+sugarLevel.get(randomTea[2]);
        return result;
    }

    static void serveDrink(MessageEvent event,String drink){
        event.getSubject().sendMessage("您的饮品是 "+drink);
    }
}
