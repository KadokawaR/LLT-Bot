package lielietea.mirai.plugin.game.jetpack;

import lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker.FoodCluster;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.pow;


public class JetPackUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final String LOCATION_RECORD = "/jetpackrecord/jetpackrecord.txt";

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
        return 0.515 * pow(distance / 1000, 0.836);
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
            BaiduAPI.Location loc_now=null;
            assert false;
            loc_now.lat = loc1.lat + (loc2.lat - loc1.lat) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            loc_now.lng = loc1.lng + (loc2.lng - loc1.lng) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            return loc_now;
        }
    }

    //在文件中追写内容
    public static void writeRecord(String content) {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(LOCATION_RECORD, true));
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
        InputStream is = FoodCluster.class.getResourceAsStream(LOCATION_RECORD);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
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
}