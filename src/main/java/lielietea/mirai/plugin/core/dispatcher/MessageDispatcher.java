package lielietea.mirai.plugin.core.dispatcher;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.feedback.FeedBack;
import lielietea.mirai.plugin.core.messagehandler.responder.ResponderManager;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageDispatcher {
    final static int GROUP_MESSAGE_LIMIT_PER_MIN = 30;
    final static int PERSONAL_MESSAGE_LIMIT_PER_MIN = 5;
    final static MessageDispatcher INSTANCE = new MessageDispatcher();
    final CacheThreshold groupThreshold = new CacheThreshold(GROUP_MESSAGE_LIMIT_PER_MIN);
    final CacheThreshold personalThreshold = new CacheThreshold(PERSONAL_MESSAGE_LIMIT_PER_MIN);
    final Timer thresholdReset = new Timer(true);
    final ExecutorService executor;


    MessageDispatcher() {
        thresholdReset.schedule(new TimerTask() {
            @Override
            public void run() {
                groupThreshold.clearCache();
                personalThreshold.clearCache();
            }
        }, 10000, 60 * 1000);
        this.executor = Executors.newCachedThreadPool();
    }

    static public MessageDispatcher getINSTANCE() {
        return INSTANCE;
    }

    public void handleMessage(MessageEvent event) {
        //首先需要没有达到每分钟消息数限制
        if (!reachLimit(event)) {
            //最先交由ResponderManager处理
            boolean handled = false;
            Optional<UUID> boxedHandler = ResponderManager.getINSTANCE().match(event);
            if (boxedHandler.isPresent()) {
                {
                    handled = true;
                    MessageChainPackage temp = ResponderManager.getINSTANCE().handle(event, boxedHandler.get());
                    addToThreshold(temp);
                    handleMessageChainPackage(temp);
                }
            }

            //然后交由GameManager处理
            //TODO:GameManager还没改写

            //最后交由Feedback处理
            if (!handled) {
                if (event instanceof FriendMessageEvent) {
                    if (FeedBack.getINSTANCE().match((FriendMessageEvent) event)) {
                        MessageChainPackage temp = FeedBack.getINSTANCE().handle((FriendMessageEvent) event);
                        addToThreshold(temp);
                        handleMessageChainPackage(temp);
                    }
                }
            }
        }
    }

    boolean reachLimit(MessageEvent event) {
        return event instanceof GroupMessageEvent ?
                (groupThreshold.reachLimit(event.getSubject().getId()) || personalThreshold.reachLimit(event.getSender().getId()))
                : personalThreshold.reachLimit(event.getSender().getId());
    }

    void addToThreshold(MessageChainPackage messageChainPackage) {
        if (messageChainPackage.getSource() instanceof Group) {
            groupThreshold.count(messageChainPackage.getSource().getId());
            personalThreshold.count(messageChainPackage.getSender().getId());
        } else {
            personalThreshold.count(messageChainPackage.getSender().getId());
        }
    }


    public void handleMessageChainPackage(MessageChainPackage messageChainPackage) {
        //首先加告知StatisticController
        //TODO: Add Hook To StatisticController

        //TODO 如何处理 MessagePackage自带的Note？

        //最后加入线程池
        executor.submit(messageChainPackage::execute);
    }

    public void close() {
        executor.shutdown();
    }
}
