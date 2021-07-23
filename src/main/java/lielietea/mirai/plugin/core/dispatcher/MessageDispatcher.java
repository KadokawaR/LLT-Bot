package lielietea.mirai.plugin.core.dispatcher;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.MessageHandler;
import lielietea.mirai.plugin.core.messagehandler.feedback.FeedBack;
import lielietea.mirai.plugin.core.messagehandler.responder.ResponderManager;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    final ExecutorService executor;

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
        },10000,60 * 1000);
        this.executor = Executors.newCachedThreadPool();
    }

    static MessageDispatcher INSTANCE = new MessageDispatcher();

    static public MessageDispatcher getINSTANCE() {
        return INSTANCE;
    }

    public void handleMessage(MessageEvent event){
        boolean handled = false;

        //最先交由ResponderManager处理
        Optional<ResponderManager.BoxedHandler> boxedHandler = ResponderManager.getINSTANCE().match(event);
        if(boxedHandler.isPresent()){
            if(!reachLimit(event,boxedHandler.get().getGroupLimit(),boxedHandler.get().getPersonalLimit())){
                handled = true;
                MessageChainPackage temp = ResponderManager.getINSTANCE().handle(event,boxedHandler.get());
                addToThreshold(temp);
                handleMessageChainPackage(temp);
            }
        }

        //然后交由GameManager处理
        //TODO:GameManager还没改写

        //最后交由Feedback处理
        if(!handled){
            if(event instanceof FriendMessageEvent){
                if(FeedBack.getINSTANCE().match((FriendMessageEvent) event)){
                    MessageChainPackage temp = FeedBack.getINSTANCE().handle((FriendMessageEvent) event);
                    addToThreshold(temp);
                    handleMessageChainPackage(temp);
                }
            }
        }
    }

    boolean reachLimit(MessageEvent event, int groupLimit, int personalLimit){
        readLock.lock();
        try{
            boolean f1 = false;
            boolean f2 = false;
            if(event instanceof GroupMessageEvent){
                if(groupMessageThreshold.containsKey(((GroupMessageEvent) event).getGroup().getId())){
                    f1 = groupMessageThreshold.get(((GroupMessageEvent) event).getGroup().getId()) >= groupLimit;
                }
            }
            if(personalMessageThreshold.containsKey(event.getSender().getId())){
                f2 = personalMessageThreshold.get(event.getSender().getId()) >= personalLimit;
            }
            return f1 || f2;
        } finally {
            readLock.unlock();
        }

    }

    void addToThreshold(MessageChainPackage messageChainPackage){
        writeLock.lock();
        try{
            if(messageChainPackage.getSender() instanceof Member){
                long groupID =((Member) messageChainPackage.getSender()).getGroup().getId();
                if(groupMessageThreshold.containsKey(groupID)){
                    groupMessageThreshold.replace(groupID, groupMessageThreshold.get(groupID + 1));
                } else {
                    groupMessageThreshold.put(groupID,1);
                }
            }

            long senderID = messageChainPackage.getSender().getId();
            if(personalMessageThreshold.containsKey(senderID)){
                personalMessageThreshold.replace(senderID, personalMessageThreshold.get(senderID + 1));
            } else {
                personalMessageThreshold.put(senderID,1);
            }
        } finally {
            writeLock.unlock();
        }
    }


    public void handleMessageChainPackage(MessageChainPackage messageChainPackage){
        //首先加告知StatisticController
        //TODO: Add Hook To StatisticController

        //最后加入线程池
        executor.submit(messageChainPackage::execute);
    }

    public void close() {
        executor.shutdown();
    }
}
