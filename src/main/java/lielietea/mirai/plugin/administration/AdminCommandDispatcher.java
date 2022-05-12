package lielietea.mirai.plugin.administration;

import lielietea.mirai.plugin.NotificationSetting;
import lielietea.mirai.plugin.administration.blacklist.Whitelist;
import lielietea.mirai.plugin.administration.statistics.GameCenterCount;
import lielietea.mirai.plugin.administration.statistics.MPSEHandler.MessagePostSendEventHandler;
import lielietea.mirai.plugin.administration.blacklist.Blacklist;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminCommandDispatcher {
    static final AdminCommandDispatcher INSTANCE = new AdminCommandDispatcher();
    final ExecutorService executor;


    public AdminCommandDispatcher() {
        this.executor = Executors.newCachedThreadPool();
    }

    public static AdminCommandDispatcher getInstance() {
        return INSTANCE;
    }


    public void handleMessage(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        AdminTools.getINSTANCE().handleAdminCommand(event);
        //MPSE 消息统计
        MessagePostSendEventHandler.getMPSEStatistics(event);
        MessagePostSendEventHandler.checkBreaker(event);
        //GameCenter统计
        GameCenterCount.getStatistics(event);
        //黑名单
        Blacklist.operation(event);
        Whitelist.operation(event);
        //设置管理
        ConfigHandler.react(event);
        //管理帮助
        AdminHelp.send(event);
        //通知管理
        NotificationSetting.change(event);

    }

    public void close() {
        executor.shutdown();
    }
}
