package lielietea.mirai.plugin.messageresponder;


import lielietea.mirai.plugin.messageresponder.autoreply.AutoReplyMessageHandler;
import lielietea.mirai.plugin.messageresponder.autoreply.GreetingMessageHandler;
import lielietea.mirai.plugin.messageresponder.dice.DiceMessageHandler;
import lielietea.mirai.plugin.messageresponder.feastinghelper.DrinkPicker;
import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryWinnerMessageHandler;
import lielietea.mirai.plugin.messageresponder.overwatch.HeroLinesMessageHandler;
import lielietea.mirai.plugin.utils.messagematcher.*;
import net.mamoe.mirai.event.events.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类管理所有回复处理器，并将回复事件传递给回复处理器。回复处理器是用来处理消息事件，并根据情况进行回复的组件(包括自动打招呼，关键词触发，指令 etc)，
 *
 * <p>所有回复处理器(也就是不同功能的回复模组)，都需要实现 {@link MessageHandler} 接口，并在使用 {@link #register(MessageHandler)} 进行注册。推荐在 {@link #ini()} 方法内进行注册</p>
 */
public class MessageRespondCenter {
    static final List<MessageHandler<MessageEvent>> groupMessageHandlers = new ArrayList<>();
    static final List<MessageHandler<MessageEvent>> groupTempMessageHandlers = new ArrayList<>();
    static final List<MessageHandler<MessageEvent>> friendMessageHandlers = new ArrayList<>();
    static final List<MessageHandler<MessageEvent>> strangerMessageHandlers = new ArrayList<>();
    static final List<MessageHandler<MessageEvent>> reloadable = new ArrayList<>();

    static final MessageRespondCenter INSTANCE = new MessageRespondCenter();

    public static MessageRespondCenter getINSTANCE() {
        return INSTANCE;
    }

    /**
     * 自动处理来自群的消息
     * @param event 群消息事件
     */
    public void handleGroupMessageEvent(MessageEvent event){
        for(MessageHandler<MessageEvent> handler:groupMessageHandlers){
            if(handler.handleMessage(event)) break;
        }
    }

    /**
     * 自动处理来自好友的消息
     * @param event 好友消息事件
     */
    public void handleFrinedMessageEvent(MessageEvent event){
        for(MessageHandler<MessageEvent> handler:friendMessageHandlers){
            if(handler.handleMessage(event)) break;
        }
    }

    /**
     * 自动处理来自群临时的消息
     * @param event 群临时消息事件
     */
    public void handleGroupTempMessageEvent(MessageEvent event){
        for(MessageHandler<MessageEvent> handler:groupTempMessageHandlers){
            if(handler.handleMessage(event)) break;
        }
    }

    /**
     * 自动处理来自陌生人的消息
     * @param event 陌生人消息事件
     */
    public void handleStrangerMessageEvent(MessageEvent event){
        for(MessageHandler<MessageEvent> handler:strangerMessageHandlers){
            if(handler.handleMessage(event)) break;
        }
    }

    /**
     * 注册回复处理器，处理器都需要实现 {@link MessageHandler} 接口，
     * @param handler 回复处理器类
     */
    @SuppressWarnings("unchecked")
    public void register(MessageHandler<? extends MessageEvent> handler){
        if(handler.types().contains(MessageHandler.MessageType.GROUP)) groupMessageHandlers.add((MessageHandler<MessageEvent>) handler);
        if(handler.types().contains(MessageHandler.MessageType.FRIEND)) friendMessageHandlers.add((MessageHandler<MessageEvent>) handler);
        if(handler.types().contains(MessageHandler.MessageType.STRANGER)) strangerMessageHandlers.add((MessageHandler<MessageEvent>) handler);
        if(handler.types().contains(MessageHandler.MessageType.TEMP)) groupTempMessageHandlers.add((MessageHandler<MessageEvent>) handler);
        if(handler instanceof Reloadable) reloadable.add((MessageHandler<MessageEvent>) handler);
    }

    /**
     * 初始化该管理器类。必须在插件启动时调用。
     */
    public void ini(){
        register(new AutoReplyMessageHandler(new MentionOverwatchMessageMatcher(),new DirtyWordMessageMatcher(),new GoodbyeMessageMatcher()));
        register(new GreetingMessageHandler());
        register(new DiceMessageHandler());
        register(new DrinkPicker(new RequestDrinkMessageMatcher()));
        register(new HeroLinesMessageHandler(new RequestOverwatchHeroLineMessageMatcher()));
        register(new LotteryWinnerMessageHandler(new LotteryWinnerMessageMatcher(),new LotteryBummerMessageMatcher(),new LotteryC4MessageMatcher()));
    }

    /**
     * 实现了 {@link Reloadable} 的回复处理器，可用此方法来完成回复设置重载
     */
    public void reload(){
        for(MessageHandler<MessageEvent> reloadable:reloadable){
            ((Reloadable) reloadable).reload();
        }
    }


}
