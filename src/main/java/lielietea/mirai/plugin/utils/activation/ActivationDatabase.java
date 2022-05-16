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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    static synchronized void readRecord(){
        try {
            getINSTANCE().data = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ACTIVATION_PATH)), StandardCharsets.UTF_8))), ActivationData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().data);
        Write.cover(jsonString, ACTIVATION_PATH);
        readRecord();
    }

    public static boolean containUser(long userID){
        return getINSTANCE().data.userPermissionList.contains(userID);
    }

    public static boolean isActivated(GroupMessageEvent event){
        return getINSTANCE().data.activatedGroupID.contains(event.getGroup().getId());
    }

    public static boolean isActivated(long groupID){
        return getINSTANCE().data.activatedGroupID.contains(groupID);
    }

    static void addGroup(long groupID){
        if(!getINSTANCE().data.activatedGroupID.contains(groupID)) getINSTANCE().data.activatedGroupID.add(groupID);
    }

    public static void addGroup(List<Long> groupIDList){
        for(long groupID:groupIDList){
            if(!getINSTANCE().data.activatedGroupID.contains(groupID)) getINSTANCE().data.activatedGroupID.add(groupID);
        }
        writeRecord();
    }

    public static void addUser(long userID){
        if(!getINSTANCE().data.userPermissionList.contains(userID)) getINSTANCE().data.userPermissionList.add(userID);
        writeRecord();
    }

    public static void deleteGroup(long groupID){
        getINSTANCE().data.activatedGroupID.remove(groupID);
        writeRecord();
    }

    static void deleteUser(long userID){
        if(userID!=0) getINSTANCE().data.userPermissionList.remove(userID);
    }

    public static void combinedActivationOperation(long groupID, long userID){
        addGroup(groupID);
        deleteUser(userID);
        writeRecord();
    }

    public static void addRecord(long groupID){
        getINSTANCE().data.enterGroupRecords.add(new EnterGroupRecord(groupID));
    }

    public static List<Long> getOutOfDateGroupIDList(){
        List<Long> result = new ArrayList<>();
        List<EnterGroupRecord> recordList = getINSTANCE().data.enterGroupRecords.stream().filter(EnterGroupRecord::outOfDate).collect(Collectors.toList());
        for(EnterGroupRecord egr:recordList){
            result.add(egr.groupID);
        }
        return result;
    }

    public void ini(){}

}
