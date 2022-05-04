package lielietea.mirai.plugin.core.responder.help;

import lielietea.mirai.plugin.core.responder.RespondTask;
import lielietea.mirai.plugin.core.responder.MessageResponder;
import lielietea.mirai.plugin.utils.exception.NoHandlerMethodMatchException;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;


public class Help implements MessageResponder<MessageEvent> {
    static final List<MessageType> type = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));
    static final Map<Predicate<String>, String> MAP = new HashMap<>();

    static {
        {
            MAP.put(string -> string.equalsIgnoreCase("/help") || string.equals("帮助"), Speech.HELP);
            //MAP.put(event -> event.getMessage().contentToString().equals("/funct") || event.getMessage().contentToString().equals("/功能"), Speech.FUNCT);
            MAP.put(string -> string.equalsIgnoreCase("/conta") || string.equals("联系作者"), Speech.CONTA);
            MAP.put(string -> string.equalsIgnoreCase("/intro") || string.equals("介绍"), Speech.INTRO);
            //MAP.put(event -> event.getMessage().contentToString().equals("/discl") || event.getMessage().contentToString().equals("/免责协议"), Speech.DISCLAIMER);
            MAP.put(string -> string.equalsIgnoreCase("/usage") || string.equals("用法"), Speech.USAGE);
        }
    }

    @Override
    public boolean match(String content) {
        for (Predicate<String> predicate : MAP.keySet()) {
            if (predicate.test(content)) return true;
        }
        return false;
    }

    @Override
    public RespondTask handle(MessageEvent event) throws NoHandlerMethodMatchException {
        for (Map.Entry<Predicate<String>, String> entry : MAP.entrySet()) {
            if (entry.getKey().test(event.getMessage().contentToString()))
                return RespondTask.of(event, entry.getValue(), this);
        }
        throw new NoHandlerMethodMatchException("帮助", event);
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }
    
    @Override
    public String getName() {
        return "帮助";
    }

    static class Speech {
        static final String HELP = "输入下方带有斜杠的关键词可以获得相关信息。\n\n" +
                "/intro 七筒简介\n" +
                "/usage 如何在自己的群中使用七筒\n" +
                "/discl 免责协议\n" +
                "/funct 功能列表\n" +
                "/conta 联系开发者";
        static final String INTRO = "七筒是一个用于服务简体中文 Furry 社群的 QQ 机器人项目，皆在试图为群聊增加一些乐趣。请发送/funct 来了解如何使用七筒。注意，不要和我，也不要和生活太较真。\n"+
                "如需要联系七筒的开发者和体验七筒功能，请添加公众聊天群：932617537。\n" +
                "如需要获得七筒的最新消息，请添加通知群：948979109。";
        static final String USAGE = "点击头像添加七筒为好友，并将其邀请到QQ群聊中，即可在该群聊中使用七筒的服务。如果需要查看七筒的功能列表，请输入/funct。";
        static final String CONTA = "如果需要报错或者告知开发者你的想法，请在任意七筒所在位置发送开头是”意见反馈“的消息，七筒会自动收集。有且只有开头是“意见反馈”字样的单条消息才会被接收。\n"+
                "如需要联系七筒的开发者和体验七筒功能，请添加公众聊天群：932617537。\n" +
                        "如需要获得七筒的最新消息，请添加通知群：948979109。";
    }
}
