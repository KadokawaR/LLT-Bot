package lielietea.mirai.plugin.core.game.zeppelin.processor;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity {

    final static String ACTIVITY_DIR = System.getProperty("user.dir") + File.separator + "Zeppelin";
    final static String ACTIVITY_FILE = ACTIVITY_DIR + File.separator + "Activity.json";

    List<ActivityInfo> activities;

    static class activityList{
        List<ActivityInfo> activityInfoList;
        activityList(){
            this.activityInfoList = new ArrayList<>();
        }

        public activityList(List<ActivityInfo> activities) {
            this.activityInfoList = activities;
        }
    }

    Activity(){
        activities = new ArrayList<>();
        if(!new File(ACTIVITY_DIR).exists()) Touch.dir(ACTIVITY_DIR);
        if(!new File(ACTIVITY_FILE).exists()) {
            Touch.file(ACTIVITY_FILE);
            Write.cover(new Gson().toJson(new activityList(),activityList.class),ACTIVITY_FILE);
        }
        InputStream is = Activity.class.getResourceAsStream(ACTIVITY_FILE);
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
        InputStream is = Aircraft.class.getResourceAsStream(ACTIVITY_FILE);
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

    public static Integer getIndexCode(long playerID){
        for(int i =0; i<getInstance().activities.size();i++){
            if(getInstance().activities.get(i).getPlayerID()==playerID) return i;
        }
        return null;
    }

    public static boolean isInActivity(long playerID){
        return getIndexCode(playerID) != null;
    }

    public static void eliminateActivity(long playerID){
        getInstance().activities.remove(Objects.requireNonNull(getIndexCode(playerID)).intValue());
    }

    public static void updateRecord(ActivityInfo activityInfo){
        long playerID = activityInfo.getPlayerID();
        if (!isInActivity(playerID)) return;
        int originalIndex = getIndexCode(playerID);
        eliminateActivity(playerID);
        getInstance().activities.add(originalIndex, activityInfo);
    }

    public static void writeRecord(){
        Write.cover(new Gson().toJson(new activityList(getInstance().activities)),ACTIVITY_FILE);
        readRecord();
    }

}
