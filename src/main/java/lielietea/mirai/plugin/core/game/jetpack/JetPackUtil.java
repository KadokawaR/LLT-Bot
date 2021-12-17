package lielietea.mirai.plugin.core.game.jetpack;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.pow;


public class JetPackUtil {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final String TXT_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "jetpack.txt";

    public static class locationRecord {
        final double lng;
        final double lat;
        final String locationName;
        final String departureTime;

        locationRecord(double lng1, double lat1, String ln1, String dt1) {
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
    public static String getEstimatedArrivalTime(BaiduAPI.Location loc1, BaiduAPI.Location loc2, String departureTime){
        Date date = null;
        try {
            date = sdf.parse(departureTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double flightDuration = getFlightDuration(loc1, loc2);
        assert date != null;
        long time_ms = date.getTime();
        time_ms += flightDuration * 60 * 1000;
        return sdf.format(new Date(time_ms));
    }

    //获得七筒飞行当前的地理坐标
    public static BaiduAPI.Location getCurrentLocation(BaiduAPI.Location loc1, BaiduAPI.Location loc2, String departureTimeStr){
        Date actualArrivalTime = null;
        Date departureTime = null;
        try {
            actualArrivalTime = sdf.parse(getEstimatedArrivalTime(loc1, loc2, departureTimeStr));
            departureTime = sdf.parse(departureTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
            return new BaiduAPI.Location(lng, lat);
        }
    }

    //将整行String转换为locationRecord
    public static locationRecord strToRecord(String line) {
        line = line.replace("\n", "");
        String[] record = line.split(",");
        assert false;
        if (record.length == 4) {
            return new locationRecord(Double.parseDouble(record[0]), Double.parseDouble(record[1]), record[2], record[3]);
        } else {
            return null;
        }
    }

    //读取txt文件返回Map
    public static List<locationRecord> readRecord(){
        List<locationRecord> locMap = new ArrayList<>();
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(new FileInputStream(TXT_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader br = new BufferedReader(is);
        String str = null;
        while (true) {
            try {
                str = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (str == null) {
                break;
            }
            assert false;
            locMap.add(strToRecord(str));
        }
        return locMap;
    }

    //把LocationRecord按照输出顺序转换成String
    public static String convertLocationRecord(locationRecord lr) {
        return lr.lng + "," + lr.lat + "," + lr.locationName + "," + lr.departureTime + "\n";
    }

    //计算ZoomLevel
    public static int zoomLevelCalculator(BaiduAPI.Location loc1, BaiduAPI.Location loc2) {
        double duration = getFlightDuration(loc1, loc2);
        if (duration <= 1.25) {
            return 16;
        } else if (duration <= 2.5) {
            return 15;
        } else if (duration <= 7.5) {
            return 14;
        } else if (duration <= 10) {
            return 13;
        } else if (duration <= 15) {
            return 12;
        } else if (duration <= 20) {
            return 11;
        } else if (duration <= 30) {
            return 10;
        } else if (duration <= 150) {
            return (int) (10 - duration / 30);
        } else {
            return 5;
        }
    }

    static class cityBorders {
        List<cityCoords> borders;
    }

    static class cityCoords {
        String city;
        List<BaiduAPI.Location> coords;
    }

    public static boolean isBetween(BaiduAPI.Location loc1, BaiduAPI.Location loc2, BaiduAPI.Location loc) {
        return ((loc1.lat - loc.lat) * (loc2.lat - loc.lat) < 0) && ((loc1.lng - loc.lng) * (loc2.lng - loc.lng) < 0);
    }

    //判断坐标是否在城市区域内，是的话返回17
    public static int zoomLevelCalculatorS(BaiduAPI.Location loc) {
        String CITYCOORDS_PATH = "/cluster/citycoords.json";
        InputStream is = JetPackUtil.class.getResourceAsStream(CITYCOORDS_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        cityBorders cb = gson.fromJson(br, cityBorders.class);
        for (int i = 0; i < cb.borders.size(); i++) {
            if (isBetween(cb.borders.get(i).coords.get(0), cb.borders.get(i).coords.get(1), loc)) {
                return BaiduAPI.ZOOM_LEVEL_CITY;
            }
        }
        return BaiduAPI.ZOOM_LEVEL;
    }
}