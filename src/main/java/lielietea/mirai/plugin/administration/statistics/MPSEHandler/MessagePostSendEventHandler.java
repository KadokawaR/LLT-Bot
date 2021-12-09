package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.utils.MessageUtil;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MessagePostSendEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessagePostSendEventHandler extends MPSEStatistics {


    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    Table<MultiBotHandler.BotName,MessageKind,Integer> messageCountTable;

    DataList dataList = new DataList();
    static MainTask mainTask = new MainTask();

    MessagePostSendEventHandler() {
    }

    final static MessagePostSendEventHandler INSTANCE;

    static {
        INSTANCE = new MessagePostSendEventHandler();
        getINSTANCE().messageCountTable = HashBasedTable.create();
        for(MultiBotHandler.BotName bn: MultiBotHandler.BotName.values()){
            getINSTANCE().messageCountTable.put(bn, MessageKind.FriendMessage, 0);
            getINSTANCE().messageCountTable.put(bn, MessageKind.GroupMessage, 0);
            getINSTANCE().messageCountTable.put(bn, MessageKind.FailedMessage, 0);
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
            mcb.append("getException().getMessage").append(Objects.requireNonNull(event.getException()).getMessage()).append("\n");
            mcb.append("getReceipt().getSource().contentToString()").append(event.getReceipt().getSource().contentToString()).append("\n");
            mcb.append("getMessage().contentToString()").append(event.getMessage().contentToString()).append("\n");
            mcb.append("getTarget().getId()").append(String.valueOf(event.getTarget().getId()));
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
                for(MultiBotHandler.BotName bn: MultiBotHandler.BotName.values()){
                    updateDataList(bn.getValue());
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


}
