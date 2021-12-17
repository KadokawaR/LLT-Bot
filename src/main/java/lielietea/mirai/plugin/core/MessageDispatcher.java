package lielietea.mirai.plugin.core;

import lielietea.mirai.plugin.core.messagehandler.feedback.FeedBack;
import lielietea.mirai.plugin.core.messagehandler.responder.ResponderManager;
import lielietea.mirai.plugin.utils.StandardTimeUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageDispatcher {
    final static int GROUP_MESSAGE_LIMIT_PER_MIN = 10;
    final static int PERSONAL_MESSAGE_LIMIT_PER_MIN = 5;
    final static int PERSONAL_MESSAGE_LIMIT_PER_DAY = 40;
    final static int DAILY_MESSAGE_LIMIT = 4800;
    final static MessageDispatcher INSTANCE = new MessageDispatcher();
    static final CacheThreshold groupThreshold = new CacheThreshold(GROUP_MESSAGE_LIMIT_PER_MIN);
    static final CacheThreshold personalThreshold = new CacheThreshold(PERSONAL_MESSAGE_LIMIT_PER_MIN);
    static final CacheThreshold dailyThreshold = new CacheThreshold(DAILY_MESSAGE_LIMIT);
    static final CacheThreshold personalDailyThreshold = new CacheThreshold(PERSONAL_MESSAGE_LIMIT_PER_DAY);
    final Timer thresholdReset1 = new Timer(true);
    final Timer thresholdReset2 = new Timer(true);
    final ExecutorService executor;


    MessageDispatcher() {
        thresholdReset1.schedule(new TimerTask() {
                                     @Override
                                     public void run() {
                                         groupThreshold.clearCache();
                                         personalThreshold.clearCache();
                                     }
                                 }, StandardTimeUtil.getPeriodLengthInMS(0, 0, 0, 1),
                StandardTimeUtil.getPeriodLengthInMS(0, 0, 1, 0));
        thresholdReset2.schedule(new TimerTask() {
                                     @Override
                                     public void run() {
                                         dailyThreshold.clearCache();
                                         personalDailyThreshold.clearCache();
                                         System.out.println("MessageDispatcher的每日计数器已经重置。");
                                     }
                                 }, StandardTimeUtil.getStandardFirstTime(0, 0, 1),
                StandardTimeUtil.getPeriodLengthInMS(1, 0, 0, 0));
        this.executor = Executors.newCachedThreadPool();
    }

    static public MessageDispatcher getINSTANCE() {
        return INSTANCE;
    }

    public void handleMessage(MessageEvent event) {
        //首先需要没有达到消息数限制
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

            //最后交由Feedback处理
            if (!handled) {
                if (event instanceof FriendMessageEvent || event instanceof GroupTempMessageEvent) {
                    if (FeedBack.getINSTANCE().match(event)) {
                        MessageChainPackage temp = FeedBack.getINSTANCE().handle(event);
                        addToThreshold(temp);
                        handleMessageChainPackage(temp);
                    }
                }
            }
        }
    }

    // 检测是否达到发送消息数量上限
    boolean reachLimit(MessageEvent event) {
        if (dailyThreshold.reachLimit(0)) return true;
        if (event instanceof GroupMessageEvent) {
            if (groupThreshold.reachLimit(event.getSubject().getId()))
                return true;
        }
        return personalThreshold.reachLimit(event.getSender().getId()) || personalDailyThreshold.reachLimit(event.getSender().getId());
    }

    //添加到 Threshold 计数中
    void addToThreshold(MessageChainPackage messageChainPackage) {
        if (messageChainPackage.getSource() instanceof Group)
            groupThreshold.count(messageChainPackage.getSource().getId());
        personalThreshold.count(messageChainPackage.getSender().getId());
        personalDailyThreshold.count(messageChainPackage.getSender().getId());
        dailyThreshold.count(0);
    }

    public static void addToThreshold(MessageEvent event) {
        if (event instanceof GroupMessageEvent)
            groupThreshold.count(event.getSubject().getId());
        personalThreshold.count(event.getSender().getId());
        personalDailyThreshold.count(event.getSender().getId());
        dailyThreshold.count(0);
    }


    public void handleMessageChainPackage(MessageChainPackage messageChainPackage) {
        // Well 本来statistic是要在这里处理的，不过已经有 MSPE 处理了
        // 所以 MessageChainPackage 这个玩意是否还有必要？

        //加入线程池
        executor.submit(messageChainPackage::execute);
    }

    public void close() {
        executor.shutdown();
    }
}
