package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import net.mamoe.mirai.event.events.MessagePostSendEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessagePostSendEventHandler extends MPSEStatistics{

    final static MessagePostSendEventHandler INSTANCE = new MessagePostSendEventHandler();
    ScheduledExecutorService executor;
    int groupMessageCount;
    int FriendMessageCount;
    int failedMessageCount;
    DataList dataList;
    MainTask mainTask;

    MessagePostSendEventHandler(){
        this.groupMessageCount=0;
        this.FriendMessageCount=0;
        this.failedMessageCount=0;
        this.executor = Executors.newScheduledThreadPool(1);
        this.dataList = new DataList();
        mainTask = new MainTask();
        this.executor.scheduleAtFixedRate(getINSTANCE().mainTask,0,5,TimeUnit.MINUTES);
    }

    static public MessagePostSendEventHandler getINSTANCE(){return INSTANCE;}

    public static void count(MessagePostSendEvent event){
        if(event.getReceipt() == null) {
            getINSTANCE().failedMessageCount++;
            return;
        }

        if (event.getReceipt().isToGroup()){
            getINSTANCE().groupMessageCount++;
        } else {
            getINSTANCE().failedMessageCount++;
        }
    }

    static class MainTask implements Runnable{
        @Override
        public void run() {
            try {
                getINSTANCE().dataList=openData();
                updateDataList();
                writeData(getINSTANCE().dataList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void handle(MessagePostSendEvent event){
        count(event);
    }


}
