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

    /**
     * 检查某语句是否是一个需要饮料的命令
     * @param input 被检查语句
     * @return 检查结果
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
     * @return 检查结果
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
     * 检查某语句是否是一个 X 的命令
     * 更为普适的字段检测
     * @param input 被检查语句
     * @return 检查结果
     */
    public static boolean isWhat(ArrayList<String> WhatPatterns,String input){
        for(String pattern: WhatPatterns){
            if(Pattern.matches(pattern,input)){
                return true;
            }
        }
        return false;
    }

}
