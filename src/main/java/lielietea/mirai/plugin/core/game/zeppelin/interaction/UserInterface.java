package lielietea.mirai.plugin.core.game.zeppelin.interaction;

import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.AircraftUtils;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.function.Shop;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Arrays;
import java.util.Objects;


public class UserInterface {

    public static void zeppelinCommand(MessageEvent event){
        Command command = contentToCommand(event);
        if(command == null) return;
        switch(Objects.requireNonNull(command)){
            case RegisterAircraft:

                break;
            case SetAircraftName:

                break;
            case ChangeAircraft:

                break;
            case Shop:

                break;

            case RegisterGuild:

                break;
            case SetGuildName:

                break;
            case JoinGuild:

                break;
            case QuitGuild:

                break;

            case SetHomePort:

                break;
            case SetPirate:

                break;

            case StartTravel:

                break;
            case AbortTravel:

                break;
            case StartStationed:

                break;

            case Instruction:

                break;
            case DailyTask:

                break;
            case ShowMap:

                break;

            case SetTrader:

                break;
        }
    }

    public static Command contentToCommand(MessageEvent event){
        String content = event.getMessage().contentToString().toLowerCase();
        if(content.equals("/register")||content.equals("注册飞艇")) return Command.RegisterAircraft;
        if(content.contains("/setname")||content.contains("设置名称")) return Command.SetAircraftName;
        if(content.equals("/changeship")||content.equals("更换飞艇")) return Command.ChangeAircraft;
        if(content.equals("/shop")||content.equals("飞艇商店")) return Command.Shop;

        if(content.equals("/registerguild")||content.equals("注册公会")||content.equals("注册工会")) return Command.RegisterGuild;
        if(content.contains("/setguildname")||content.contains("设置公会名称")||content.contains("设置工会名称")) return Command.SetGuildName;
        if(content.contains("/joinguild")||content.contains("加入公会")||content.contains("加入工会")) return Command.JoinGuild;
        if(content.equals("/quitguild")||content.equals("退出公会")||content.equals("退出工会")) return Command.QuitGuild;

        if(content.contains("/sethomeport")||content.contains("设置母港")) return Command.SetHomePort;
        if(content.equals("/pirate")||content.equals("成为海盗")) return Command.SetPirate;

        if(content.contains("/starttravel")||content.contains("启动飞艇")) return Command.StartTravel;
        if(content.equals("/aborttravel")||content.equals("停止飞艇")) return Command.AbortTravel;
        if(content.equals("/stationed")||content.equals("驻扎")) return Command.StartStationed;

        if(content.equals("/zeppelin")||content.contains("飞行指南")) return Command.Instruction;
        if(content.equals("/dailytask")||content.equals("每日任务")) return Command.DailyTask;
        if(content.equals("/map")||content.equals("显示地图")) return Command.ShowMap;

        return null;
    }

    public static MessageChainBuilder mcb(MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if(event instanceof GroupMessageEvent) mcb.append(new At(event.getSender().getId())).append(" ");
        return mcb;
    }

    public static void registerAircraft(MessageEvent event){
        if(event instanceof FriendMessageEvent){
            event.getSubject().sendMessage(Notice.REGISTRATION_IN_GROUP);
            return;
        }

        MessageChainBuilder mcb = mcb(event);
        if(Aircraft.exist(event.getSender().getId())){
            mcb.append(Notice.REPEATED_REGISTRATION);
            event.getSubject().sendMessage(mcb.asMessageChain());
            return;
        }

        String randomName = UIUtils.randomAircraftName();
        Aircraft.register(randomName,event.getSender().getId(),UIUtils.getGroupHomePort(event.getSubject().getId()));
        mcb.append("您已经成功注册飞艇").append(randomName).append(",可以输入/setname+空格+7位数字与字母混合名称来更改您的飞艇名称。");
        event.getSubject().sendMessage(mcb.asMessageChain());
    }

    public static void setAircraftName(MessageEvent event){
        MessageChainBuilder mcb = mcb(event);

        if(!Aircraft.exist(event.getSender().getId())){
            event.getSubject().sendMessage(mcb.append(Notice.NOT_REGISTERED).asMessageChain());
            return;
        }

        String content = event.getMessage().contentToString().toUpperCase();
        content = UIUtils.deleteKeywords(content, Arrays.asList("/SETNAME","设置名称"));

        if(!Aircraft.exist(event.getSender().getId())){
            event.getSubject().sendMessage(mcb.append(Notice.NOT_REGISTERED).asMessageChain());
            return;
        }

        if(content.length()==7) {
            if (content.matches("[A-Z0-9]+")){
                if(!UIUtils.containsBannedWords(content)){
                    AircraftInfo ai = Aircraft.get(event.getSender().getId());
                    assert ai != null;
                    ai.setName(content);
                    Aircraft.updateRecord(ai);
                    event.getSubject().sendMessage(mcb.append("已成功设置您的飞艇名称 ").append(content).asMessageChain());
                    return;
                }
            }
        }
        event.getSubject().sendMessage(mcb.append(Notice.WRONG_NAME_CHANGING_FORMAT).asMessageChain());
    }

    public static void changeAircraft(MessageEvent event) {
        event.getSubject().sendMessage(mcb(event).append(Shop.activity(event)).asMessageChain());
    }

    public static void showShop(MessageEvent event){

    }

    public static void registerGuild(MessageEvent event){

    }

    public static void setGuildName(MessageEvent event){

    }

    public static void joinGuild(MessageEvent event){

    }

    public static void quitGuild(MessageEvent event){

    }

    public static void setHomePort(MessageEvent event){

    }

    public static void setPirate(MessageEvent event){
        event.getSubject().sendMessage(mcb(event).append(AircraftUtils.changePirateStatus(true,event.getSender().getId())).asMessageChain());
    }

    public static void setTrader(MessageEvent event){
        event.getSubject().sendMessage(mcb(event).append(AircraftUtils.changePirateStatus(false,event.getSender().getId())).asMessageChain());
    }

    public static void startTravel(MessageEvent event){

    }

    public static void abortTravel(MessageEvent event){

    }

    public static void startStationed(MessageEvent event){

    }

    public static void instruction(MessageEvent event){

    }

    public static void dailyTask(MessageEvent event){

    }

    public static void showMap(MessageEvent event){

    }

}
