package lielietea.mirai.plugin.core.game.zeppelin.interaction;

import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UIUtils {
    public static boolean containsBannedWords(String content){
        List<String> bannedWords = Arrays.asList("SB","FUCK","IDIOT","JIBA","BITCH","CAO","DICK","PUSSY","ASS","CHINA","CPC","PRC","SHIT","POLICE");
        for(String word:bannedWords){
            if(content.contains(word)) return true;
        }
        return false;
    }

    public static boolean notInNameList(String name){
        for(AircraftInfo ai: Aircraft.getInstance().aircrafts){
            if(ai.getName().equals(name)) return false;
        }
        return true;
    }

    public static String getGroupHomePort(long groupID){
        Random random = new Random(groupID);
        int loc = random.nextInt(CityInfoUtils.getINSTANCE().cityInfoList.size());
        return CityInfoUtils.getINSTANCE().cityInfoList.get(loc).code;
    }

    public static String generateRandomAircraftName(){
        StringBuilder res = new StringBuilder();
        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i=0;i<7;i++){
            if(random.nextBoolean()){
                int loc = random.nextInt(26);
                res.append(alphabet.charAt(loc));
            } else {
                res.append(random.nextInt(9));
            }
        }
        return res.toString();
    }

    public static String randomAircraftName(){
        String name = generateRandomAircraftName();
        while(Aircraft.getIDFromName(name)!=null){
            name = generateRandomAircraftName();
        }
        return name;
    }

    public static String randomAircraftName(int num){
        StringBuilder res = new StringBuilder();
        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i=0;i<num;i++){
            if(random.nextBoolean()){
                int loc = random.nextInt(26);
                res.append(alphabet.charAt(loc));
            } else {
                res.append(random.nextInt(9));
            }
        }
        return res.toString();
    }

    public static String deleteKeywords(String originalString,List<String> keywords){
        originalString = originalString.toUpperCase().replace(" ","");
        for(String str:keywords){
            originalString=originalString.replace(str.toUpperCase(),"");
        }
        return originalString;
    }

    public static boolean isLegalShipCode(String str){
        str = str.toUpperCase();
        if(str.length()!=7) return false;
        return str.matches("[0-9A-Z]+");
    }

    public static boolean isLegalCityCode(String str){
        str = str.toUpperCase();
        if(str.length()!=3) return false;
        return str.matches("[A-Z]+");
    }

    public enum pirateStartMode{
        ToPlayer,
        ToCity,
        ToCoordinate
    }

    public static pirateStartMode getMode(String str){
        str=str.toUpperCase();
        String[] indicator = str.split(" ");
        if(indicator.length==3&&indicator[1].contains("X")&&indicator[2].contains("Y")) return pirateStartMode.ToCoordinate;
        str = deleteKeywords(str,Arrays.asList("启动飞艇","/starttravel"));
        if(isLegalShipCode(str)) return pirateStartMode.ToPlayer;
        if(isLegalCityCode(str)) return pirateStartMode.ToCity;
        return null;
    }

}
