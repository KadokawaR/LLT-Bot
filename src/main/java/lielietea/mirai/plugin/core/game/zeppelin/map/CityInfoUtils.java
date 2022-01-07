package lielietea.mirai.plugin.core.game.zeppelin.map;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.game.zeppelin.data.CityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    public static String getCityNameCN(String code){
        for(CityInfo ci:getINSTANCE().cityInfoList){
            if(ci.code.equals(code)) return ci.nameCN;
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
}
