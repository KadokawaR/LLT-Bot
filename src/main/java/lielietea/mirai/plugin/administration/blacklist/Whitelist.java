package lielietea.mirai.plugin.administration.blacklist;

import lielietea.mirai.plugin.utils.IdentityUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Whitelist {

    static String WhiteLIST_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Chitung" + File.separator + "Whitelist.json";

    Whitelist(){}

    private static final Whitelist INSTANCE;

    static class WhiteListClass{
        List<Long> friendWhitelist;
        List<Long> groupWhitelist;
        WhiteListClass(){
            this.friendWhitelist = new ArrayList<>();
            this.groupWhitelist = new ArrayList<>();
        }
    }

    static {
        INSTANCE = new Whitelist();
        initialize();
    }

    public WhiteListClass WhiteListClass;

    public static Whitelist getINSTANCE() {
        return INSTANCE;
    }

    static void initialize(){
        getINSTANCE().WhiteListClass = new WhiteListClass();
        if(Touch.file(WhiteLIST_PATH)){
            try {
                getINSTANCE().WhiteListClass = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(WhiteLIST_PATH)), StandardCharsets.UTF_8))), WhiteListClass.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeRecord();
        }
    }

    static WhiteListClass readRecord(){
        try {
            return new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(WhiteLIST_PATH)), StandardCharsets.UTF_8))), WhiteListClass.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().WhiteListClass);
        Write.cover(jsonString, WhiteLIST_PATH);
    }

    public enum TrustKind{
        Friend,
        Group
    }

    static void addTrust(long ID,TrustKind bk){
        switch(bk){
            case Friend:
                getINSTANCE().WhiteListClass.friendWhitelist.add(ID);
                writeRecord();
                getINSTANCE().WhiteListClass=readRecord();
                break;
            case Group:
                getINSTANCE().WhiteListClass.groupWhitelist.add(ID);
                writeRecord();
                getINSTANCE().WhiteListClass=readRecord();
        }
    }

    static void deleteTrust(long ID,TrustKind bk){
        switch(bk){
            case Friend:
                getINSTANCE().WhiteListClass.friendWhitelist.remove(ID);
                writeRecord();
                getINSTANCE().WhiteListClass=readRecord();
                break;
            case Group:
                getINSTANCE().WhiteListClass.groupWhitelist.remove(ID);
                writeRecord();
                getINSTANCE().WhiteListClass=readRecord();
        }
    }

    static void trustGroupInGroup(GroupMessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(message.equalsIgnoreCase("/trust")) {
            addTrust(event.getGroup().getId(),TrustKind.Group);
            return;
        }
        if(message.equalsIgnoreCase("/untrust")){
            deleteTrust(event.getGroup().getId(),TrustKind.Group);
        }
    }

    static void trust(MessageEvent event, String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.toLowerCase().contains("/trust ")&&!message.toLowerCase().contains("/trust-")) return;
        String rawString = message.toLowerCase().replace("/trust","").replace(" ","").replace("-","");
        String strID = Pattern.compile("[^0-9]").matcher(rawString).replaceAll(" ").trim();

        Long ID=null;
        TrustKind bk=null;

        if(rawString.contains("g")){
            bk = TrustKind.Group;
        }

        if(rawString.contains("f")){
            bk = TrustKind.Friend;
        }

        if(bk==null||(rawString.contains("g")&&(rawString.contains("f")))){
            event.getSubject().sendMessage("???????????????????????????????????????");
            return;
        }

        try{
            ID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(ID==null||ID<10000){
            event.getSubject().sendMessage("???????????????????????????????????????");
            return;
        }

        if(bk.equals(TrustKind.Friend)&&IdentityUtil.isAdmin(ID)){
            event.getSubject().sendMessage("????????????????????????");
            return;
        }

        addTrust(ID,bk);

        switch(bk){
            case Friend:
                event.getSubject().sendMessage("????????????????????????"+ID);
                break;
            case Group:
                event.getSubject().sendMessage("????????????????????????"+ID);
                break;
        }

    }

    static void unTrust(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!message.toLowerCase().contains("/untrust ")&&!message.toLowerCase().contains("/untrust-")) return;
        String rawString = message.toLowerCase().replace("/untrust","").replace(" ","").replace("-","");
        String strID = Pattern.compile("[^0-9]").matcher(rawString).replaceAll(" ").trim();

        Long ID=null;
        TrustKind bk=null;

        if(rawString.contains("g")){
            bk = TrustKind.Group;
        }

        if(rawString.contains("f")){
            bk = TrustKind.Friend;
        }

        if(bk==null||(rawString.contains("g")&&(rawString.contains("f")))){
            event.getSubject().sendMessage("???????????????????????????????????????");
            return;
        }

        try{
            ID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(ID==null||ID<10000){
            event.getSubject().sendMessage("???????????????????????????????????????");
            return;
        }

        if(!isTrusted(ID,bk)){
            switch(bk){
                case Friend:
                    event.getSubject().sendMessage("??????"+ID+"???????????????????????????");
                    break;
                case Group:
                    event.getSubject().sendMessage("??????"+ID+"???????????????????????????");
                    break;
            }
            return;
        }

        deleteTrust(ID,bk);

        switch(bk){
            case Friend:
                event.getSubject().sendMessage("????????????"+ID+"??????????????????");
                break;
            case Group:
                event.getSubject().sendMessage("????????????"+ID+"??????????????????");
                break;
        }
    }

    public static boolean isTrusted(long ID,TrustKind bk){

        if(IdentityUtil.isAdmin(ID)) return false;

        switch(bk){
            case Group:
                return getINSTANCE().WhiteListClass.groupWhitelist.contains(ID);
            case Friend:
                return getINSTANCE().WhiteListClass.friendWhitelist.contains(ID);
        }
        return false;

    }

    public static void operation(MessageEvent event){
        String message = event.getMessage().contentToString();
        if(event instanceof GroupMessageEvent){
            trustGroupInGroup((GroupMessageEvent) event,message);
        }
        trust(event,message);
        unTrust(event,message);
    }

    public void ini(){
        System.out.println("Initialize Whitelist");
    }
}
