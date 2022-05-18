package lielietea.mirai.plugin.utils;

import lielietea.mirai.plugin.utils.activation.data.ActivationData;
import lielietea.mirai.plugin.utils.activation.handler.ActivationDatabase;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;

import java.util.HashMap;
import java.util.Map;

public class BotOnlineUtil {

    BotOnlineUtil(){}

    private static final BotOnlineUtil INSTANCE;

    static {
        INSTANCE = new BotOnlineUtil();
        initialize();
    }

    private Map<MultiBotHandler.BotName,Boolean> initialisationRecord;

    private static BotOnlineUtil getINSTANCE() {
        return INSTANCE;
    }

    static void initialize() {
        getINSTANCE().initialisationRecord = new HashMap<>();
        for(MultiBotHandler.BotName botName:MultiBotHandler.BotName.values()){
            getINSTANCE().initialisationRecord.put(botName,false);
        }
    }

    public static boolean hasInitialized(Bot bot){
        return getINSTANCE().initialisationRecord.get(MultiBotHandler.BotName.get(bot));
    }

    public static void setInitialized(Bot bot){
        getINSTANCE().initialisationRecord.remove(MultiBotHandler.BotName.get(bot));
        getINSTANCE().initialisationRecord.put(MultiBotHandler.BotName.get(bot),true);
    }



}
