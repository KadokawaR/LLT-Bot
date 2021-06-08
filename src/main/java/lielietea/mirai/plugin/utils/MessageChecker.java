package lielietea.mirai.plugin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MessageChecker {
    static ArrayList<Pattern> needDrinkPatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "喝点什么",
                    "奶茶",
                    "喝了什么",
                    "喝什么",
                    "有点渴",
                    "好渴",
                    "来一杯"));
            for(String content:contents){
                this.add(Pattern.compile(".*"+content+".*"));
            }
        }
    };
    static ArrayList<Pattern> sayGoodbyePatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "下线了",
                    "我走了",
                    "拜拜"));
            for(String content:contents){
                this.add(Pattern.compile(".*"+content+".*"));
            }
        }
    };
    static ArrayList<Pattern> talkOverwatchPatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "overwatch",
                    "Overwatch",
                    "守望先锋",
                    "(玩|打)((OW)|(ow))"
            ));
            for(String content:contents){
                this.add(Pattern.compile(".*"+content+".*"));
            }
        }
    };
    static ArrayList<Pattern> dirtyWordsPatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "(日|干|操|艹|草|滚)(你|尼|泥)(妈|马|麻)",
                    "motherfucker",
                    "fuck you"
            ));
            for(String content:contents){
                this.add(Pattern.compile(".*"+content+".*"));
            }
        }
    };
    static ArrayList<Pattern> rollDicePatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})",
                    "\\.([1-9]\\d{0,2})(d|D)[1-9][0-9]{1,7}",
                    "\\.(d|D)[1-9][0-9]{1,7}"
            ));
            for(String content:contents){
                this.add(Pattern.compile(content));
            }
        }
    };

    static ArrayList<Pattern> heroLinesPatterns = new ArrayList<Pattern>() {
        {
            ArrayList<String> contents = new ArrayList<>(List.of(
                    "/大招",
                    "/英雄不朽"
            ));
            for(String content:contents){
                this.add(Pattern.compile(content));
            }
        }
    };

    /**
     * 检查某语句是否是一个需要饮料的命令
     * @param input 被检查语句
     * @return 检查结果
     */
    public static boolean isNeedDrink(String input){
        for(Pattern pattern: needDrinkPatterns){
            if(pattern.matcher(input).matches()){
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
        for(Pattern pattern: rollDicePatterns){
            if(pattern.matcher(input).matches()){
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
    public static boolean isTalkOverwatch(String input){
        for(Pattern pattern: talkOverwatchPatterns){
            if(pattern.matcher(input).matches()){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否含有脏话的命令
     * @param input 被检查语句
     * @return 检查结果
     */
    public static boolean isDirtyWord(String input){
        for(Pattern pattern: dirtyWordsPatterns){
            if(pattern.matcher(input).matches()){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否含有脏话的命令
     * @param input 被检查语句
     * @return 检查结果
     */

    public static boolean isGoodbye(String input){
        for(Pattern pattern: sayGoodbyePatterns){
            if(pattern.matcher(input).matches()){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否含有脏话的命令
     * @param input 被检查语句
     * @return 检查结果
     */

    public static boolean isBoobCardGame(String input){
        if(input.equals("/bc")) return true;
        else return false;
    }

    /**
     * 检查某语句是否含有脏话的命令
     * @param input 被检查语句
     * @return 检查结果
     */

    public static boolean isHeroLines(String input){
        for(Pattern pattern: heroLinesPatterns){
            if(pattern.matcher(input).matches()){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某语句是否是一个 X 的命令
     * 更为普适的字段检测
     * @param patternIn 匹配模式
     * @param input 被检查语句
     * @return
     */

    public static boolean generalRegCommandCheck(ArrayList<Pattern> patternIn,String input){
        for(Pattern pattern: patternIn){
            if(pattern.matcher(input).matches()){
                return true;
            }
        }
        return false;
    }



}
