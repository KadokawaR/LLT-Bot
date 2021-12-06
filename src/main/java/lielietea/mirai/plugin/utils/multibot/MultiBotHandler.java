package lielietea.mirai.plugin.utils.multibot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.BankRecord;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.Bot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MultiBotHandler {

    final static String BOT_CONFIGURATION_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "botconfig.json";
    public enum BotName{
        Chitung(340865180L),     //红七筒
        Chitung2(3628496803L),   //蓝七筒
        Chitung3(384087036L),    //引号
        Chitung4(2429465624L );  //黄七筒

        private final long value;

        private BotName(long ID) { this.value = ID; }

        public long getValue() { return this.value; }

        public static BotName get(long ID){
            for(BotName bn:BotName.values()){
                if(bn.getValue()==ID) return bn;
            }
            return null;
        }
    }

    public static BotName getBotName(long ID){
        for(BotName bt : BotName.values()){
            if (bt.getValue()==ID) return bt;
        }
        return null;
    }

    MultiBotHandler(){}

    private static final MultiBotHandler INSTANCE;

    static {
        INSTANCE = new MultiBotHandler();
    }

    public BotConfigList botConfigList;

    public static MultiBotHandler getINSTANCE() {
        return INSTANCE;
    }

    public static void initialize(){
        getINSTANCE().botConfigList = new BotConfigList();
        if(Touch.file(BOT_CONFIGURATION_PATH)){
            try {
                getINSTANCE().botConfigList = new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(new FileInputStream(BOT_CONFIGURATION_PATH)))), BotConfigList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (BotName bt : BotName.values()) {
                getINSTANCE().botConfigList.botConfigs.add(new BotConfig(bt.getValue()));
            }
            String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().botConfigList);
            Write.cover(jsonString, BOT_CONFIGURATION_PATH);
        }
    }

    private Integer getIndexOfBot(long ID){
        int index=0;
        for(BotConfig bc:getINSTANCE().botConfigList.botConfigs){
            if(bc.ID==ID) return index;
            index+=1;
        }
        return null;
    }

    public static String rejectInformation(long ID){
        StringBuilder sb = new StringBuilder();
        sb.append("该账号已经停止接受加入新群组，请尝试如下账号：");
        for(Bot bot:Bot.getInstances()){
            if(bot.getId()==ID) continue;
            if(getINSTANCE().botConfigList.botConfigs.get(getINSTANCE().getIndexOfBot(bot.getId())).acceptGroup){
                sb.append("\n").append(bot.getId()).append(" ").append(bot.getNick());
            }
        }
        return sb.toString();
    }

    public static boolean canAcceptGroup(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(getINSTANCE().getIndexOfBot(ID)).acceptGroup;
    }

    public static boolean canAcceptFriend(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(getINSTANCE().getIndexOfBot(ID)).acceptFriend;
    }

    public static boolean canAnswerGroup(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(getINSTANCE().getIndexOfBot(ID)).answerGroup;
    }

    public static boolean canAnswerFriend(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(getINSTANCE().getIndexOfBot(ID)).answerFriend;
    }
}
