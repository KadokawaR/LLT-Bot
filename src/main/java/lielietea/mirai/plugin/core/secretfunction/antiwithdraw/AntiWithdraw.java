package lielietea.mirai.plugin.core.secretfunction.antiwithdraw;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionData;
import lielietea.mirai.plugin.core.secretfunction.SecretFunctionDatabase;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.message.data.*;

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
                MessageChainBuilder mcb = new MessageChainBuilder();
                mcb.append(new At(event.getAuthorId())).append(" 刚刚撤回了：").append(mc);
                event.getGroup().sendMessage(mcb.asMessageChain());
            }
        }

    }

    private static boolean isValidMessage(MessageChain chain) {
        if (chain.contains(QuoteReply.Key)) {
            return false;
        }
        for (Message message : chain) {
            if (!(message instanceof MessageSource || message instanceof PlainText || message instanceof At || message instanceof Image))
                return false;
        }
        return true;
    }

    public static void save(GroupMessageEvent event){
        if(isValidMessage(event.getMessage())) {
            dataInTwoMinutes.put(event.getGroup().getId(), event.getTime(), event.getMessage());
        }
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
