package lielietea.mirai.plugin.core.game.zeppelin.aircraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static lielietea.mirai.plugin.core.game.zeppelin.TestTools.printAircraftLocation;

public class Aircraft {
    final static String AIRCRAFT_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Zeppelin";
    final static String AIRCRAFT_FILE = AIRCRAFT_DIR + File.separator + "Aircraft.json";

    static class aircraftList {
        List<AircraftInfo> aircraftList;
        aircraftList() {
            this.aircraftList = new ArrayList<>();
        }
        aircraftList(List<AircraftInfo> aircraftList) {
            this.aircraftList = aircraftList;
        }
    }

    public List<AircraftInfo> aircrafts;
    static aircraftList empty = new aircraftList();

    Aircraft() {
        aircrafts = new ArrayList<>();
        if (!new File(AIRCRAFT_DIR).exists()) Touch.dir(AIRCRAFT_DIR);
        if (!new File(AIRCRAFT_FILE).exists()) {
            Touch.file(AIRCRAFT_FILE);
            Write.cover(new Gson().toJson(empty, aircraftList.class), AIRCRAFT_FILE);
        }
        InputStream is = null;
        try {
            is = new FileInputStream(AIRCRAFT_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        aircrafts.addAll(new Gson().fromJson(br, aircraftList.class).aircraftList);
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(AircraftInfo ai:aircrafts){
            System.out.println(ai.getPlayerID()+" "+ai.getCoordinate().x+" "+ai.getCoordinate().y);
        }
    }

    static final Aircraft INSTANCE = new Aircraft();

    public static Aircraft getInstance() {
        return INSTANCE;
    }

    public static void register(String name, long playerID, String homePortCode) {
        getInstance().aircrafts.add(new AircraftInfo(name, playerID, homePortCode));
        writeRecord();
    }

    public static AircraftInfo get(long playerID) {
        for (AircraftInfo ai : getInstance().aircrafts) {
            if (ai.getPlayerID() == playerID) return ai;
        }
        return null;
    }

    public static void readRecord() {
        InputStream is = null;
        try {
            is = new FileInputStream(AIRCRAFT_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        getInstance().aircrafts = new Gson().fromJson(br, aircraftList.class).aircraftList;
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecord(Map<Long, Coordinate> map) {

        if(map.keySet().size()<=0) return;

        List<AircraftInfo> deleteList = new ArrayList<>();
        List<AircraftInfo> addList = new ArrayList<>();
        for (Long playerID : map.keySet()) {
            for(AircraftInfo ai: getInstance().aircrafts) {
                if(ai.getPlayerID()==playerID){
                    deleteList.add(ai);
                    ai.setCoordinate(map.get(playerID));
                    addList.add(ai);
                }
            }
        }

        for(AircraftInfo ai:deleteList){
            getInstance().aircrafts.remove(ai);
        }
        for(AircraftInfo ai:addList){
            getInstance().aircrafts.add(ai);
        }
        writeRecord();
    }

    public static void updateRecord(AircraftInfo aiInput) {
        long playerID = aiInput.getPlayerID();
        if(!exist(playerID)){
            getInstance().aircrafts.add(aiInput);
        } else {
            for (AircraftInfo ai : getInstance().aircrafts) {
                if (ai.getPlayerID() == playerID) {
                    getInstance().aircrafts.remove(ai);
                    getInstance().aircrafts.add(aiInput);
                    break;
                }
            }
        }
        writeRecord();
    }

    public static void writeRecord() {
        System.out.println("========================> Aircraft writeRecord");
        Gson gs = new GsonBuilder().setPrettyPrinting().create();
        aircraftList al = new aircraftList(getInstance().aircrafts);
        printAircraftLocation();
        Write.cover(gs.toJson(al), AIRCRAFT_FILE);
        System.out.println("写入完毕");
        readRecord();
    }

    public static String getNameFromID(long playerID) {
        for (AircraftInfo ai : getInstance().aircrafts) {
            if (ai.getPlayerID() == playerID) return ai.getName();
        }
        return null;
    }

    public static Long getIDFromName(String name) {
        for (AircraftInfo ai : getInstance().aircrafts) {
            if (ai.getName().equals(name)) return ai.getPlayerID();
        }
        return null;
    }

    public static Coordinate getHomePortCoordinate(long playerID) {
        AircraftInfo ai = get(playerID);
        assert ai != null;
        return CityInfoUtils.getCityCoords(ai.getHomePortCode());
    }

    public static boolean exist(long playerID) {
        for(AircraftInfo ai:getInstance().aircrafts){
            if(ai.getPlayerID()==playerID) return true;
        }
        return false;
    }

    public static void delete(long playerID){
        List<AircraftInfo> deleteList = new ArrayList<>();
        for(AircraftInfo ai:getInstance().aircrafts){
            if(ai.getPlayerID()==playerID){
                deleteList.add(ai);
                break;
            }
        }
        getInstance().aircrafts.removeAll(deleteList);
        writeRecord();
    }

    public static ShipKind getShipKind(long playerID) {
        return Objects.requireNonNull(get(playerID)).getShipKind();
    }

    public void ini(){}

}
