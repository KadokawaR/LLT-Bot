package lielietea.mirai.plugin.admintools;

import lielietea.mirai.plugin.admintools.blacklist.BlacklistManager;
import lielietea.mirai.plugin.utils.idchecker.AdministrativeAccountChecker;
import lielietea.mirai.plugin.utils.idchecker.IdentityChecker;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

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

    public void close() {
        executor.shutdown();
    }
}
