package lielietea.mirai.plugin.core.responder;


import lielietea.mirai.plugin.NotificationSetting;
import lielietea.mirai.plugin.administration.statistics.MPSEHandler.MPSEStatistics;
import lielietea.mirai.plugin.core.responder.basic.AntiDirtyWord;
import lielietea.mirai.plugin.core.responder.basic.AntiOverwatch;
import lielietea.mirai.plugin.core.responder.basic.Goodbye;
import lielietea.mirai.plugin.core.responder.basic.Greeting;
import lielietea.mirai.plugin.core.responder.dice.PlayDice;
import lielietea.mirai.plugin.core.responder.feastinghelper.dinnerpicker.MealPicker;
import lielietea.mirai.plugin.core.responder.feastinghelper.dinnerpicker.PizzaPicker;
import lielietea.mirai.plugin.core.responder.feastinghelper.drinkpicker.DrinkPicker;
import lielietea.mirai.plugin.core.responder.feedback.FeedBack;
import lielietea.mirai.plugin.core.responder.furrygamesindex.FurryGamesRandom;
import lielietea.mirai.plugin.core.responder.furrygamesindex.FurryGamesSearch;
import lielietea.mirai.plugin.core.responder.fursona.FursonaPunk;
import lielietea.mirai.plugin.core.responder.help.DisclTemporary;
import lielietea.mirai.plugin.core.responder.help.FunctTemporary;
import lielietea.mirai.plugin.core.responder.help.Help;
import lielietea.mirai.plugin.core.responder.help.NewFunct;
import lielietea.mirai.plugin.core.responder.lotterywinner.LotteryBummerMessageHandler;
import lielietea.mirai.plugin.core.responder.lotterywinner.LotteryC4MessageHandler;
import lielietea.mirai.plugin.core.responder.lotterywinner.LotteryWinnerMessageHandler;
import lielietea.mirai.plugin.core.responder.lovelypicture.LovelyImage;
import lielietea.mirai.plugin.core.responder.mahjong.FortuneTeller;
import lielietea.mirai.plugin.core.responder.overwatch.HeroLinesSelector;
import lielietea.mirai.plugin.utils.exception.MessageEventTypeException;
import lielietea.mirai.plugin.utils.MessageUtil;
import lielietea.mirai.plugin.utils.StandardTimeUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????(???????????????????????????????????????????????? etc)???
 *
 * <p>??????????????????????????????.</p>
 *
 * <p>?????????????????????(????????????????????????????????????)?????????????????? {@link MessageResponder} ????????????????????? {@link #register(Supplier)} ???????????????????????? {@link #ini()} ?????????????????????</p>
 */
public class ResponderManager {
    static final Lock LOCK = new ReentrantLock();
    static final Timer TIMER = new Timer(true);

    static {
        TIMER.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               if(NotificationSetting.NewlyOptimizedSequenceNotification) {
                                   String result = ResponderManager.getINSTANCE().optimizeHandlerSequence(true);
                                   for (Bot bot : Bot.getInstances()) {
                                       MessageUtil.notifyDevGroup(result, bot);
                                   }
                               }
                           }
                       },
                StandardTimeUtil.getStandardFirstTime(0, 0, 1),
                StandardTimeUtil.getPeriodLengthInMS(0, 6, 0, 0));
    }

    final List<BoxedHandler> handlers;
    final Map<UUID, BoxedHandler> addressMap;

    ResponderManager() {
        handlers = new ArrayList<>();
        addressMap = new HashMap<>();
    }

    static final ResponderManager INSTANCE = new ResponderManager();

    public static ResponderManager getINSTANCE() {
        return INSTANCE;
    }

    public RespondTask handle(MessageEvent event, UUID handler) {
        return addressMap.get(handler).handle(event);
    }

    public Optional<UUID> match(MessageEvent event) {
        LOCK.lock();
        try {
            MessageResponder.MessageType type = null;
            try {
                type = getType(event);
            } catch (MessageEventTypeException e) {
                e.printStackTrace();
            }
            if (type == null) return Optional.empty();
            for (BoxedHandler handler : handlers) {
                if (handler.match(event, type)) {
                    return Optional.of(handler.getUUID());
                }
            }
            return Optional.empty();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * ???????????????????????????????????????????????? {@link MessageResponder} ?????????
     */
    @SuppressWarnings("unchecked")
    public void register(Supplier<MessageResponder<? extends MessageEvent>> handler) {
        BoxedHandler registry = new BoxedHandler((MessageResponder<MessageEvent>) handler.get());
        handlers.add(registry);
        addressMap.put(registry.getUUID(), registry);
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    public void ini() {
        register(FursonaPunk::new);
        register(Help::new);
        register(LotteryWinnerMessageHandler::new);
        register(LotteryBummerMessageHandler::new);
        register(LotteryC4MessageHandler::new);
        register(DrinkPicker::new);
        register(MealPicker::new);
        register(PizzaPicker::new);
        register(FortuneTeller::new);
        register(PlayDice::new);
        register(Goodbye::new);
        register(AntiOverwatch::new);
        register(AntiDirtyWord::new);
        register(Greeting::new);
        register(HeroLinesSelector::new);
        register(FunctTemporary::new);
        register(DisclTemporary::new);
        register(LovelyImage::getINSTANCE);
        register(FurryGamesRandom::new);
        register(FurryGamesSearch::new);
        register(FeedBack::new);
        register(NewFunct::new);
    }

    /**
     * ????????????????????????????????????
     *
     * @return ????????????????????????????????????????????????
     */
    public String optimizeHandlerSequence(boolean reset) {
        LOCK.lock();
        try {
            handlers.sort(new BoxedHandlerRearrangeComparator());
            StringBuilder builder = new StringBuilder("Newly optimized sequence: \n");
            for (BoxedHandler handler : handlers) {
                builder.append(handler.getName()).append(":").append(handler.getCount()).append("\n");
                if (reset) handler.resetCount();
            }
            return builder.toString();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * ????????????????????????Mirai??????????????????????????????Mirai??????????????????
     */
    public void close() {
        for (BoxedHandler handler : handlers) {
            handler.close();
        }
    }

    /**
     * ??????UUID?????????????????????????????????
     *
     * @return ????????????UUID????????????????????????????????????????????????????????????Optional
     */
    public Optional<String> getName(UUID uuid) {
        LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getUUID().equals(uuid))
                    return Optional.of(handler.getName());
            }
            return Optional.empty();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * ???????????????????????????????????????UUID
     *
     * @return ?????????????????????????????????????????????????????????????????????UUID???Optional
     */
    public Optional<UUID> getUUID(String name) {
        LOCK.lock();
        try {
            for (BoxedHandler handler : handlers) {
                if (handler.getName().equals(name))
                    return Optional.of(handler.getUUID());
            }
            return Optional.empty();
        } finally {
            LOCK.unlock();
        }
    }

    MessageResponder.MessageType getType(MessageEvent event) throws MessageEventTypeException {
        if (event instanceof GroupMessageEvent) return MessageResponder.MessageType.GROUP;
        else if (event instanceof FriendMessageEvent) return MessageResponder.MessageType.FRIEND;
        else if (event instanceof GroupTempMessageEvent) return MessageResponder.MessageType.TEMP;
        else if (event instanceof StrangerMessageEvent) return MessageResponder.MessageType.STRANGER;
        else {
            throw new MessageEventTypeException(event);
        }
    }

    static class BoxedHandler {
        final MessageResponder<MessageEvent> handler;
        final List<MessageResponder.MessageType> types;
        int count;

        BoxedHandler(MessageResponder<MessageEvent> handler) {
            this.handler = handler;
            types = handler.types();
            count = 0;
        }

        UUID getUUID() {
            return handler.getUUID();
        }

        String getName() {
            return handler.getName();
        }

        boolean match(MessageEvent event, MessageResponder.MessageType messageType) {
            String s = event.getMessage().contentToString();
            if (fit(messageType))
                return handler.match(s);
            return false;
        }

        RespondTask handle(MessageEvent event) {
            count++;
            return handler.handle(event);
        }

        boolean fit(MessageResponder.MessageType type) {
            return types.contains(type);
        }

        int getCount() {
            return count;
        }

        void close() {
            handler.onclose();
        }

        void resetCount() {
            count = 0;
        }
    }

    static class BoxedHandlerRearrangeComparator implements Comparator<BoxedHandler> {

        @Override
        public int compare(BoxedHandler o1, BoxedHandler o2) {
            if (o1.getCount() > o2.getCount()) return -1;
            else if (o1.getCount() == o2.getCount()) return 0;
            return 1;
        }
    }

}
