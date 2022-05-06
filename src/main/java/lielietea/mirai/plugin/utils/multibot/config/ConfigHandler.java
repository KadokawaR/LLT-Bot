package lielietea.mirai.plugin.utils.multibot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import lielietea.mirai.plugin.utils.multibot.BotConfigList;
import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotJoinGroupEvent;
import net.mamoe.mirai.event.events.BotLeaveEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.*;
import java.util.Map;

public class ConfigHandler {

    static BotConfigList readRecord(){ return MultiBotHandler.readRecord(); }

    static void writeRecord(){ MultiBotHandler.writeRecord(); }

    public static Config getConfig(Bot bot){
        return MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(bot.getId()));
    }

    public static Config getConfig(long botID){
        return MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(botID));
    }

    public static Config getConfig(MessageEvent event){
        return MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId()));
    }

    static void getCurrentBotConfig(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.equalsIgnoreCase("/config")) return;
        MessageChainBuilder mcb = new MessageChainBuilder();
        mcb.append("addFriend: ").append(String.valueOf(getConfig(event).getRc().isAddFriend())).append("\n");
        mcb.append("addGroup: ").append(String.valueOf(getConfig(event).getRc().isAddGroup())).append("\n");
        mcb.append("answerFriend: ").append(String.valueOf(getConfig(event).getRc().isAnswerFriend())).append("\n");
        mcb.append("answerGroup: ").append(String.valueOf(getConfig(event).getRc().isAnswerGroup())).append("\n");
        mcb.append("autoAnswer: ").append(String.valueOf(getConfig(event).getRc().isAutoAnswer())).append("\n");
        event.getSubject().sendMessage(mcb.asMessageChain());
    }

    static void changeCurrentBotConfig(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(message.equalsIgnoreCase("/config -h")) {
            event.getSubject().sendMessage("使用/config+空格+数字序号+空格+true/false来开关配置。\n\n1:addFriend\n2:addGroup\n3:answerFriend\n4:answerGroup\n5:autoAnswer");
        }
        if(message.contains("/config")&&(message.contains("true")||message.contains("false"))){
            String[] messageSplit = message.split(" ");
            if(messageSplit.length!=3){
                event.getSubject().sendMessage("/config指示器使用错误。");
                return;
            }
            if(Boolean.parseBoolean(messageSplit[2]) &&!messageSplit[2].contains("r")){
                event.getSubject().sendMessage("Boolean设置错误。");
                return;
            }
            if(!Boolean.parseBoolean(messageSplit[2]) &&!messageSplit[2].contains("a")){
                event.getSubject().sendMessage("Boolean设置错误。");
                return;
            }


            switch(messageSplit[1]){
                case "1":{
                    MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId())).setAddFriend(Boolean.parseBoolean(messageSplit[2]));
                    event.getSubject().sendMessage("已设置acceptFriend为"+Boolean.parseBoolean(messageSplit[2]));
                    break;
                }
                case "2":{
                    MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId())).setAddGroup(Boolean.parseBoolean(messageSplit[2]));
                    event.getSubject().sendMessage("已设置acceptGroup为"+Boolean.parseBoolean(messageSplit[2]));
                    break;
                }
                case "3":{
                    MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId())).setAnswerFriend(Boolean.parseBoolean(messageSplit[2]));
                    event.getSubject().sendMessage("已设置answerFriend为"+Boolean.parseBoolean(messageSplit[2]));
                    break;
                }
                case "4":{
                    MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId())).setAnswerGroup(Boolean.parseBoolean(messageSplit[2]));
                    event.getSubject().sendMessage("已设置answerGroup为"+Boolean.parseBoolean(messageSplit[2]));
                    break;
                }
                case "5":{
                    MultiBotHandler.getINSTANCE().botConfigList.getBotConfigs().get(MultiBotHandler.BotName.get(event.getBot().getId())).setAutoAnswer(Boolean.parseBoolean(messageSplit[2]));
                    event.getSubject().sendMessage("已设置sendNotice为"+Boolean.parseBoolean(messageSplit[2]));
                    break;
                }
            }
            MultiBotHandler.writeRecord();
        }
    }

    public static boolean canAddFriend(Bot bot){
        return getConfig(bot).getRc().isAddFriend();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canAddGroup(Bot bot){ return getConfig(bot).getRc().isAddGroup(); }

    public static boolean canAnswerFriend(Bot bot){
        return getConfig(bot).getRc().isAnswerFriend();
    }

    public static boolean canAnswerGroup(Bot bot){
        return getConfig(bot).getRc().isAnswerGroup();
    }

    public static boolean canAutoAnswer(Bot bot){
        return getConfig(bot).getRc().isAutoAnswer();
    }

    static void reset(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(message.equalsIgnoreCase("/reset config")){
            MultiBotHandler.getINSTANCE().botConfigList=MultiBotHandler.readRecord();
            event.getSubject().sendMessage("已经重置 Config 配置文件。");
        }

    }

    public static void react(MessageEvent event){
        String message = event.getMessage().contentToString();
        getCurrentBotConfig(event,message);
        changeCurrentBotConfig(event,message);
        reset(event,message);
    }

}
