package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import net.mamoe.mirai.event.events.MessagePostSendEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessagePostSendEventHandler extends MPSEStatistics{


    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);;
    int groupMessageCount;
    int friendMessageCount;
    int failedMessageCount;
    DataList dataList = new DataList();
    static MainTask mainTask = new MainTask();

    MessagePostSendEventHandler(){}

    final static MessagePostSendEventHandler INSTANCE;

    static{
        INSTANCE = new MessagePostSendEventHandler();
        getINSTANCE().groupMessageCount=0;
        getINSTANCE().friendMessageCount=0;
        getINSTANCE().failedMessageCount=0;
        executor.schedule(mainTask,1,TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(mainTask,5,5,TimeUnit.MINUTES);
    }

    static public MessagePostSendEventHandler getINSTANCE(){return INSTANCE;}

    public static void count(MessagePostSendEvent event){
        if(event.getReceipt() == null) {
            getINSTANCE().failedMessageCount++;
            System.out.println("failedMessageCount++");
            return;
        }

        if (event.getReceipt().isToGroup()){
            getINSTANCE().groupMessageCount++;
            System.out.println("groupMessageCount++");
        } else {
            getINSTANCE().friendMessageCount++;
            System.out.println("friendMessageCount++");
        }
    }

    public static void updateDataList(){
        int groupMessageCount = getINSTANCE().groupMessageCount;
        int friendMessageCount = getINSTANCE().friendMessageCount;
        int failedMessageCount = getINSTANCE().failedMessageCount;

        Date date = new Date();
        Date dateAWeekAgo = updateDaysByGetTime(date,-7);
        Data data = new Data(date,friendMessageCount, groupMessageCount,failedMessageCount);
        getINSTANCE().dataList.addDataIntoDatas(data);
        getINSTANCE().dataList.datas.removeIf(dt -> dt.getDate().before(dateAWeekAgo));

    }

    public static void resetCount(){
        getINSTANCE().friendMessageCount = 0;
        getINSTANCE().groupMessageCount = 0;
        getINSTANCE().failedMessageCount = 0;
    }

    static class MainTask implements Runnable{
        @Override
        public void run() {
            try {
                getINSTANCE().dataList=openData();
                System.out.println("获得opendata");
                updateDataList();
                System.out.println("更新data");
                writeData(getINSTANCE().dataList);
                resetCount();
                System.out.println("MPSE的主任务已经结束。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void handle(MessagePostSendEvent event){
        count(event);
    }


}
