package lielietea.mirai.plugin.game.jetpack;

import com.google.gson.Gson;
import lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker.FoodCluster;
import lielietea.mirai.plugin.messageresponder.fursona.Fursona;
import lielietea.mirai.plugin.messageresponder.fursona.FursonaPunk;
import net.mamoe.mirai.event.events.MessageEvent;
import sun.nio.cs.UTF_8;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static java.lang.Math.pow;
import static lielietea.mirai.plugin.game.jetpack.BaiduAPI.ZOOM_LEVEL;
import static lielietea.mirai.plugin.game.jetpack.BaiduAPI.ZOOM_LEVEL_CITY;


public class JetPackUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final String TXT_PATH = System.getProperty("user.dir")+File.separator+"jetpack.txt";

    public static class locationRecord{
        double lng;
        double lat;
        String locationName;
        String departureTime;
        locationRecord(double lng1,double lat1, String ln1, String dt1){
            lng = lng1;
            lat = lat1;
            locationName = ln1;
            departureTime = dt1;
        }

    }

    //根据两点之间的坐标，返回七筒的飞行时间，单位为分钟
    public static double getFlightDuration(BaiduAPI.Location loc1, BaiduAPI.Location loc2) {
        double distance = DistanceCalculator.GetDistance(loc1.lng, loc1.lat, loc2.lng, loc2.lat);
        //分钟与公里的拟合曲线 y=0.515x^0.836
        return 0.515 * pow(distance / 1000, 0.836) / 5;
    }

    //以sdf格式返回预计抵达时间的String
    public static String getEstimatedArrivalTime(BaiduAPI.Location loc1, BaiduAPI.Location loc2, String departureTime) throws ParseException {
        Date date = sdf.parse(departureTime);
        double flightDuration = getFlightDuration(loc1, loc2);
        long time_ms = date.getTime();
        time_ms += flightDuration * 60 * 1000;
        return sdf.format(new Date(time_ms));
    }

    //获得七筒飞行当前的地理坐标
    public static BaiduAPI.Location getCurrentLocation(BaiduAPI.Location loc1, BaiduAPI.Location loc2, String departureTimeStr) throws ParseException {
        Date actualArrivalTime = sdf.parse(getEstimatedArrivalTime(loc1, loc2, departureTimeStr));
        Date departureTime = sdf.parse(departureTimeStr);
        Date now = new Date();

        long actualArrivalTime_ms = actualArrivalTime.getTime();
        long departureTime_ms = departureTime.getTime();
        long now_ms = now.getTime();
        if (now_ms >= actualArrivalTime_ms) {
            return loc2;
        } else {
            assert false;
            Double lat = loc1.lat + (loc2.lat - loc1.lat) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            Double lng = loc1.lng + (loc2.lng - loc1.lng) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            return new BaiduAPI.Location(lng,lat);
        }
    }

    //在文件中追写内容
    public static void writeRecord(String content) {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(TXT_PATH, true));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //将整行String转换为locationRecord
    public static locationRecord strToRecord(String line){
        line = line.replace("\n","");
        String[] record = line.split(",");
        assert false;
        if (record.length==4) {
            return new locationRecord(Double.parseDouble(record[0]),Double.parseDouble(record[1]),record[2],record[3]);
        }
        else{
            return null;
        }
    }

    //读取txt文件返回Map
    public static List<locationRecord> readRecord() throws IOException {
        List<locationRecord> locMap = new ArrayList<>();
        InputStreamReader is = new InputStreamReader(new FileInputStream(TXT_PATH));
        BufferedReader br = new BufferedReader(is);
        String str;
        while(true) {
            str = br.readLine();
            if (str == null) {
                break;
            }
            assert false;
            locMap.add(strToRecord(str));
        }
        return locMap;
    }

    //把LocationRecord按照输出顺序转换成String
    public static String convertLocationRecord(locationRecord lr){
        return String.valueOf(lr.lng)+","+String.valueOf(lr.lat)+","+lr.locationName+","+lr.departureTime+"\n";
    }

    //计算ZoomLevel
    public static int zoomLevelCalculator(BaiduAPI.Location loc1, BaiduAPI.Location loc2){
        double duration = getFlightDuration(loc1,loc2);
        if(duration<=1.25){
            return 16;
        }else if (duration<=2.5){
            return 15;
        }else if (duration<=7.5){
            return 14;
        }else if (duration<=15){
            return 13;
        }else if(duration<=30){
            return 12;
        }else if(duration<=240){
            return (int) (13-duration/30);
        }else{
            return 5;
        }
    }

    static class cityBorders{
        List<cityCoords> borders;
    }

    static class cityCoords{
        String city;
        List<BaiduAPI.Location> coords;
    }

    public static boolean isBetween(BaiduAPI.Location loc1, BaiduAPI.Location loc2, BaiduAPI.Location loc){
        return ((loc1.lat-loc.lat)*(loc2.lat-loc.lat)<0)&&((loc1.lng-loc.lng)*(loc2.lng-loc.lng)<0);
    }

    //判断坐标是否在城市区域内，是的话返回17
    public static int zoomLevelCalculatorS(BaiduAPI.Location loc){
        String CITYCOORDS_PATH = "/cluster/citycoords.json";
        InputStream is = FursonaPunk.class.getResourceAsStream(CITYCOORDS_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        cityBorders cb = gson.fromJson(br, cityBorders.class);
        for(int i=0;i<cb.borders.size();i++){
            if (isBetween(cb.borders.get(i).coords.get(0),cb.borders.get(i).coords.get(1),loc)){
                return ZOOM_LEVEL_CITY;
            }
        }
        return ZOOM_LEVEL;
    }
}