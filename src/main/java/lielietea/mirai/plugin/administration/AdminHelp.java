package lielietea.mirai.plugin.administration;

import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.MessageEvent;

public class AdminHelp {

    static final String ADMIN_HELP = "Bank：\n" +
            "/laundry 空格 金额：为自己增加/减少钱\n" +
            "/set 空格 QQ号 空格 钱：设置用户的钱的数量\n" +
            "/bank 空格 QQ号：查询用户的钱的数量\n\n" +
            "Broadcast:\n" +
            "/broadcast -f 或者 -g：进行好友或者群聊广播\n\n" +
            "Reset：\n" +
            "/reset 空格 ur：重置通用响应的配置文件\n" +
            "/reset 空格 ir：重置通用图库响应的配置文件\n" +
            "/reset 空格 config：重置 Config 配置文件\n\n" +
            "UniversalResponder：\n" +
            "/check 空格 ur：查看 UR 的配置文件\n\n" +
            "Blacklist：\n" +
            "/block 空格 -g 或者 -f 空格 QQ号：屏蔽该号码的群聊或者用户\n" +
            "/unblock 空格 -g 或者 -f 空格 QQ号：解除屏蔽该号码的群聊或者用户\n\n" +
            "Config：\n" +
            "/config：查看 config\n" +
            "/config -h：查看 config 的帮助\n" +
            "/config 空格 数字序号 空格 true/false：开关相应配置\n\n" +
            "Data：\n" +
            "/num -f：查看好友数量\n" +
            "/num -g：查看群聊数量\n" +
            "/coverage：查看总覆盖人数\n\n" +
            "MessagePostSendEvent:\n" +
            "/break：查看熔断状况\n" +
            "/mpse：查看 MPSE 数据\n\n"+
            "GameCenter:\n" +
            "/gamecenter：查看游戏情况\n\n"+
            "Activation:\n" +
            "/align -g：**慎重使用** 授权所有七筒在的群权限\n"+
            "/permit @用户 或者 空格 用户账号：授权该用户进行一次激活\n"+
            "/activate 空格 群号：激活该群聊\n"+
            "/deactivate 空格 群号：取消激活该群聊，会连带删除邀请者好友\n"+
            "/reset 空格 activation：手动重新刷新一次激活列表\n\n"+
            "Notification:\n"+
            ".open 或者 .close 空格 mpse / gamecenter / sequence / notification";


    //todo 补充MPSE GameCenter WhiteList
    public static void send(MessageEvent event){
        if(!IdentityUtil.isAdmin(event.getSender().getId())) return;
        if(event.getMessage().contentToString().equalsIgnoreCase("/adminhelp")) {
            event.getSubject().sendMessage(ADMIN_HELP);
        }
    }
}
