package lielietea.mirai.plugin.utils.activation;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ActivationDatabase {

    static String ACTIVATION_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Chitung" + File.separator +"Activation.json";

    ActivationDatabase(){}

    private static final ActivationDatabase INSTANCE;

    static {
        INSTANCE = new ActivationDatabase();
        initialize();
    }

    ActivationData data;

    static ActivationDatabase getINSTANCE() {
        return INSTANCE;
    }

    public static void initialize(){
        getINSTANCE().data = new ActivationData();
        if(Touch.file(ACTIVATION_PATH)){
            try {
                getINSTANCE().data = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ACTIVATION_PATH)), StandardCharsets.UTF_8))), ActivationData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeRecord();
        }
    }

    static void readRecord(){
        try {
            getINSTANCE().data = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ACTIVATION_PATH)), StandardCharsets.UTF_8))), ActivationData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().data);
        Write.cover(jsonString, ACTIVATION_PATH);
        readRecord();
    }

    public static boolean containUser(long userID){
        for(Long ID:getINSTANCE().data.userPermissionList){
            if(ID.equals(userID)) return true;
        }
        return false;
    }

    public static boolean isActivated(GroupMessageEvent event){
        for(Long ID:getINSTANCE().data.activatedGroupID){
            if(ID.equals(event.getGroup().getId())) return true;
        }
        return false;
    }

    public static boolean isActivated(long groupID){
        for(Long ID:getINSTANCE().data.activatedGroupID){
            if(ID.equals(groupID)) return true;
        }
        return false;
    }

    public static void addGroup(long groupID){
        if(!getINSTANCE().data.activatedGroupID.contains(groupID)) getINSTANCE().data.activatedGroupID.add(groupID);
    }

    public static void addUser(long userID){
        if(!getINSTANCE().data.activatedGroupID.contains(userID)) getINSTANCE().data.activatedGroupID.add(userID);
    }

    public static void deleteGroup(long groupID){
        getINSTANCE().data.activatedGroupID.remove(groupID);
    }

    public static void deleteUser(long userID){
        getINSTANCE().data.userPermissionList.remove(userID);
    }

    public static void addRecord(long groupID, long userID){
        getINSTANCE().data.recordList.add(new ActivationRecord(groupID,userID));
    }

    public void ini(){}

}
