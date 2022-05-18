package lielietea.mirai.plugin.utils.activation.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.utils.ContactUtil;
import lielietea.mirai.plugin.utils.StandardTimeUtil;
import lielietea.mirai.plugin.utils.activation.data.ActivationData;
import lielietea.mirai.plugin.utils.activation.data.GroupEntryRecord;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivationDatabase {

    private static final String ACTIVATION_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Chitung" + File.separator +"Activation.json";

    private static final long LONGEST_TIME_GAP = StandardTimeUtil.getPeriodLengthInMS(3,0,0,0);

    ActivationDatabase(){}

    private static final ActivationDatabase INSTANCE;

    static {
        INSTANCE = new ActivationDatabase();
        initialize();
    }

    private ActivationData data;

    private static ActivationDatabase getINSTANCE() {
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
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean containsUser(long userID,Bot bot){
        return getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.contains(userID);
    }

    public static boolean isActivated(long groupID,Bot bot){
        return getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.contains(groupID);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void addUser(long userID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.add(userID);
        writeRecord();
        readRecord();
    }

    public static void deleteUser(long userID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.remove(userID);
        writeRecord();
        readRecord();
    }

    public static void addUsers(List<Long> userIDs, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.addAll(userIDs);
        writeRecord();
        readRecord();
    }

    public static void deleteUsers(List<Long> userIDs, Bot bot){
        for(long userID:userIDs){
            getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.remove(userID);
        }
        writeRecord();
        readRecord();
    }

    public static void addGroup(long groupID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.add(groupID);
        writeRecord();
        readRecord();
    }

    public static void deleteGroup(long groupID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.remove(groupID);
        writeRecord();
        readRecord();
    }

    public static void addGroups(List<Long> groupIDs, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.addAll(groupIDs);
        writeRecord();
        readRecord();
    }

    public static void deleteGroups(List<Long> groupIDs, Bot bot){
        for(long groupID:groupIDs){
            getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.remove(groupID);
        }
        writeRecord();
        readRecord();
    }

    public static void addRecord(long groupID, long invitorID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.add(new GroupEntryRecord(groupID,invitorID,bot));
        writeRecord();
        readRecord();
    }

    public static void deleteRecord(long groupID, Bot bot){
        List<GroupEntryRecord> removalList = new ArrayList<>();
        for(GroupEntryRecord ger:getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords){
            if(ger.groupID==groupID) removalList.add(ger);
        }
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.removeAll(removalList);
        writeRecord();
        readRecord();
    }

    public static void deleteRecordWithoutSave(long groupID, Bot bot){
        List<GroupEntryRecord> removalList = new ArrayList<>();
        for(GroupEntryRecord ger:getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords){
            if(ger.groupID==groupID) removalList.add(ger);
        }
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.removeAll(removalList);
    }

    public static void addRecordWithoutSave(long groupID, long invitorID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.add(new GroupEntryRecord(groupID,invitorID,bot));
    }

    public static void addRecords(List<GroupEntryRecord> records, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.addAll(records);
        writeRecord();
        readRecord();
    }

    public static void deleteRecordsWithoutSave(List<GroupEntryRecord> records, Bot bot){
        List<GroupEntryRecord> removalList = new ArrayList<>();
        for(GroupEntryRecord ger:getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords){
            for(GroupEntryRecord inputger:records) {
                if (ger.equalsRecord(inputger)) removalList.add(ger);
            }
        }
        getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.removeAll(removalList);
    }

    public static void combinedActivationOperation(long groupID, long userID, Bot bot){
        getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs.add(groupID);
        getINSTANCE().data.getBotsData(bot).botActivationData.authorizedUsers.remove(userID);
        writeRecord();
        readRecord();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void updateActivationDataGroupList(Bot bot, List<Long> currentGroupIDs){

        List<Long> removalList = new ArrayList<>();

        for(long listedGroupID:getINSTANCE().data.getBotsData(bot).botActivationData.activatedGroupIDs){
            if(!currentGroupIDs.contains(listedGroupID)) removalList.add(listedGroupID);
        }

        deleteGroups(removalList,bot);

    }

    public static void updateEntryRecordList(Bot bot, List<Long> currentGroupIDs){

        //有入群记录但是却不在的群

        List<GroupEntryRecord> removalList = new ArrayList<>();

        for(GroupEntryRecord ger:getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords){
            if(!currentGroupIDs.contains(ger.groupID)) removalList.add(ger);
            if(isActivated(ger.groupID,bot)) removalList.add(ger);
        }

        deleteRecordsWithoutSave(removalList,bot);

        //没有入群记录但是在的群

        try {

            for (long groupID : currentGroupIDs) {
                if (!getINSTANCE().data.getBotsData(bot).botActivationData.recordContainsGroup(groupID)) {
                    addRecordWithoutSave(groupID, 0L, bot);
                    Group group = bot.getGroup(groupID);

                    if (group != null) {
                        MessageChainBuilder mcb = new MessageChainBuilder();
                        mcb.append("您好，由于七筒可能是在离线期间被拉入本群，故未收到有关激活的事宜。")
                                .append(new At(group.getOwner().getId()))
                                .append("目前七筒还没有激活，请任意群成员添加公众聊天群 932617537 并按照提示激活。七筒在被激活前不会响应任何消息。");
                        group.sendMessage(mcb.asMessageChain());

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        writeRecord();
        readRecord();

    }

    public static void quitOutOfDateGroups(Bot bot){

        long currentTime = new Date().getTime();
        List<GroupEntryRecord> copiedList = new ArrayList<>(getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords);

        for(GroupEntryRecord ger:copiedList) {
            if((currentTime- ger.date) >= LONGEST_TIME_GAP){

                ContactUtil.tryQuitGroup(ger.groupID,bot);

                getINSTANCE().data.getBotsData(bot).botActivationData.groupEntryRecords.remove(ger);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(ger.invitorID!=0L) ContactUtil.tryDeleteFriend(ger.invitorID,bot);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
