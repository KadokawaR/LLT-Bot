package lielietea.mirai.plugin.messageresponder.dice;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.dice.DiceFactory;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类用来处理骰子相关消息
 */
public class DiceMessageHandler implements MessageHandler<MessageEvent> {
    static final List<Pattern> regPattern = new ArrayList<>();
    static final Pattern PATTERN_COMMON_COMMAND = Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})");
    static final Pattern PATTERN_DND = Pattern.compile("\\.([1-9]\\d{0,2})([dD])[1-9][0-9]{1,7}");
    static final Pattern PATTERN_DND_SINGLE_ROLL = Pattern.compile("\\.([dD])[1-9][0-9]{1,7}");
    static final Pattern CAPTURE_PATTERN_COMMON_COMMAND = Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})");
    static final Pattern CAPTURE_PATTERN_DND = Pattern.compile("\\.([1-9]\\d{0,2})([dD])([1-9]\\d{0,7})");
    static final Pattern CAPTURE_PATTERN_DND_SINGLE_ROLL = Pattern.compile("\\.([dD])([1-9][0-9]{1,7})");

    static{
        {
            regPattern.add(Pattern.compile("(/dice|/d|/Dice|/D)\\s?([1-9]\\d{0,7})"));
            regPattern.add(Pattern.compile("\\.([1-9]\\d{0,2})([dD])[1-9][0-9]{1,7}"));
            regPattern.add(Pattern.compile("\\.([dD])[1-9][0-9]{1,7}"));
        }
    }

    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND,MessageType.GROUP));

    @Override
    public boolean handleMessage(MessageEvent event){
        if(messagePatternMatches(event)){
            if(event instanceof GroupMessageEvent){
                executeDiceCommandFromGroup((GroupMessageEvent) event);
                return true;
            }
            else if(event instanceof FriendMessageEvent) {
                executeDiceCommandFromFriend((FriendMessageEvent) event);
                return true;
            }
        }
        return false;
    }
    public boolean messagePatternMatches(MessageEvent event) {
        for(Pattern pattern: regPattern){
            if(pattern.matcher(event.getMessage().contentToString()).matches()){
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }

    @Override
    public String getFunctionName() {
        return "骰子";
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static int captureFromPatternCommon(String input){
        Matcher matcher = CAPTURE_PATTERN_COMMON_COMMAND.matcher(input);
        matcher.find();
        return Integer.parseInt(matcher.group(2));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static List<Integer> captureFromPatternDND(String input){
        Matcher matcher = CAPTURE_PATTERN_DND.matcher(input);
        matcher.find();
        List<Integer> captured = new ArrayList<>();

        captured.add(Integer.valueOf(matcher.group(1)));
        captured.add(Integer.valueOf(matcher.group(3)));

        return captured;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static int captureFromPatternDNDSingleRoll(String input){
        Matcher matcher = CAPTURE_PATTERN_DND_SINGLE_ROLL.matcher(input);
        matcher.find();
        return Integer.parseInt(matcher.group(2));
    }
}