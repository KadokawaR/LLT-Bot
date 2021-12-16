package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.utils.MessageUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessagePostSendEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessagePostSendEventHandler extends MPSEStatistics {


    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Table<MultiBotHandler.BotName,MessageKind,Integer> messageCountTable;

    DataList dataList = new DataList();
    Map<Long,Boolean> triggerBreakMap = new HashMap<>();
    static MainTask mainTask = new MainTask();

    MessagePostSendEventHandler() {
    }

    final static MessagePostSendEventHandler INSTANCE;

    static {
        INSTANCE = new MessagePostSendEventHandler();
        getINSTANCE().messageCountTable = HashBasedTable.create();
        for(Bot bot: Bot.getInstances()){
            getINSTANCE().messageCountTable.put(Objects.requireNonNull(MultiBotHandler.BotName.get(bot.getId())), MessageKind.FriendMessage, 0);
            getINSTANCE().messageCountTable.put(Objects.requireNonNull(MultiBotHandler.BotName.get(bot.getId())), MessageKind.GroupMessage, 0);
            getINSTANCE().messageCountTable.put(Objects.requireNonNull(MultiBotHandler.BotName.get(bot.getId())), MessageKind.FailedMessage, 0);
            getINSTANCE().triggerBreakMap.put(bot.getId(),false);
        }
        executor.schedule(mainTask, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(mainTask, 5, 5, TimeUnit.MINUTES);
    }

    static public MessagePostSendEventHandler getINSTANCE() {
        return INSTANCE;
    }

    public static void updateCount(MultiBotHandler.BotName bn, MessageKind mk, int num){
        getINSTANCE().messageCountTable.put(bn,mk,getINSTANCE().messageCountTable.get(bn,mk)+num);
    }

    public static void count(MessagePostSendEvent event) {
        if (event.getReceipt() == null) {
            updateCount(MultiBotHandler.BotName.get(event.getBot().getId()),MessageKind.FailedMessage,1);
            System.out.println("failedMessageCount++");
            MessageChainBuilder mcb = new MessageChainBuilder();
            mcb.append(Objects.requireNonNull(event.getException()).getMessage()).append("\n");
            mcb.append(String.valueOf(event.getTarget().getId()));
            MessageUtil.notifyDevGroup(mcb.toString());
            return;
        }

        if (event.getReceipt().isToGroup()) {
            updateCount(MultiBotHandler.BotName.get(event.getBot().getId()),MessageKind.GroupMessage,1);
            System.out.println("groupMessageCount++");
        } else {
            updateCount(MultiBotHandler.BotName.get(event.getBot().getId()),MessageKind.FriendMessage,1);
            System.out.println("friendMessageCount++");
        }
    }

    public static void updateDataList(long botID) {
        int groupMessageCount = getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID),MessageKind.GroupMessage);
        int friendMessageCount = getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID),MessageKind.FriendMessage);
        int failedMessageCount = getINSTANCE().messageCountTable.get(MultiBotHandler.BotName.get(botID),MessageKind.FailedMessage);

        Date date = new Date();
        Date dateAWeekAgo = updateDaysByGetTime(date, -7);
        Data data = new Data(date, friendMessageCount, groupMessageCount, failedMessageCount, MultiBotHandler.BotName.get(botID));
        getINSTANCE().dataList.addDataIntoDatas(data);
        getINSTANCE().dataList.datas.removeIf(dt -> dt.getDate().before(dateAWeekAgo));

    }

    public static void resetCount() {
        for(MultiBotHandler.BotName bn: MultiBotHandler.BotName.values()){
            getINSTANCE().messageCountTable.put(bn,MessageKind.FriendMessage,0);
            getINSTANCE().messageCountTable.put(bn,MessageKind.GroupMessage,0);
            getINSTANCE().messageCountTable.put(bn,MessageKind.FailedMessage,0);
        }
    }

    static class MainTask implements Runnable {
        @Override
        public void run() {
            try {
                getINSTANCE().dataList = openData();
                System.out.println("获得opendata");
                for(Bot bot : Bot.getInstances()){
                    updateDataList(bot.getId());
                    getINSTANCE().triggerBreakMap.put(bot.getId(),triggeredBreaker(bot.getId()));
                    if(triggeredBreaker(bot.getId())){
                        MessageUtil.notifyDevGroup("已触发消息熔断机制。",bot.getId());
                    }
                }
                System.out.println("更新data");
                writeData(getINSTANCE().dataList);
                resetCount();
                System.out.println("MPSE的主任务已经结束。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handle(MessagePostSendEvent event) {
        count(event);
    }

    public enum MessageKind{
        FriendMessage,
        GroupMessage,
        FailedMessage
    }

    public static boolean botHasTriggeredBreak(GroupMessageEvent event){
        return getINSTANCE().triggerBreakMap.get(event.getBot().getId());
    }

}
