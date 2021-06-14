package lielietea.mirai.plugin.messagerepeater;

import lielietea.mirai.plugin.utils.idchecker.BotChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Repeater {
    final Lock lock;
    String content;
    int count;


    public Repeater() {
        this.lock = new ReentrantLock(true);
        this.content = "";
        this.count = 0;
    }

    //处理消息，并根据情况进行复读
    public void handleMessage(GroupMessageEvent event){
        lock.lock();
        try{
            if(content.equals(event.getMessage().contentToString())){
                count+=1;
            } else {
                count=0;
                content = event.getMessage().contentToString();
            }

            if (count==2){
                event.getSubject().sendMessage(content);
                count=0;
            }
        }finally{
            lock.unlock();
        }
    }
}
