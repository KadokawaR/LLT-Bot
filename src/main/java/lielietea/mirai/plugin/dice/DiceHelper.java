package lielietea.mirai.plugin.dice;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可以直接调用骰子，而不用自己来调用
 */
public class DiceHelper {
    static final String PATTERN_COMMON_COMMAND = "(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})";
    static final String PATTERN_DND = "\\.([1-9]\\d{0,2})(d|D)[1-9][0-9]{1,7}";
    static final String PATTERN_DND_SINGLE_ROLL = "\\.(d|D)[1-9][0-9]{1,7}";

    /**
     * 直接根据命令在群内执行扔骰子动作与广播结果操作
     * @param event 命令相关的群聊消息事件
     */
    public static void executeDiceCommandFromGroup(GroupMessageEvent event){
        if(Pattern.matches(PATTERN_COMMON_COMMAND,event.getMessage().contentToString())){
            new CommonDice()
                    .setBound(captureFromPatternCommon(event.getMessage().contentToString()))
                    .roll()
                    .broadcastResult(event.getSubject());
        } else {
            if(Pattern.matches(PATTERN_DND,event.getMessage().contentToString())){
                new CommonDice()
                        .setBound(captureFromPatternDND(event.getMessage().contentToString()).get(1))
                        .setRepeat(captureFromPatternDND(event.getMessage().contentToString()).get(0))
                        .roll()
                        .broadcastResult(event.getSubject());
            } else {
                new CommonDice()
                        .setBound(captureFromPatternDNDSingleRoll(event.getMessage().contentToString()))
                        .roll()
                        .broadcastResult(event.getSubject());
            }
        }
    }

    /**
     * 直接根据私聊命令执行扔骰子动作并私聊操作
     * @param event 命令相关的好友消息事件
     */
    public static void executeDiceCommandFromFriend(FriendMessageEvent event){
        if(Pattern.matches(PATTERN_COMMON_COMMAND,event.getMessage().contentToString())){
            new CommonDice()
                    .setBound(captureFromPatternCommon(event.getMessage().contentToString()))
                    .roll()
                    .privatelyInfoResult(event.getSender());
        } else {
            if(Pattern.matches(PATTERN_DND,event.getMessage().contentToString())){
                new CommonDice()
                        .setBound(captureFromPatternDND(event.getMessage().contentToString()).get(1))
                        .setRepeat(captureFromPatternDND(event.getMessage().contentToString()).get(0))
                        .roll()
                        .privatelyInfoResult(event.getSender());
            } else {
                new CommonDice()
                        .setBound(captureFromPatternDNDSingleRoll(event.getMessage().contentToString()))
                        .roll()
                        .privatelyInfoResult(event.getSender());
            }
        }
    }

    static int captureFromPatternCommon(String input){
        String PATTERN_COMMON_COMMAND_CAPTURE = "(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})";
        Pattern capturePattern = Pattern.compile(PATTERN_COMMON_COMMAND_CAPTURE);
        Matcher matcher = capturePattern.matcher(input);
        matcher.find();
        return Integer.valueOf(matcher.group(2));
    }

    static List<Integer> captureFromPatternDND(String input){
        String PATTERN_DND_COMMAND_CAPTURE = "\\.([1-9]\\d{0,2})(d|D)([1-9]\\d{0,7})";
        Pattern capturePattern = Pattern.compile(PATTERN_DND_COMMAND_CAPTURE);
        Matcher matcher = capturePattern.matcher(input);
        matcher.find();
        List<Integer> captured = new ArrayList<>();

        captured.add(Integer.valueOf(matcher.group(1)));
        captured.add(Integer.valueOf(matcher.group(3)));

        return captured;
    }

    static int captureFromPatternDNDSingleRoll(String input){
        String PATTERN_COMMON_COMMAND_CAPTURE = "\\.(d|D)([1-9][0-9]{1,7})";
        Pattern capturePattern = Pattern.compile(PATTERN_COMMON_COMMAND_CAPTURE);
        Matcher matcher = capturePattern.matcher(input);
        matcher.find();
        return Integer.valueOf(matcher.group(2));
    }
}
