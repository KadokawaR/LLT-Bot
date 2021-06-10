package lielietea.mirai.plugin.dice;

import lielietea.mirai.plugin.utils.messagematcher.DiceMessageMatcher;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类用来处理骰子相关消息
 */
public class DiceCommandManager {
    static final Pattern PATTERN_COMMON_COMMAND = Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})");
    static final Pattern PATTERN_DND = Pattern.compile("\\.([1-9]\\d{0,2})(d|D)[1-9][0-9]{1,7}");
    static final Pattern PATTERN_DND_SINGLE_ROLL = Pattern.compile("\\.(d|D)[1-9][0-9]{1,7}");
    static final Pattern CAPTURE_PATTERN_COMMON_COMMAND = Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})");
    static final Pattern CAPTURE_PATTERN_DND = Pattern.compile("\\.([1-9]\\d{0,2})(d|D)([1-9]\\d{0,7})");
    static final Pattern CAPTURE_PATTERN_DND_SINGLE_ROLL = Pattern.compile("\\.(d|D)([1-9][0-9]{1,7})");

    static final MessageMatcher<MessageEvent> requestRollingDiceMatcher = new DiceMessageMatcher();

    public static void handleMessage(MessageEvent event){
        if(requestRollingDiceMatcher.matches(event)){
            if(event instanceof GroupMessageEvent)
                executeDiceCommandFromGroup((GroupMessageEvent) event);
            else if(event instanceof FriendMessageEvent)
                executeDiceCommandFromFriend((FriendMessageEvent) event);
        }
    }

    //直接根据命令在群内执行扔骰子动作与广播结果操作
    static void executeDiceCommandFromGroup(GroupMessageEvent event){
        if(PATTERN_COMMON_COMMAND.matcher(event.getMessage().contentToString()).matches()){
            DiceFactory.getCustomDice(captureFromPatternCommon(event.getMessage().contentToString()),1)
                    .broadcastResult(event.getSubject());
        } else {
            if(PATTERN_DND.matcher(event.getMessage().contentToString()).matches()){
                DiceFactory.getCustomDice(captureFromPatternDND(event.getMessage().contentToString()).get(1),
                        captureFromPatternDND(event.getMessage().contentToString()).get(0))
                        .broadcastResult(event.getSubject());
            } else if(PATTERN_DND_SINGLE_ROLL.matcher(event.getMessage().contentToString()).matches()) {
                DiceFactory.getCustomDice(captureFromPatternDNDSingleRoll(event.getMessage().contentToString()),1)
                        .broadcastResult(event.getSubject());
            }
        }
    }


    //直接根据私聊命令执行扔骰子动作并私聊操作
    static void executeDiceCommandFromFriend(FriendMessageEvent event){
        if(PATTERN_COMMON_COMMAND.matcher(event.getMessage().contentToString()).matches()){
            DiceFactory.getCustomDice(captureFromPatternCommon(event.getMessage().contentToString()),1)
                    .privatelyInfoResult(event.getSender());
        } else {
            if(PATTERN_DND.matcher(event.getMessage().contentToString()).matches()){
                DiceFactory.getCustomDice(captureFromPatternDND(event.getMessage().contentToString()).get(1),
                        captureFromPatternDND(event.getMessage().contentToString()).get(0))
                        .privatelyInfoResult(event.getSender());
            } else if(PATTERN_DND_SINGLE_ROLL.matcher(event.getMessage().contentToString()).matches()) {
                DiceFactory.getCustomDice(captureFromPatternDNDSingleRoll(event.getMessage().contentToString()),1)
                        .privatelyInfoResult(event.getSender());
            }
        }
    }

    static int captureFromPatternCommon(String input){
        Matcher matcher = CAPTURE_PATTERN_COMMON_COMMAND.matcher(input);
        matcher.find();
        return Integer.valueOf(matcher.group(2));
    }

    static List<Integer> captureFromPatternDND(String input){
        Matcher matcher = CAPTURE_PATTERN_DND.matcher(input);
        matcher.find();
        List<Integer> captured = new ArrayList<>();

        captured.add(Integer.valueOf(matcher.group(1)));
        captured.add(Integer.valueOf(matcher.group(3)));

        return captured;
    }

    static int captureFromPatternDNDSingleRoll(String input){
        Matcher matcher = CAPTURE_PATTERN_DND_SINGLE_ROLL.matcher(input);
        matcher.find();
        return Integer.valueOf(matcher.group(2));
    }
}
