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

    static void TrustGroupInGroup(GroupMessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if(event.getMessage().contentToString().equalsIgnoreCase("/trust")) {
            addTrust(event.getGroup().getId(),TrustKind.Group);
        }
        if(event.getMessage().contentToString().equalsIgnoreCase("/untrust")){
            deleteTrust(event.getGroup().getId(),TrustKind.Group);
        }
    }

    static void Trust(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!event.getMessage().contentToString().toLowerCase().contains("/trust ")&&!event.getMessage().contentToString().toLowerCase().contains("/trust-")) return;
        String rawString = event.getMessage().contentToString().toLowerCase().replace("/trust","").replace(" ","").replace("-","");
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
            event.getSubject().sendMessage("命令格式错误，请重新输入。");
            return;
        }

        try{
            ID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(ID==null||ID<10000){
            event.getSubject().sendMessage("账号格式错误，请重新输入。");
            return;
        }

        if(bk.equals(TrustKind.Friend)&&IdentityUtil.isAdmin(ID)){
            event.getSubject().sendMessage("管理员无需添加。");
            return;
        }

        addTrust(ID,bk);

        switch(bk){
            case Friend:
                event.getSubject().sendMessage("白名单已添加用户"+ID);
            case Group:
                event.getSubject().sendMessage("白名单已添加群聊"+ID);
        }

    }

    static void unTrust(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if(!event.getMessage().contentToString().toLowerCase().contains("/untrust ")&&!event.getMessage().contentToString().toLowerCase().contains("/untrust-")) return;
        String rawString = event.getMessage().contentToString().toLowerCase().replace("/untrust","").replace(" ","").replace("-","");
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
            event.getSubject().sendMessage("命令格式错误，请重新输入。");
            return;
        }

        try{
            ID = Long.parseLong(strID);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(ID==null||ID<10000){
            event.getSubject().sendMessage("账号格式错误，请重新输入。");
            return;
        }

        if(!isTrusted(ID,bk)){
            switch(bk){
                case Friend:
                    event.getSubject().sendMessage("用户"+ID+"未被添加至白名单。");
                case Group:
                    event.getSubject().sendMessage("群聊"+ID+"未被添加至白名单。");
            }
            return;
        }

        deleteTrust(ID,bk);

        switch(bk){
            case Friend:
                event.getSubject().sendMessage("已将用户"+ID+"移出白名单。");
            case Group:
                event.getSubject().sendMessage("已将群聊"+ID+"移出白名单。");
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
        if(event instanceof GroupMessageEvent){
            TrustGroupInGroup((GroupMessageEvent) event);
        }
        Trust(event);
        unTrust(event);
    }

    public void ini(){
        System.out.println("Initialize Whitelist");
    }
}
