package lielietea.mirai.plugin.core.game.zeppelin.map;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.data.CityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CityInfoUtils {

    public List<CityInfo> cityInfoList;

    CityInfoUtils(){
        cityInfoList = new ArrayList<>();
        cityInfoList.addAll(ini());
    }

    static class cityList{
        List<CityInfo> cityInfoList;
    }

    static final CityInfoUtils INSTANCE = new CityInfoUtils();
    public static CityInfoUtils getINSTANCE(){ return INSTANCE;}

    public static List<CityInfo> ini(){
        Gson gson = new Gson();
        String PATH = "/zeppelin/CityCoords.json";
        InputStream is = CityInfoUtils.class.getResourceAsStream(PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<CityInfo> res = gson.fromJson(br, cityList.class).cityInfoList;
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Coordinate getCityCoords(String code){
        for(CityInfo ci: getINSTANCE().cityInfoList){
            if(ci.code.equals(code)) return ci.coordinate;
        }
        return null;
    }

    public static Coordinate getRandomCoords(){
        Random random = new Random();
        int r = random.nextInt(getINSTANCE().cityInfoList.size());
        return getINSTANCE().cityInfoList.get(r).coordinate;
    }

    public static String getCityNameCN(String code){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(ci.code.equals(code)) return ci.nameCN;
        }
        return null;
    }

    public static String getCityNameCN(Coordinate coord){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(ci.coordinate.equals(coord)) return ci.nameCN;
        }
        return null;
    }

    public static String getCityCode(String nameCN){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(ci.nameCN.equals(nameCN)) return ci.code;
        }
        return null;
    }

    public static String getCityCode(Coordinate coord){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(Coordinate.equals(ci.coordinate,coord)) return ci.code;
        }
        return null;
    }

    public static boolean isInCity(Coordinate coord){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(Coordinate.equals(ci.coordinate,coord)) return true;
        }
        return false;
    }

    public static double distance(Coordinate coord1,Coordinate coord2){
        return Math.sqrt(Math.pow(coord1.x-coord2.x,2)+Math.pow(coord1.y-coord2.y,2));
    }

    public static boolean isInCityProtection(Coordinate coord){
        for(CityInfo ci: getINSTANCE().cityInfoList){
            if(distance(coord,ci.coordinate)<= Config.CITY_PROTECTION_DISTANCE) return true;
        }
        return false;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean exist(String name){
        name = name.toUpperCase();
        if(name.length() != 3 || !name.matches("[A-Z]+")) return false;
        for(CityInfo ci: getINSTANCE().cityInfoList){
            if(ci.code.equals(name)) return true;
        }
        return false;
    }

    public static boolean isInMapRange(Coordinate coord){
        return coord.x<Config.MAX_X_POSITION&&coord.x>0&&coord.y<Config.MAX_Y_POSITION&&coord.y>0;
    }

}
