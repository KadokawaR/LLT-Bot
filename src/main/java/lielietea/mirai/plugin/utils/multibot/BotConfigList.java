package lielietea.mirai.plugin.utils.multibot;

import lielietea.mirai.plugin.utils.multibot.config.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class BotConfigList {
    Map<Config, MultiBotHandler.BotName> botConfigs;
    BotConfigList(){
        botConfigs = new HashMap<>();
    }
}
