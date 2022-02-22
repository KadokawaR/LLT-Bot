package lielietea.mirai.plugin.core.game.zeppelin.interaction;

import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.AircraftUtils;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.ShipKind;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.function.Shop;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.core.game.zeppelin.map.MapGenerator;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;
import lielietea.mirai.plugin.core.game.zeppelin.processor.ActivityUtils;
import lielietea.mirai.plugin.core.game.zeppelin.processor.GoodsGenerator;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Objects;


public class UserInterface {

    public static void zeppelinCommand(MessageEvent event) {
        Command command = contentToCommand(event);
        if (command == null) return;
        //除了注册以外，其他的内容都理应提前注册过
        if (!Arrays.asList(Command.Instruction, Command.RegisterAircraft, Command.Shop, Command.ShowMap, Command.DailyTask).contains(command)) {
            if (!Aircraft.exist(event.getSender().getId())) {
                event.getSubject().sendMessage(mcb(event).append(Notice.NOT_REGISTERED).asMessageChain());
            }
        }
        switch (Objects.requireNonNull(command)) {
            case RegisterAircraft:
                registerAircraft(event);
                break;
            case SetAircraftName:
                setAircraftName(event);
                break;
            case ChangeAircraft:
                changeAircraft(event);
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
                setHomePort(event);
                break;
            case SetPirate:
                setPirate(event);
                break;
            case StartTravel:
                startTravel(event);
                break;
            case AbortTravel:
                abortTravel(event);
                break;
            case StartStationed:
                startStationed(event);
                break;
            case Instruction:

                break;
            case DailyTask:

                break;
            case ShowMap:
                showMap(event);
                break;

            case SetTrader:
                setTrader(event);
                break;

            case GoHome:
                goHome(event);
                break;

            case Location:
                getLocation(event);
                break;
            case CheckShip:

                break;
            case DeleteShip:
                deleteShip(event);
                break;
            case Statistics:

                break;
        }
    }

    static Command contentToCommand(MessageEvent event) {
        String content = event.getMessage().contentToString().toLowerCase();
        if (content.equals("/register") || content.equals("注册飞艇")) return Command.RegisterAircraft;
        if (content.contains("/setname") || content.contains("设置名称")) return Command.SetAircraftName;
        if (content.equals("/changeship") || content.equals("更换飞艇")) return Command.ChangeAircraft;
        if (content.equals("/shop") || content.equals("飞艇商店")) return Command.Shop;

        if (content.equals("/registerguild") || content.equals("注册公会") || content.equals("注册工会")) return Command.RegisterGuild;
        if (content.contains("/setguildname") || content.contains("设置公会名称") || content.contains("设置工会名称")) return Command.SetGuildName;
        if (content.contains("/joinguild") || content.contains("加入公会") || content.contains("加入工会")) return Command.JoinGuild;
        if (content.equals("/quitguild") || content.equals("退出公会") || content.equals("退出工会")) return Command.QuitGuild;

        if (content.contains("/sethomeport") || content.contains("设置母港")) return Command.SetHomePort;
        if (content.equals("/pirate") || content.equals("成为海盗")) return Command.SetPirate;
        if (content.equals("/trader") || content.equals("成为商人")) return Command.SetTrader;

        if (content.contains("/starttravel") || content.contains("启动飞艇")) return Command.StartTravel;
        if (content.equals("/aborttravel") || content.equals("停止飞艇")) return Command.AbortTravel;
        if (content.equals("/stationed") || content.equals("驻扎飞艇")) return Command.StartStationed;
        if (content.equals("/gohome") || content.equals("返回母港")) return Command.GoHome;

        if (content.equals("/zeppelin") || content.equals("飞行指南") || content.equals("飞行帮助") || content.equals("空艇指南")) return Command.Instruction;
        if (content.equals("/dailytask") || content.equals("每日任务")) return Command.DailyTask;
        if (content.equals("/map") || content.equals("显示地图")) return Command.ShowMap;

        if (content.equals("/mylocation") || content.equals("我的位置") || content.equals("飞艇位置")) return Command.Location;
        if (content.equals("/deleteship") || content.equals("删除飞艇") ) return Command.DeleteShip;

        return null;
    }

    static MessageChainBuilder mcb(MessageEvent event) {
        MessageChainBuilder mcb = new MessageChainBuilder();
        if (event instanceof GroupMessageEvent) mcb.append(new At(event.getSender().getId())).append(" ");
        return mcb;
    }

    static void registerAircraft(MessageEvent event) {
        if (event instanceof FriendMessageEvent) {
            event.getSubject().sendMessage(Notice.REGISTRATION_IN_GROUP);
            return;
        }

        MessageChainBuilder mcb = mcb(event);
        if (Aircraft.exist(event.getSender().getId())) {
            mcb.append(Notice.REPEATED_REGISTRATION);
            event.getSubject().sendMessage(mcb.asMessageChain());
            return;
        }

        String randomName = UIUtils.randomAircraftName();
        Aircraft.register(randomName, event.getSender().getId(), UIUtils.getGroupHomePort(event.getSubject().getId()));
        mcb.append("您已经成功注册飞艇").append(randomName).append(",可以输入/setname+空格+7位数字与字母混合名称来更改您的飞艇名称。");
        event.getSubject().sendMessage(mcb.asMessageChain());
    }

    static void setAircraftName(MessageEvent event) {
        MessageChainBuilder mcb = mcb(event);

        String content = event.getMessage().contentToString().toUpperCase();
        content = UIUtils.deleteKeywords(content, Arrays.asList("/SETNAME", "设置名称"));

        if (!UIUtils.notInNameList(content)) {
            event.getSubject().sendMessage(mcb.append("该名称已经被使用，请尝试其他名称。").asMessageChain());
            return;
        }

        if (content.length() == 7) {
            if (content.matches("[A-Z0-9]+")) {
                if (!UIUtils.containsBannedWords(content)) {
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

    static void changeAircraft(MessageEvent event) {
        event.getSubject().sendMessage(mcb(event).append(Shop.activity(event)).asMessageChain());
    }

    static void showShop(MessageEvent event) {

    }

    static void registerGuild(MessageEvent event) {

    }

    static void setGuildName(MessageEvent event) {

    }

    static void joinGuild(MessageEvent event) {

    }

    static void quitGuild(MessageEvent event) {

    }

    static void setHomePort(MessageEvent event) {
        String indicator = UIUtils.deleteKeywords(event.getMessage().contentToString(), Arrays.asList("/SETHOMEPORT", "/SETHOME", "设置母港"));
        if (!UIUtils.isLegalCityCode(indicator) || !CityInfoUtils.exist(indicator)) {
            event.getSubject().sendMessage(mcb(event).append(Notice.WRONG_CITY_CODE).asMessageChain());
            return;
        }
        event.getSubject().sendMessage(mcb(event).append(AircraftUtils.changeHomePort(indicator, event.getSender().getId())).asMessageChain());
    }

    static void setPirate(MessageEvent event) {
        event.getSubject().sendMessage(mcb(event).append(AircraftUtils.changePirateStatus(ShipKind.Pirate, event)).asMessageChain());
    }

    static void setTrader(MessageEvent event) {
        event.getSubject().sendMessage(mcb(event).append(AircraftUtils.changePirateStatus(ShipKind.NormalShip, event)).asMessageChain());
    }

    static void startTravel(MessageEvent event) {
        MessageChainBuilder mcb = mcb(event);
        if (Activity.exist(event.getSender().getId())) {
            event.getSubject().sendMessage(mcb.append(Notice.IS_IN_ACTIVITY).asMessageChain());
            return;
        }

        AircraftInfo ai = Aircraft.get(event.getSender().getId());

        assert ai != null;
        if (ai.getShipKind() == ShipKind.Pirate) {
            String indicator = event.getMessage().contentToString().toUpperCase();
            UIUtils.pirateStartMode psm = UIUtils.getMode(indicator);
            if (psm == null) {
                event.getSubject().sendMessage(mcb(event).append(Notice.WRONG_DESTINATION_INDICATOR).asMessageChain());
                return;
            }
            switch (psm) {
                case ToCity:
                    indicator = UIUtils.deleteKeywords(event.getMessage().contentToString(), Arrays.asList("启动飞艇", "/starttravel","/STARTTRAVEL"));

                    if (!CityInfoUtils.exist(indicator)) {
                        event.getSubject().sendMessage(mcb.append(Notice.WRONG_CITY_CODE).asMessageChain());
                        return;
                    }

                    if (CityInfoUtils.isInCity(ai.getCoordinate())) {
                        String currentCity = CityInfoUtils.getCityCode(ai.getCoordinate());
                        if (Objects.equals(currentCity, indicator)) {
                            event.getSubject().sendMessage(mcb.append(Notice.SAME_CITY_WARNING).asMessageChain());
                            return;
                        }
                    }

                    ActivityUtils.startAsTrader(event, "", 0, indicator);
                    event.getSubject().sendMessage(mcb.append("您的飞艇正在前往").append(CityInfoUtils.getCityNameCN(indicator)).append("，该操作消耗燃油费。").asMessageChain());

                    break;

                case ToPlayer:
                    indicator = UIUtils.deleteKeywords(event.getMessage().contentToString(), Arrays.asList("启动飞艇", "/starttravel","/STARTTRAVEL"));

                    if (Aircraft.getIDFromName(indicator) == null) {
                        event.getSubject().sendMessage(mcb.append(Notice.SHIP_DOESNT_EXIST).asMessageChain());
                        return;
                    }
                    ActivityUtils.startAsPirateChasingShip(event, indicator);
                    event.getSubject().sendMessage(mcb.append("您的飞艇正在追踪").append(indicator).append("，该操作消耗燃油费。").asMessageChain());

                    break;

                case ToCoordinate:
                    String[] indicatorSplit = indicator.split(" ");
                    Coordinate coordinate = new Coordinate(Integer.parseInt(indicatorSplit[1].replace("X","")), Integer.parseInt(indicatorSplit[2].replace("Y","")));

                    if (!CityInfoUtils.isInMapRange(coordinate)) {
                        event.getSubject().sendMessage(mcb.append(Notice.NOT_IN_MAP_RANGE).asMessageChain());
                        return;
                    }

                    ActivityUtils.startAsTrader(event, "", 0, coordinate);
                    event.getSubject().sendMessage(mcb.append("您的飞艇正在前往").append(indicator).append("，该操作消耗燃油费。").asMessageChain());

                    break;

            }
        } else {

            String indicator = UIUtils.deleteKeywords(event.getMessage().contentToString(), Arrays.asList("启动飞艇", "/starttravel","STARTTRAVEL"));

            if (indicator.equals("")) {
                event.getSubject().sendMessage(mcb.append(Notice.EMPTY_INDICATOR).asMessageChain());
                return;
            }

            if (!UIUtils.isLegalCityCode(indicator) || !CityInfoUtils.exist(indicator)) {
                event.getSubject().sendMessage(mcb.append(Notice.WRONG_CITY_CODE).asMessageChain());
                return;
            }

            if (CityInfoUtils.isInCity(ai.getCoordinate())) {
                String currentCity = CityInfoUtils.getCityCode(ai.getCoordinate());
                if (Objects.equals(currentCity, indicator)) {
                    event.getSubject().sendMessage(mcb.append(Notice.SAME_CITY_WARNING).asMessageChain());
                    return;
                }
            }

            String goodsName = GoodsGenerator.name();
            int goodsValue = GoodsGenerator.value(ai.getCoordinate(), CityInfoUtils.getCityCoords(indicator), ai.getPlayerID());

            ActivityUtils.startAsTrader(event, goodsName, goodsValue, indicator);
            try {
                goodsName = URLDecoder.decode(goodsName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            event.getSubject().sendMessage(mcb.append("您的飞艇正在前往").append(CityInfoUtils.getCityNameCN(indicator)).append("\n货物名称：").append(goodsName).append("\n货物价值为").append(String.valueOf(goodsValue)).append("南瓜比索").asMessageChain());

        }

    }

    static void abortTravel(MessageEvent event) {
        if (!Activity.exist(event.getSender().getId())) {
            event.getSubject().sendMessage(mcb(event).append(Notice.IS_NOT_IN_ACTIVITY).asMessageChain());
            return;
        }
        ActivityUtils.abortFlight(event);
        if (Aircraft.getShipKind(event.getSender().getId()) == ShipKind.Pirate) {
            event.getSubject().sendMessage(mcb(event).append("您的飞艇已经停飞。").asMessageChain());
        } else {
            event.getSubject().sendMessage(mcb(event).append("您的飞艇已经不再执飞，将会返回出发地，本次航程无法获得酬劳。").asMessageChain());
        }
    }

    static void startStationed(MessageEvent event) {
        if (Aircraft.getShipKind(event.getSender().getId()) != ShipKind.Pirate) {
            event.getSubject().sendMessage(mcb(event).append(Notice.NOT_PIRATE).asMessageChain());
            return;
        }
        ActivityUtils.startAsPirateStationed(event, Objects.requireNonNull(Aircraft.get(event.getSender().getId())).getCoordinate());
        event.getSubject().sendMessage(mcb(event).append("您的飞艇已经开始驻扎，将会消耗燃油费，如不手动取消则持续2小时。请注意不要驻扎太久时间，否则会耗尽您所有的南瓜比索。").asMessageChain());
    }

    static void goHome(MessageEvent event) {
        event.getSubject().sendMessage(mcb(event).append(ActivityUtils.goHome(event)).asMessageChain());
    }

    static void instruction(MessageEvent event) {

    }

    static void dailyTask(MessageEvent event) {

    }

    static void showMap(MessageEvent event) {
        ImageSender.sendImageFromBufferedImage(event.getSubject(), MapGenerator.draw());
    }

    static void getLocation(MessageEvent event){
        long playerID = event.getSender().getId();

        if(!Aircraft.exist(playerID)){
            event.getSubject().sendMessage(mcb(event).append(Notice.NOT_REGISTERED).asMessageChain());
            return;
        }

        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        Coordinate coordinate = ai.getCoordinate();
        String res = "您的飞艇"+ai.getName()+"在 ";

        if(CityInfoUtils.isInCity(coordinate)){
            res += CityInfoUtils.getCityNameCN(coordinate)+"市内，";
        } else {
            res += "坐标x:"+coordinate.x+" y:"+coordinate.y+"，";
        }

        if(Activity.exist(playerID)){

            ActivityInfo ac = Activity.get(playerID);
            assert ac != null;

            if(ac.getDestination()==null||ac.getTargetPlayerID()!=0){
                res += "目前正在追踪";
                res += Objects.requireNonNull(Aircraft.get(ac.getTargetPlayerID())).getName();
            }

            res += "目前正在前往";

            if(CityInfoUtils.isInCity(ac.getDestination())){
                res += CityInfoUtils.getCityNameCN(coordinate);
            } else {
                res += "坐标x:"+ac.getDestination().x+" y:"+ac.getDestination().y;
            }

        } else {
            res += "没有执行飞行计划。";
        }

        event.getSubject().sendMessage(mcb(event).append(res).asMessageChain());
    }

    static void deleteShip(MessageEvent event){
        long playerID = event.getSender().getId();
        if(!Aircraft.exist(playerID)){
            event.getSubject().sendMessage(mcb(event).append(Notice.NOT_REGISTERED).asMessageChain());
            return;
        }
        if(Activity.exist(playerID)){
            event.getSubject().sendMessage(mcb(event).append(Notice.IS_IN_ACTIVITY).asMessageChain());
            return;
        }
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai != null;
        Coordinate homePort = CityInfoUtils.getCityCoords(ai.getHomePortCode());
        Coordinate current = ai.getCoordinate();
        assert homePort != null;
        if(!current.equals(homePort)){
            event.getSubject().sendMessage(mcb(event).append(Notice.NOT_IN_HOME_PORT).asMessageChain());
            return;
        }

        Aircraft.delete(playerID);

    }

}
