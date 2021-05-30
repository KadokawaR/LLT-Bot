package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MessageChecker {
    static ArrayList<String> needDrinkPatterns = new ArrayList<>(Arrays.asList(
            "喝点什么",
            "奶茶",
            "喝了什么",
            "喝什么",
            "有点渴",
            "好渴",
            "来一杯"
    ));
    static ArrayList<String> rollDicePatterns = new ArrayList<>(Arrays.asList(
            "(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})",
            "\\.([1-9]\\d{0,2})(d|D)[1-9][0-9]{1,7}",
            "\\.(d|D)[1-9][0-9]{1,7}"
    ));
    static ArrayList<String> talkOverwatchPatterns = new ArrayList<>(Arrays.asList(
            "overwatch",
            "Overwatch",
            "守望先锋",
            "守望屁股",
            "玩ow",
            "打ow",
            "玩OW",
            "打OW",
            "玩屁股",
            "打屁股"
    ));
    static ArrayList<String> goodbyePatterns = new ArrayList<>(Arrays.asList(
            "下了",
            "下线",
            "88",
            "byebye",
            "再见"
    ));
    static ArrayList<String> dirtyWordPatterns = new ArrayList<>(Arrays.asList(
            "motherfucker",
            "草泥马",
            "操你妈",
            "日你妈",
            "你妈死了",
            "尼玛死了",
            "nmsl"
    ));

    /**
     * 检查某语句是否是一个需要饮料的命令
     * @param input 被检查语句
     * @return <code>true</code> 是一个需要饮料的命令 <code>false</code> 不是一个需要饮料的命令
     */
    public static boolean isNeedDrink(String input){
        for(String pattern: needDrinkPatterns){
            if(Pattern.matches(".*"+pattern+".*",input)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否是一个投掷骰子的命令
     * @param input 被检查语句
     * @return <code>true</code> 是一个投掷骰子的命令 <code>false</code> 不是一个投掷骰子的命令
     */
    public static boolean isRollDice(String input){
        for(String pattern: rollDicePatterns){
            if(Pattern.matches(pattern,input)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否是一个提到守望先锋的句子
     * @param input 被检查语句
     * @return <code>true</code> 是一个提到守望先锋的句子 <code>false</code> 不是一个提到守望先锋的句子
     */
    public static boolean isTalkOverwatch(String input){
        for(String pattern: talkOverwatchPatterns){
            if(Pattern.matches(".*"+pattern+".*",input)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否是告别
     * @param input 被检查语句
     * @return <code>true</code> 是告别 <code>false</code> 不是告别
     */
    public static boolean isGoodbye(String input){
        for(String pattern: goodbyePatterns){
            if(Pattern.matches(".*"+pattern+".*",input)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否是某种脏话
     * @param input 被检查语句
     * @return <code>true</code> 是某种脏话 <code>false</code> 不是某种脏话
     */
    public static boolean isDirtyWord(String input){
        for(String pattern: dirtyWordPatterns){
            if(Pattern.matches(".*"+pattern+".*",input)){
                return true;
            }
        }
        return false;
    }
}
