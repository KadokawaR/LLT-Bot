package lielietea.mirai.plugin.utils;

import lielietea.mirai.plugin.administration.blacklist.Blacklist;
import lielietea.mirai.plugin.administration.blacklist.Whitelist;
import lielietea.mirai.plugin.core.game.zeppelin.Zeppelin;
import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import lielietea.mirai.plugin.core.responder.ResponderManager;
import lielietea.mirai.plugin.core.responder.imageresponder.ImageResponder;
import lielietea.mirai.plugin.core.responder.universalrespond.URManager;
import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;

public class InitializeUtil {

    static final String WELCOME_TEXT = "////////////////////////////////////////////////////////////////////////";

    public static void initialize(){
        GroupPolice.getINSTANCE().ini();
        ResponderManager.getINSTANCE().ini();
        GroupConfigManager.getINSTANCE().ini();
        URManager.getINSTANCE().ini();
        Blacklist.getINSTANCE().ini();
        Whitelist.getINSTANCE().ini();
        ImageResponder.getINSTANCE().ini();
        Zeppelin.ini();
        System.out.println(WELCOME_TEXT);
    }
}
