package lielietea.mirai.plugin.utils.activation.data;

import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;

public class ActivationDataOfSingleBot {
    public MultiBotHandler.BotName botName;
    public BotActivationData botActivationData;

    ActivationDataOfSingleBot(Bot bot){
        this.botName = MultiBotHandler.BotName.get(bot);
        this.botActivationData = new BotActivationData();
    }
}
