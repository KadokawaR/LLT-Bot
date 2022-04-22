package lielietea.mirai.plugin.utils.multibot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import lielietea.mirai.plugin.utils.multibot.config.Config;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.*;

public class MultiBotHandler {

    final static String BOT_CONFIGURATION_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "botconfig.json";
    public enum BotName{
        //Chitung1(340865180L),     //七筒#1
        //Chitung2(3628496803L),   //七筒#2
        Chitung3(2429465624L ),  //七筒#3
        Chitung4(3582637350L),    //七筒#4
        Chitung5(1256252623L);    //七筒#5

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
        initialize();
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
                getINSTANCE().botConfigList.botConfigs.put(bt,new Config());
            }
            writeRecord();
        }
    }

    public static BotConfigList readRecord(){
        try {
            return new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(new FileInputStream(BOT_CONFIGURATION_PATH)))), BotConfigList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().botConfigList);
        Write.cover(jsonString, BOT_CONFIGURATION_PATH);
    }

    private Integer getIndexOfBot(long ID){
        int index=0;
        for(BotName bt:getINSTANCE().botConfigList.botConfigs.keySet()){
            if(bt.getValue()==ID) return index;
            index+=1;
        }
        return null;
    }

    public static String rejectInformation(long ID){
        StringBuilder sb = new StringBuilder();
        sb.append("该账号已经停止接受加入新群组，请尝试如下账号：");
        int count = 0;
        for(Bot bot:Bot.getInstances()){
            if(bot.getId()==ID) continue;
            if(getINSTANCE().botConfigList.botConfigs.get(BotName.get(bot.getId())).getRc().isAddGroup()){
                sb.append("\n").append(bot.getId()).append(" ").append(bot.getNick());
                count++;
            }
        }
        if(count==0) sb.append("\n\n艹 我们没号了，先别加群了。");
        return sb.toString();
    }

    public static boolean canAcceptGroup(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(BotName.get(ID)).getRc().isAddGroup();
    }

    public static boolean canAcceptFriend(long ID){
        return getINSTANCE().botConfigList.botConfigs.get(BotName.get(ID)).getRc().isAddFriend();
    }

    public static boolean canAnswerGroup(GroupMessageEvent event){
        if(IdentityUtil.isAdmin(event)) return true;
        return getINSTANCE().botConfigList.botConfigs.get(BotName.get(event.getBot().getId())).getRc().isAnswerGroup();
    }

    public static boolean canAnswerFriend(FriendMessageEvent event){
        if(IdentityUtil.isAdmin(event)) return true;
        return getINSTANCE().botConfigList.botConfigs.get(BotName.get(event.getBot().getId())).getRc().isAnswerFriend();
    }

    public static boolean canSendNotice(Bot bot){
        return getINSTANCE().botConfigList.botConfigs.get(BotName.get(bot.getId())).getRc().isAutoAnswer();
    }

}
