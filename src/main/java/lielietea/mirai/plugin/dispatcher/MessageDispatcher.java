package lielietea.mirai.plugin.dispatcher;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageDispatcher {
    final Map<Long,Integer> groupMessageThreshold = new HashMap<>();
    final Map<Long,Integer> personalMessageThreshold = new HashMap<>();
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();
    final Timer thresholdReset = new Timer(true);

    MessageDispatcher(){
        thresholdReset.schedule(new TimerTask() {
            @Override
            public void run() {
                writeLock.lock();
                try{
                    groupMessageThreshold.clear();
                    personalMessageThreshold.clear();
                }finally{
                    writeLock.unlock();
                }
            }
        },60000,60 * 1000);
    }

    MessageDispatcher INSTANCE = new MessageDispatcher();

    public MessageDispatcher getINSTANCE() {
        return INSTANCE;
    }

    public void handleMessage(MessageEvent event){
        //TODO:替换statisticController中theshhold的功能

    }
}
