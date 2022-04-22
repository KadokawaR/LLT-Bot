package lielietea.mirai.plugin.core.secretfunction.antiwithdraw;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionData;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionDatabase;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AntiWithdraw {

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static{
        executor.scheduleAtFixedRate(new Update(), 1, 1, TimeUnit.MINUTES);
    }

    static Table<Long,Integer,MessageChain> dataInTwoMinutes = HashBasedTable.create();

    public static void handle(MessageRecallEvent.GroupRecall event){

        if(SecretFunctionDatabase.getINSTANCE().secretFunctionData.canDoAntiWithdraw(event.getGroup().getId())) {
            MessageChain mc = dataInTwoMinutes.get(event.getGroup().getId(), event.getMessageTime());
            if (mc != null) {
                event.getGroup().sendMessage(mc);
            }
        }

    }

    public static void save(GroupMessageEvent event){
        dataInTwoMinutes.put(event.getGroup().getId(), event.getTime(),event.getMessage());
    }

    static class Update implements Runnable{

        @Override
        public void run(){

            int now = Math.toIntExact(Calendar.getInstance().getTimeInMillis() / 1000);
            Table<Long,Integer,MessageChain> clonedTable = HashBasedTable.create();
            clonedTable.putAll(dataInTwoMinutes);

            for(Long groupID:clonedTable.rowKeySet()){
                for(Integer time:dataInTwoMinutes.columnKeySet()){
                    if(now-time>120) dataInTwoMinutes.remove(groupID,time);
                }
            }
        }
    }

}
