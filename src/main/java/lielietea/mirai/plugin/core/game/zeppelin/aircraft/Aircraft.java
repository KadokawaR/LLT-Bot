package lielietea.mirai.plugin.core.game.zeppelin.aircraft;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Aircraft {
    final static String AIRCRAFT_DIR = System.getProperty("user.dir") + File.separator + "Zeppelin";
    final static String AIRCRAFT_FILE = AIRCRAFT_DIR + File.separator + "Aircraft.json";

    List<AircraftInfo> aircrafts;

    static class aircraftList{
        List<AircraftInfo> aircrafts;
        aircraftList(){
            this.aircrafts = new ArrayList<>();
        }
        aircraftList(List<AircraftInfo> aircrafts){
            this.aircrafts=aircrafts;
        }
    }

    Aircraft(){
        aircrafts = new ArrayList<>();
        if(!new File(AIRCRAFT_DIR).exists()) Touch.dir(AIRCRAFT_DIR);
        if(!new File(AIRCRAFT_FILE).exists()) {
            Touch.file(AIRCRAFT_FILE);
            Write.cover(new Gson().toJson(new aircraftList(), aircraftList.class),AIRCRAFT_FILE);
        }
        InputStream is = Aircraft.class.getResourceAsStream(AIRCRAFT_FILE);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        aircrafts.addAll(new Gson().fromJson(br, aircraftList.class).aircrafts);
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final Aircraft INSTANCE = new Aircraft();
    public static Aircraft getInstance(){ return INSTANCE;}

    public static void register(String name, long playerID, String homePortCode){
        getInstance().aircrafts.add(new AircraftInfo(name,playerID,homePortCode));
    }

    public static AircraftInfo get(long playerID){
        for(AircraftInfo ai : getInstance().aircrafts){
            if(ai.getPlayerID()==playerID) return ai;
        }
        return null;
    }

    public static void readRecord(){
        InputStream is = Aircraft.class.getResourceAsStream(AIRCRAFT_FILE);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        getInstance().aircrafts = new Gson().fromJson(br, aircraftList.class).aircrafts;
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer getIndexCode(long playerID){
        for(int i=0;i<getInstance().aircrafts.size();i++){
            if(getInstance().aircrafts.get(i).getPlayerID()==playerID) return i;
        }
        return null;
    }

    public static void updateRecord(Map<Long, Coordinate> map){
        for(Long playerID:map.keySet()){
            int indexCode = getIndexCode(playerID);
            getInstance().aircrafts.get(indexCode).setCoordinate(map.get(playerID));
        }
    }

    public static void updateRecord(AircraftInfo ai){
        long playerID = ai.getPlayerID();
        int indexCode = getIndexCode(playerID);
        getInstance().aircrafts.get(indexCode).set(ai);
    }

    public static void writeRecord(){
        Write.cover(new Gson().toJson(new aircraftList(getInstance().aircrafts)),AIRCRAFT_FILE);
        readRecord();
    }

    public static boolean exist(long playerID){
        return getIndexCode(playerID)!=null;
    }

    public static boolean isPirate(long playerID){
        return Objects.requireNonNull(get(playerID)).isPirate();
    }

}
