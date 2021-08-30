package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.admintools.blacklist.BlacklistManager;
import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.GroupID;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminCommandDispatcher {
    static final AdminCommandDispatcher INSTANCE = new AdminCommandDispatcher();
    static final IdentityChecker<MessageEvent> administrativeAccountChecker = new AdministrativeAccountChecker();
    final ExecutorService executor;


    public AdminCommandDispatcher() {
        this.executor = Executors.newCachedThreadPool();
    }

    public static AdminCommandDispatcher getInstance() {
        return INSTANCE;
    }


    public void handleMessage(MessageEvent event){
        //TODO 这个部分先临时丢这里，回头再改
        if(event instanceof FriendMessageEvent) AdminTools.getINSTANCE().handleAdminCommand((FriendMessageEvent) event);



        // 黑名单管理器
        if(administrativeAccountChecker.checkIdentity(event)){
            boolean handled = false;
            Optional<Operation> operation = BlacklistManager.getInstance().Handle(event);
            if(operation.isPresent()){
                executor.submit(operation.get()::execute);
                handled = true;
            }
        }

    }

    /**
     * 向开发群发送消息通知
     */
    public void notifyDevGroup(String content){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Group group = bot.getGroup(GroupID.DEV);
            if (group != null) group.sendMessage(content);
        }
    }

    /**
     * 指定某个Bot，向开发群发送消息通知
     */
    public void notifyDevGroup(String content, long botId){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            if(bot.getId() == botId){
                Group group = bot.getGroup(GroupID.DEV);
                if (group != null) group.sendMessage(content);
            }
        }
    }

    /**
     * 尝试退出某个群
     */
    public void tryQuitGroup(long id){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Group group = bot.getGroup(id);
            //  TODO 问题来了，退群要给群内发通知吗?
            if (group != null) group.quit();
        }
    }

    /**
     * 尝试删除好友
     */
    public void tryDeleteFriend(long id){
        List<Bot> bots = Bot.getInstances();
        for (Bot bot : bots) {
            Friend friend = bot.getFriend(id);
            //  TODO 问题来了，删除好友需要告知被删除对象吗？
            if (friend != null) friend.delete();
        }
    }

    public void close() {
        executor.shutdown();
    }
}
