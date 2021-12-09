package lielietea.mirai.plugin.core.messagehandler.game.montecarlo.roulette;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.*;

public class RouletteUtils extends IndicatorProcessor{

    static boolean isRoulette(MessageEvent event){
        return (event.getMessage().contentToString().equals("/roulette")||event.getMessage().contentToString().equals("轮盘"));
    }

    static boolean isBet(MessageEvent event){
        return (event.getMessage().contentToString().contains("/bet")||event.getMessage().contentToString().contains("下注"));
    }

    static Integer getBet(String string){
        string = string.replace(" ","");
        string = string.replace("/bet","");
        string = string.replace("下注","");
        Integer res;
        try{res = Integer.parseInt(string);}
        catch (NumberFormatException e){
            e.printStackTrace();
            res=null;
        }
        return res;
    }

    //查看列表里面是不是都是Wrong
    static boolean isAllWrong(List<RouletteBet> betList){
        for(RouletteBet rb: betList){
            if (rb.indicator.equals(Indicator.Wrong)) continue;
            if (rb.status.equals(Status.Cool)) return false;
        }
        return true;
    }

    //查看列表里面有多少个下注
    static int getBetAmount(List<RouletteBet> betList){
        int time=0;
        for(RouletteBet rb: betList){
            if(!rb.indicator.equals(Indicator.Wrong)&&(rb.status.equals(Status.Cool))) time++;
        }
        return time;
    }

    //整理实际的下注
    static List<RouletteBet> getDeFactoBets(List<RouletteBet> betList){
        List<RouletteBet> resBetList = new ArrayList<>();
        for(RouletteBet rb: betList){
            if(!rb.indicator.equals(Indicator.Wrong)&&(rb.status.equals(Status.Cool))) resBetList.add(rb);
        }
        return resBetList;
    }

    //告知下注情况
    static String feedbeckBetStatus(List<RouletteBet> betList){
        if(isAllWrong(betList)) return "未获得有效下注。";
        StringBuilder sb = new StringBuilder();
        sb.append("收到：\n");
        boolean hasUsedWrongIndicator = false;
        for(RouletteBet rb: betList){
            if(rb.indicator.equals(Indicator.Wrong)){
                hasUsedWrongIndicator = true;
                continue;
            }
            if(rb.status.equals(Status.WrongNumber)){
                sb.append(rb.indicator.getName()).append("的数字下注不正确。\n");
                continue;
            }
            if(doesntNeedNumber(rb.indicator)){
                sb.append(rb.indicator.getName()).append("\n");
            } else {
                sb.append("下在").append(rb.location).append("的").append(rb.indicator.getName()).append("\n");
            }
        }
        sb.append("共计").append(getBetAmount(betList)).append("注");
        if(hasUsedWrongIndicator){
            sb.append("\n\n存在指示器使用错误，请仔细阅读说明书。");
        }
        return sb.toString();
    }

    //设置新Table;
    static Table<Long,Integer,Integer> setNewTable(List<Long> playerList){
        Table<Long,Integer,Integer> playersTable = HashBasedTable.create();
        for(Long ID:playerList){
            for(int i=0;i<37;i++){
                playersTable.put(ID,i,0);
            }
        }
        return playersTable;
    }

    //设置FriendSettleAccount里面的新类
    static Map<Integer,Integer> setNewMap(){
        Map<Integer,Integer> playersMap = new HashMap<>();
        for(int i=0;i<37;i++){
            playersMap.put(i,0);
        }
        return playersMap;
    }

    //根据下注往Table里面塞赌注
    static Table<Long,Integer,Integer> updateTable(Table<Long,Integer,Integer> originalTable, List<RouletteBet> rouletteBetList, long ID){
        Table<Long,Integer,Integer> newTable = originalTable;
        for(RouletteBet rb:rouletteBetList){
            Set<Integer> someSet = new HashSet<>();
            switch(rb.indicator){
                case Black: someSet.addAll(RouletteAreas.Black);
                case Red: someSet.addAll(RouletteAreas.Red);
                case Odd: someSet.addAll(RouletteAreas.getOddArea());
                case Even: someSet.addAll(RouletteAreas.getEvenArea());
                case Line: someSet.addAll(RouletteAreas.getLineArea(rb.location));
                case Column: someSet.addAll(RouletteAreas.getColumnArea(rb.location));
                case Four: someSet.addAll(RouletteAreas.getFourArea(rb.location));
                case Six: someSet.addAll(RouletteAreas.getSixArea(rb.location));
                case Part: someSet.addAll(RouletteAreas.getPartArea(rb.location));
                case Half: someSet.addAll(RouletteAreas.getHalfArea(rb.location));
                case Number: someSet.add(rb.location);
            }

            Iterator<Integer> it = someSet.iterator();
            while(it.hasNext()){
                int itNext = it.next();
                int originalValue = newTable.get(ID,itNext);
                newTable.put(ID,it.next(),originalValue+rb.indicator.getTime());
            }
        }
        return newTable;
    }

    //根据下注往Map里面塞赌注
    static Map<Long,Table<Long,Integer,Integer>> updateMap(Map<Long,Table<Long,Integer,Integer>> originalMap, List<RouletteBet> rouletteBetList, long playerID, long groupID){
        Map<Long,Table<Long,Integer,Integer>> newMap = originalMap;
        for(RouletteBet rb:rouletteBetList){
            Set<Integer> someSet = new HashSet<>();
            switch(rb.indicator){
                case Black: someSet.addAll(RouletteAreas.Black);
                case Red: someSet.addAll(RouletteAreas.Red);
                case Odd: someSet.addAll(RouletteAreas.getOddArea());
                case Even: someSet.addAll(RouletteAreas.getEvenArea());
                case Line: someSet.addAll(RouletteAreas.getLineArea(rb.location));
                case Column: someSet.addAll(RouletteAreas.getColumnArea(rb.location));
                case Four: someSet.addAll(RouletteAreas.getFourArea(rb.location));
                case Six: someSet.addAll(RouletteAreas.getSixArea(rb.location));
                case Part: someSet.addAll(RouletteAreas.getPartArea(rb.location));
                case Half: someSet.addAll(RouletteAreas.getHalfArea(rb.location));
                case Number: someSet.add(rb.location);
            }

            Iterator<Integer> it = someSet.iterator();
            while(it.hasNext()){
                int itNext = it.next();
                int originalValue = newMap.get(groupID).get(playerID,itNext);
                newMap.get(groupID).put(playerID,it.next(),originalValue+rb.indicator.getTime());
            }
        }
        return newMap;
    }

}
