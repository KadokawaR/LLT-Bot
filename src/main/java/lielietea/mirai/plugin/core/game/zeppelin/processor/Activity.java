package lielietea.mirai.plugin.core.game.zeppelin.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Activity {

    final static String ACTIVITY_DIR = System.getProperty("user.dir") + File.separator + "data"  + File.separator + "Zeppelin";
    final static String ACTIVITY_FILE = ACTIVITY_DIR + File.separator + "Activity.json";

    static class activityList{
        List<ActivityInfo> activityInfoList;
        activityList(){
            this.activityInfoList = new ArrayList<>();
        }
        activityList(List<ActivityInfo> activities) {
            this.activityInfoList = activities;
        }
    }

    public List<ActivityInfo> activities;
    static activityList empty = new activityList();

    Activity(){
        activities = new ArrayList<>();
        if(!new File(ACTIVITY_DIR).exists()) Touch.dir(ACTIVITY_DIR);
        if(!new File(ACTIVITY_FILE).exists()) {
            Touch.file(ACTIVITY_FILE);
            Write.cover(new Gson().toJson(empty,activityList.class),ACTIVITY_FILE);
        }
        InputStream is = null;
        try {
            is = new FileInputStream(ACTIVITY_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        activities.addAll(new Gson().fromJson(br,activityList.class).activityInfoList);
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final Activity INSTANCE = new Activity();
    public static Activity getInstance(){ return INSTANCE;}

    public static void readRecord(){
        InputStream is = null;
        try {
            is = new FileInputStream(ACTIVITY_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        getInstance().activities = new Gson().fromJson(br, activityList.class).activityInfoList;
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ActivityInfo get(long playerID){
        for(ActivityInfo ai:getInstance().activities){
            if(ai.getPlayerID()==playerID) return ai;
        }
        return null;
    }

    public static boolean exist(long playerID){
        for(ActivityInfo activityInfo: getInstance().activities){
            if(activityInfo.getPlayerID()==playerID) return true;
        }
        return false;
    }

    public static void delete(long playerID){
        List<ActivityInfo> deleteList = new ArrayList<>();
        for(ActivityInfo activityInfo: getInstance().activities){
            if(activityInfo.getPlayerID()==playerID){
                deleteList.add(activityInfo);
                break;
            }
        }
        getInstance().activities.removeAll(deleteList);
    }

    public static void updateRecord(ActivityInfo activityInfo){
        if(exist(activityInfo.getPlayerID())) {
            long playerID = activityInfo.getPlayerID();
            delete(playerID);
            getInstance().activities.add(activityInfo);
        } else {
            getInstance().activities.add(activityInfo);
        }
        writeRecord();
    }

    public static void writeRecord(){
        System.out.println("Activity writeRecord");
        Gson gs = new GsonBuilder().setPrettyPrinting().create();
        Write.cover(gs.toJson(new activityList(getInstance().activities)),ACTIVITY_FILE);
        readRecord();
    }

    public void ini(){}
}
