package lielietea.mirai.plugin.game.jetpack;

import com.google.gson.Gson;
import lielietea.mirai.plugin.utils.json.JsonFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class BaiduAPI {
    static final String DEV_KEY = "tOebp3inj7gwxhfWiyKykcyp8aeXrQPk";
    static final int ZOOM_LEVEL =13;//百度地图图片默认缩放等级，数值越高图片显示的区域越精细

    //地理编码Gson转换的类
    public class AddrToCoord{
        int status;
        Result result;
    }

    public class Result{
        Location location;
        int precise;
        int confidence;
        int comprehension;
        String level;
    }

    public static class Location{
        Double lng;
        Double lat;
        Location(Double lng1, Double lat1){
            lng = lng1;
            lat = lat1;
        }
    }

    //地理逆编码Gson转换的类
    public class CoordToAddr{
        int status;
        Result2 result;
    }

    public class Result2{
        Location location;
        String formatted_address;
        String business;
        AddressComponent addressComponent;
        String[] pois;
        String[] roads;
        String[] poiRegions;
        String sematic_description;
        int cityCode;
    }

    public class AddressComponent{
        String country;
        int country_code;
        String country_code_iso;
        String country_code_iso2;
        String province;
        String city;
        int city_level;
        String district;
        String town;
        String town_code;
        String adcode;
        String street;
        String street_number;
        String direction;
        String distance;
    }

    //解析百度地图地理编码返回的json
    public static AddrToCoord getJsonUrlCoord(String urlPath) throws Exception{
        Gson gson = new Gson();
        return gson.fromJson(JsonFile.read(urlPath), AddrToCoord.class);
    }

    //通过地址来返回坐标结果
    public static AddrToCoord getCoord(String address) throws Exception {
        String LOCATION_PATH = "https://api.map.baidu.com/geocoding/v3/?address=" + address + "&output=json&ak=" + DEV_KEY;
        if (JsonFile.read(LOCATION_PATH).contains("无相关结果")) {
            return null;
        }
        return getJsonUrlCoord(LOCATION_PATH);
    }
    //解析百度地图逆编码返回的json
    public static CoordToAddr getJsonUrlAddr(String urlPath) throws Exception{
        Gson gson = new Gson();
        return gson.fromJson(JsonFile.read(urlPath), CoordToAddr.class);
    }

    //通过坐标来返回地址结果
    public static CoordToAddr getAddr(Location location) throws Exception{
        String ADDRESS_PATH = "https://api.map.baidu.com/reverse_geocoding/v3/?ak="+DEV_KEY+"&output=json&coordtype=wgs84ll&location="+String.valueOf(location.lat)+","+String.valueOf(location.lng);
        return getJsonUrlAddr(ADDRESS_PATH);
    }

    //通过坐标和缩放范围返回一张静态的地图图片，默认大小为800*600
    public static BufferedImage getStaticImage(Location location,int zoomLevel) throws Exception{
        String STATIC_IMAGE_PATH = "https://api.map.baidu.com/staticimage/v2?ak="+DEV_KEY+"&mcode=666666&center="+String.valueOf(location.lat)+","+String.valueOf(location.lng)+"&width=800&height=600&zoom="+String.valueOf(zoomLevel);
        return ImageIO.read(JsonFile.getInputStream(STATIC_IMAGE_PATH));
    }

    //将CoordToAddr里面的内容组合成省市镇
    public static String C2AToString(Location location) throws Exception {
        CoordToAddr c2a = getAddr(location);
        String result = "";
        if(c2a.status == 0) {
            if (c2a.result.addressComponent.country != "中国") {
                result = result + c2a.result.addressComponent.country;
            }
            if (c2a.result.addressComponent.city!=c2a.result.addressComponent.province){
                result = result + c2a.result.addressComponent.province;
            }
            result = result + c2a.result.addressComponent.district+c2a.result.addressComponent.town;
            return "七筒目前在"+result+"境内，坐标为："+String.valueOf(location.lng)+","+String.valueOf(location.lat);
        }
        else{
            return "目前暂不清楚七筒的位置。";
        }
    }
}

