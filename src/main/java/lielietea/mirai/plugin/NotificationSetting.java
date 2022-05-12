package lielietea.mirai.plugin;

import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.MessageEvent;

public class NotificationSetting {

    public static boolean GameCenterNotification;
    public static boolean MPSENotification;
    public static boolean NewlyOptimizedSequenceNotification;

    static{
        GameCenterNotification=false;
        MPSENotification=false;
        NewlyOptimizedSequenceNotification=false;
    }

    //有一天懒癌犯了
    public static void change(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        String message = event.getMessage().contentToString();

        if(message.equalsIgnoreCase(".open gamecenter")){
            GameCenterNotification=true;
            return;
        }

        if(message.equalsIgnoreCase(".open mpse")){
            MPSENotification=true;
            return;
        }

        if(message.equalsIgnoreCase(".open sequence")){
            NewlyOptimizedSequenceNotification=true;
            return;
        }

        if(message.equalsIgnoreCase(".close gamecenter")){
            GameCenterNotification=false;
            return;
        }

        if(message.equalsIgnoreCase(".close mpse")){
            MPSENotification=false;
            return;
        }

        if(message.equalsIgnoreCase(".close sequence")){
            NewlyOptimizedSequenceNotification=false;
            return;
        }

        if(message.equalsIgnoreCase(".open notification")){
            GameCenterNotification=true;
            MPSENotification=true;
            NewlyOptimizedSequenceNotification=true;
            return;
        }

        if(message.equalsIgnoreCase(".close notification")){
            GameCenterNotification=false;
            MPSENotification=false;
            NewlyOptimizedSequenceNotification=false;
            return;
        }
    }

}
