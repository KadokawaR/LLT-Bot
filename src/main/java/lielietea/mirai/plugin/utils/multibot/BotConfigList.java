package lielietea.mirai.plugin.utils.multibot;



import lielietea.mirai.plugin.utils.multibot.config.Config;

import java.util.HashMap;
import java.util.Map;

public class BotConfigList {
    Map<MultiBotHandler.BotName, Config> botConfigs;
    BotConfigList(){
        botConfigs = new HashMap<>();
    }

    public Map<MultiBotHandler.BotName, Config> getBotConfigs() {
        return botConfigs;
    }

    public void setBotConfigs(Map<MultiBotHandler.BotName, Config> botConfigs) {
        this.botConfigs = botConfigs;
    }
}
