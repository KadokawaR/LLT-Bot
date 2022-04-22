package lielietea.mirai.plugin.core.secretfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.core.secretfunction.antiwithdraw.AntiWithdraw;
import lielietea.mirai.plugin.core.secretfunction.repeater.Repeater;
import lielietea.mirai.plugin.utils.IdentityUtil;
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

public class SecretFunctionDatabase {

    static String WhiteLIST_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Chitung" + File.separator + "SecretFunctionData.json";

    SecretFunctionDatabase(){}

    private static final SecretFunctionDatabase INSTANCE;

    static {
        INSTANCE = new SecretFunctionDatabase();
        initialize();
    }

    public SecretFunctionData secretFunctionData;

    public static SecretFunctionDatabase getINSTANCE() {
        return INSTANCE;
    }

    static void initialize(){
        getINSTANCE().secretFunctionData = new SecretFunctionData();
        if(Touch.file(WhiteLIST_PATH)){
            try {
                getINSTANCE().secretFunctionData = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(WhiteLIST_PATH)), StandardCharsets.UTF_8))), SecretFunctionData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeRecord();
        }
    }

    static void readRecord(){
        try {
            getINSTANCE().secretFunctionData = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(WhiteLIST_PATH)), StandardCharsets.UTF_8))), SecretFunctionData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().secretFunctionData);
        Write.cover(jsonString, WhiteLIST_PATH);
    }

}
