package lielietea.mirai.plugin.core.secretfunction;

import lielietea.mirai.plugin.core.secretfunction.antiwithdraw.AntiWithdraw;
import lielietea.mirai.plugin.core.secretfunction.repeater.Repeater;
import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public class SecretFunctionHandler {

    public static void go(GroupMessageEvent event){
        if(SecretFunctionDatabase.getINSTANCE().secretFunctionData.canDoAntiWithdraw(event)) AntiWithdraw.save(event);
        if(SecretFunctionDatabase.getINSTANCE().secretFunctionData.canDoRepeater(event)) Repeater.handle(event);
        changeStatus(event);
    }

    //todo:是否有远程激活的必要？
    static void changeStatus(GroupMessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        String message = event.getMessage().contentToString();
        boolean hasChanged = false;

        if(message.equalsIgnoreCase("/open antiwithdraw")||message.equals("打开防撤回")){
            SecretFunctionDatabase.getINSTANCE().secretFunctionData.antiWithdraw.add(event.getGroup().getId());
            event.getSubject().sendMessage("已开启防撤回。");
            hasChanged = true;
        }

        if(message.equalsIgnoreCase("/close antiwithdraw")||message.equals("关闭防撤回")){
            if(SecretFunctionDatabase.getINSTANCE().secretFunctionData.antiWithdraw.contains(event.getGroup().getId())) {
                SecretFunctionDatabase.getINSTANCE().secretFunctionData.antiWithdraw.remove(event.getGroup().getId());
                event.getSubject().sendMessage("已关闭防撤回。");
                hasChanged = true;
            } else {
                event.getSubject().sendMessage("该群未开启防撤回。");
            }
        }

        if(message.equalsIgnoreCase("/open repeater")||message.equals("打开复读")){
            SecretFunctionDatabase.getINSTANCE().secretFunctionData.repeater.add(event.getGroup().getId());
            event.getSubject().sendMessage("已开启复读。");
            hasChanged = true;
        }

        if(message.equalsIgnoreCase("/close repeater")||message.equals("关闭复读")){
            if(SecretFunctionDatabase.getINSTANCE().secretFunctionData.antiWithdraw.contains(event.getGroup().getId())) {
                SecretFunctionDatabase.getINSTANCE().secretFunctionData.antiWithdraw.remove(event.getGroup().getId());
                event.getSubject().sendMessage("已关闭复读。");
                hasChanged = true;
            } else {
                event.getSubject().sendMessage("该群未开启复读。");
            }
        }

        if(hasChanged){
            SecretFunctionDatabase.writeRecord();
            SecretFunctionDatabase.readRecord();
        }

    }
}
