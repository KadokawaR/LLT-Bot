package lielietea.mirai.plugin.game.jetpack;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static java.lang.Math.pow;


public class JetPackUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String RECORD_PATH = System.getProperty("user.dir") + "/jetpackrecord.txt";
    public static final String DEFAULT_LOCATION = "121.48315050378567,31.238178524853188,上海市人民广场,2021-07-01 00:00:00,2021-07-01 00:00:00\n";
    
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
            BaiduAPI.Location loc_now = null;
            loc_now.lat = loc1.lat + (loc2.lat - loc1.lat) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            loc_now.lng = loc1.lng + (loc2.lng - loc1.lng) * (now_ms - departureTime_ms) / (actualArrivalTime_ms - departureTime_ms);
            return loc_now;
        }
    }

    //在文件中追写内容
    public static void writeRecord(String filePath, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filePath, true));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //将整行String转换为locationRecord
    public static locationRecord strToRecord(String line){
        String[] record = line.split(",");
        locationRecord lr = null;
        lr.lng = Double.parseDouble(record[0]);
        lr.lat = Double.parseDouble(record[1]);
        lr.locationName=record[2];
        lr.departureTime=record[3];
        return lr;
    }


    //读取txt文件返回Map
    public static Map<Integer,locationRecord> readRecord() throws IOException {
        File file = new File(RECORD_PATH);
        Map<Integer,locationRecord> locMap = null;
        //判断txt是否存在
        if (!file.exists()) {
            try {
                file.createNewFile();
                writeRecord(RECORD_PATH, DEFAULT_LOCATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ArrayList<String> records = new ArrayList<>();
            InputStreamReader isr = new InputStreamReader(new FileInputStream(RECORD_PATH), "gbk");
            BufferedReader br = new BufferedReader(isr);
            String str;
            int count = 0;
            for (; ; ) {
                try {
                    str = br.readLine();
                    if (str == null) {
                        break;
                    }
                    count+=1;
                    locMap.put(count,strToRecord(str));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return locMap;
    }

    //根据输入的地址设置完整的LocationRecord
    public static locationRecord setLocationRecord(String locationStr) throws Exception {
        BaiduAPI.Location loc2 = BaiduAPI.getCoord(locationStr).result.location;
        String departureTime = sdf.format(new Date());
        locationRecord lr = readRecord().get(readRecord().size());
        BaiduAPI.Location loc1 = null;
        loc1.lat = lr.lat;
        loc1.lng = lr.lng;
        String actualArrivalTime = getEstimatedArrivalTime(loc1,loc2,departureTime);
        return new locationRecord(lr.lng,lr.lat,locationStr,departureTime);
    }

    //把LocationRecord按照输出顺序转换成String
    public static String convertLocationRecord(locationRecord lr){
        return String.valueOf(lr.lng)+","+String.valueOf(lr.lat)+","+lr.locationName+","+lr.departureTime;
    }
}
