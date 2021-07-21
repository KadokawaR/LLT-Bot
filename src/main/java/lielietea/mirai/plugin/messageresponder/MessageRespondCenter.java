package lielietea.mirai.plugin.messageresponder;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.admintools.StatisticController;
import lielietea.mirai.plugin.messageresponder.autoreply.AntiDirtyWordMessageHandler;
import lielietea.mirai.plugin.messageresponder.autoreply.AntiOverwatchMessageHandler;
import lielietea.mirai.plugin.messageresponder.autoreply.GoodbyeMessageHandler;
import lielietea.mirai.plugin.messageresponder.autoreply.GreetingMessageHandler;
import lielietea.mirai.plugin.messageresponder.dice.DiceMessageHandler;
import lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker.MealPicker;
import lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker.PizzaPicker;
import lielietea.mirai.plugin.messageresponder.feastinghelper.drinkpicker.DrinkPicker;
import lielietea.mirai.plugin.messageresponder.fursona.FursonaPunk;
import lielietea.mirai.plugin.messageresponder.help.Help;
import lielietea.mirai.plugin.messageresponder.lovelypicture.LovelyImage;
import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryBummerMessageHandler;
import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryC4MessageHandler;
import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryWinnerMessageHandler;
import lielietea.mirai.plugin.messageresponder.mahjong.FortuneTeller;
import lielietea.mirai.plugin.messageresponder.overwatch.HeroLinesMessageHandler;
import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.GroupID;
import lielietea.mirai.plugin.utils.messagematcher.*;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 这个类管理所有回复处理器，并将回复事件传递给回复处理器。回复处理器是用来处理消息事件，并根据情况进行回复的组件(包括自动打招呼，关键词触发，指令 etc)，
 *
 * <p>该管理器是线程安全的.</p>
 *
 * <p>所有回复处理器(也就是不同功能的回复模组)，都需要实现 {@link MessageHandler} 接口，并在使用 {@link #register(MessageHandler)} 进行注册。推荐在 {@link #ini()} 方法内进行注册</p>
 */
public class MessageRespondCenter {
    static final ReadWriteLock REENTRANT_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    static final Lock READ_LOCK = REENTRANT_READ_WRITE_LOCK.readLock();
    static final Lock WRITE_LOCK = REENTRANT_READ_WRITE_LOCK.writeLock();
    static final Timer TIMER = new Timer(true);
    static final MessageRespondCenter INSTANCE = new MessageRespondCenter();

    static {
        //每隔1个小时自动优化回复处理器顺序
        Calendar calendar = Calendar.getInstance();
        int baseHour = calendar.get(Calendar.HOUR_OF_DAY) / 6 * 6 + 6;
        baseHour = baseHour == 24 ? 0 : baseHour;
        calendar.set(Calendar.HOUR_OF_DAY, baseHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
        }
        TIMER.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               String result = MessageRespondCenter.getINSTANCE().optimizeHandlerSequence();
                               //Notify Devs
                               List<Bot> bots = Bot.getInstances();
                               for (Bot bot : bots) {
                                   Group group = bot.getGroup(GroupID.DEV);
                                   if (group != null) group.sendMessage(result);
                               }
                           }
                       },
                date,
                6 * 60 * 60 * 1000);
    }

    final List<BoxedHandler> handlers;

    MessageRespondCenter() {
        handlers = new ArrayList<>();
    }

    public static MessageRespondCenter getINSTANCE() {
        return INSTANCE;
    }

    /**
     * 自动处理来自群的消息
     *
     * @param event 群消息事件
     */
    public void handleGroupMessageEvent(MessageEvent event) throws IOException {
        READ_LOCK.lock();
        try {
            BotChecker botChecker = new BotChecker();
            if (StatisticController.checkGroupCount((GroupMessageEvent) event)&&!botChecker.checkIdentity((GroupMessageEvent) event)) {
                for (BoxedHandler handler : handlers) {
                    if (handler.isBetaFeature()) {
                        if (true/*这里缺个Group Config的判断*/) {
                            if (handler.handleMessage(event, MessageHandler.MessageType.GROUP)) {
                                StatisticController.addMinuteCount(event.getSubject().getId());
                                StatisticController.countIn(event.getSubject().getId(), handler.getUUID());
                                break;
                            }
                        }
                    } else if (handler.needPermission()) {
                        if (true/*这里缺个Group Config的判断*/) {
                            if (handler.handleMessage(event, MessageHandler.MessageType.GROUP)) {
                                StatisticController.addMinuteCount(event.getSubject().getId());
                                StatisticController.countIn(event.getSubject().getId(), handler.getUUID());
                                break;
                            }
                        }
                    } else if (handler.handleMessage(event, MessageHandler.MessageType.GROUP)) {
                        StatisticController.addMinuteCount(event.getSubject().getId());
                        StatisticController.countIn(event.getSubject().getId(), handler.getUUID());
                        break;
                    }
                }
            }


        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 自动处理来自好友的消息
     *
     * @param event 好友消息事件
     */
    public void handleFriendMessageEvent(MessageEvent event) throws IOException {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.handleMessage(event, MessageHandler.MessageType.FRIEND)) break;
            }
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 自动处理来自群临时的消息
     *
     * @param event 群临时消息事件
     */
    public void handleGroupTempMessageEvent(MessageEvent event) throws IOException {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.handleMessage(event, MessageHandler.MessageType.TEMP)) break;
            }
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 自动处理来自陌生人的消息
     *
     * @param event 陌生人消息事件
     */
    public void handleStrangerMessageEvent(MessageEvent event) throws IOException {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.handleMessage(event, MessageHandler.MessageType.STRANGER)) break;
            }
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 注册回复处理器，处理器都需要实现 {@link MessageHandler} 接口，
     *
     * @param handler 回复处理器类
     */
    @SuppressWarnings("unchecked")
    public void register(MessageHandler<? extends MessageEvent> handler) {
        handlers.add(new BoxedHandler((MessageHandler<MessageEvent>) handler));
    }

    /**
     * 初始化该管理器类。必须在插件启动时调用。
     */
    public void ini() {
        register(new FursonaPunk());
        register(new Help());
        register(new LotteryWinnerMessageHandler(new LotteryWinnerMessageMatcher()));
        register(new LotteryBummerMessageHandler(new LotteryBummerMessageMatcher()));
        register(new LotteryC4MessageHandler(new LotteryC4MessageMatcher()));
        register(new DrinkPicker(new DrinkPickerMessageMatcher()));
        register(new MealPicker(new MealPickerMessageMatcher()));
        register(new PizzaPicker(new PizzaPickerMessageMatcher()));
        register(new FortuneTeller());
        register(new DiceMessageHandler());
        register(new GoodbyeMessageHandler(new GoodbyeMessageMatcher()));
        register(new AntiOverwatchMessageHandler(new MentionOverwatchMessageMatcher()));
        register(new AntiDirtyWordMessageHandler(new DirtyWordMessageMatcher()));
        register(new GreetingMessageHandler());
        register(new HeroLinesMessageHandler(new RequestOverwatchHeroLineMessageMatcher()));
        register(new LovelyImage(new DogMessageMatcher(),
                new DogShibaMessageMatcher(),
                new DogHuskyMessageMatcher(),
                new DogBerneseMessageMatcher(),
                new DogMalamuteMessageMatcher(),
                new DogGSDMessageMatcher(),
                new DogSamoyedMessageMatcher(),
                new CatMessageMatcher()));
    }

    /**
     * 优化回复处理器的调用顺序
     *
     * @return 优化后的回复处理器顺序与调用统计
     */
    public String optimizeHandlerSequence() {
        WRITE_LOCK.lock();
        try {
            Collections.sort(handlers);
            StringBuilder builder = new StringBuilder("优化后顺序为：\n");
            for (BoxedHandler handler : handlers) {
                builder.append("[Name:").append(handler.getName()).append("|CallingTime:").append(handler.getCount()).append("]\n");
            }
            return builder.toString();
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    /**
     * 用此方法完成关闭Mirai时的收尾工作，必须在Mirai关闭时调用。
     */
    public void close() {
        for (BoxedHandler handler : handlers) {
            handler.close();
        }
    }

    /**
     * 通过UUID来获取回复处理器的名字
     *
     * @return 如果对应UUID的回复处理器存在，那么返回一个包含名字的Optional
     */
    public Optional<String> getName(UUID uuid) {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getUUID().equals(uuid))
                    return Optional.of(handler.getName());
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过名字来获取回复处理器的UUID
     *
     * @return 如果对应名字的回复处理器存在，那么返回一个包含UUID的Optional
     */
    public Optional<UUID> getUUID(String name) {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getName().equals(name))
                    return Optional.of(handler.getUUID());
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过UUID来获取对应回复处理器调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     * @return -1：该回复处理器不存在。
     */
    public int getStatistics(UUID uuid, boolean reset) {
        READ_LOCK.lock();
        try {
            BoxedHandler handler = getHandler(uuid).orElse(null);
            if (handler != null) {
                if (reset) handler.resetCount();
                return handler.getCount();
            }
            return -1;
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过名字来获取对应回复处理器调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     * @return -1：该回复处理器不存在。
     */
    public int getStatistics(String name, boolean reset) {
        READ_LOCK.lock();
        try {
            BoxedHandler handler = getHandler(name).orElse(null);
            if (handler != null) {
                if (reset) handler.resetCount();
                return handler.getCount();
            }
            return -1;
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过获取所有回复处理器调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     */
    public Map<UUID, Integer> getStatistics(boolean reset) {
        READ_LOCK.lock();
        try {
            Map<UUID, Integer> result = new HashMap<>();
            for (BoxedHandler handler : handlers) {
                result.put(handler.getUUID(), handler.getCount());
                if (reset) handler.resetCount();
            }
            return result;
        } finally {
            READ_LOCK.unlock();
        }
    }


    /**
     * 通过UUID来获取对应回复处理器群消息调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     * @return 如果对应UUID的回复处理器存在，那么返回一个包含统计数据的的Optional。请注意：如果该回复处理器不支持处理群消息，仍将返回一个包含空Map的Optional。
     */
    public Optional<Map<Long, Integer>> getGroupStatistics(UUID uuid, boolean reset) {
        READ_LOCK.lock();
        try {
            BoxedHandler handler = getHandler(uuid).orElse(null);
            if (handler != null) {
                if (reset) handler.resetGroupCount();
                return Optional.of(handler.getGroupCount());
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过名字来获取对应回复处理器群消息调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     * @return 如果对应名字的回复处理器存在，那么返回一个包含统计数据的的Optional。请注意：如果该回复处理器不支持处理群消息，仍将返回一个包含空Map的Optional。
     */
    public Optional<Map<Long, Integer>> getGroupStatistics(String name, boolean reset) {
        READ_LOCK.lock();
        try {
            BoxedHandler handler = getHandler(name).orElse(null);
            if (handler != null) {
                if (reset) handler.resetGroupCount();
                return Optional.of(handler.getGroupCount());
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 通过获取所有回复处理器群消息调用次数的统计数据
     *
     * @param reset 是否在获取后重置统计数据
     */
    public Table<Long, UUID, Integer> getGroupStatistics(boolean reset) {
        READ_LOCK.lock();
        try {
            Table<Long, UUID, Integer> result = HashBasedTable.create();
            for (BoxedHandler handler : handlers) {
                if (!handler.getGroupCount().isEmpty()) {
                    Map<Long, Integer> handlerGroupCount = handler.groupCount;
                    for (Map.Entry<Long, Integer> entry : handlerGroupCount.entrySet()) {
                        result.put(entry.getKey(), handler.getUUID(), entry.getValue());
                    }
                    if (reset) handler.resetGroupCount();
                }
            }
            return result;
        } finally {
            READ_LOCK.unlock();
        }
    }


    Optional<BoxedHandler> getHandler(UUID uuid) {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getUUID().equals(uuid))
                    return Optional.of(handler);
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    Optional<BoxedHandler> getHandler(String name) {
        READ_LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getName().equals(name))
                    return Optional.of(handler);
            }
            return Optional.empty();
        } finally {
            READ_LOCK.unlock();
        }
    }

    static class BoxedHandler implements Comparable<BoxedHandler> {
        final MessageHandler<MessageEvent> handler;
        final List<MessageHandler.MessageType> types;
        final Map<Long, Integer> groupCount;
        int count;

        BoxedHandler(MessageHandler<MessageEvent> handler) {
            this.handler = handler;
            types = handler.types();
            count = 0;
            groupCount = new HashMap<>();
        }

        UUID getUUID() {
            return handler.getUUID();
        }

        String getName() {
            return handler.getFunctionName();
        }

        boolean handleMessage(MessageEvent event, MessageHandler.MessageType messageType) throws IOException {
            if (fit(messageType))
                if (handler.handleMessage(event)) {
                    count++;
                    if (isGroupMessage(event)) {
                        addToGroupCount(((GroupMessageEvent) event).getGroup().getId());
                    }
                    return true;
                }
            return false;
        }

        boolean needPermission() {
            return handler.isPermissionRequired();
        }

        boolean isBetaFeature() {
            return handler.isOnBeta();
        }

        boolean fit(MessageHandler.MessageType type) {
            return types.contains(type);
        }

        boolean isGroupMessage(MessageEvent event) {
            return event instanceof GroupMessageEvent;
        }

        int getCount() {
            return count;
        }

        void close() {
            handler.onclose();
        }

        Map<Long, Integer> getGroupCount() {
            return new HashMap<>(groupCount);
        }

        void addToGroupCount(long groupID) {
            if (groupCount.containsKey(groupID))
                groupCount.replace(groupID, groupCount.get(groupID) + 1);
            else
                groupCount.put(groupID, 1);
        }

        void resetCount() {
            count = 0;
        }

        void resetGroupCount() {
            groupCount.clear();
        }

        @Override
        public int compareTo(@NotNull BoxedHandler o) {
            return count - o.getCount();
        }
    }

}
