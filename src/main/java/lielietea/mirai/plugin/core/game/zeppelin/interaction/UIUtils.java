package lielietea.mirai.plugin.core.game.zeppelin.interaction;

import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UIUtils<U> {
    public static boolean containsBannedWords(String content){
        List<String> bannedWords = Arrays.asList("SB","FUCK","IDIOT","JIBA","BITCH","CAO","DICK","PUSSY","ASS","CHINA","CPC","PRC","SHIT");
        for(String word:bannedWords){
            if(content.contains(word)) return true;
        }
        return false;
    }

    public static String getGroupHomePort(long groupID){
        Random random = new Random(groupID);
        int loc = random.nextInt(CityInfoUtils.getINSTANCE().cityInfoList.size());
        return CityInfoUtils.getINSTANCE().cityInfoList.get(loc).code;
    }

    public static String randomAircraftName(){
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

    public static String deleteKeywords(String originalString,List<String> keywords){
        originalString = originalString.toUpperCase().replace(" ","");
        for(String str:keywords){
            originalString=originalString.replace(str.toUpperCase(),"");
        }
        return originalString;
    }

}
