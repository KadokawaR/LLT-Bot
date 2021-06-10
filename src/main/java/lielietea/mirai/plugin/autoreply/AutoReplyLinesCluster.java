package lielietea.mirai.plugin.autoreply;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

class AutoReplyLinesCluster {
    TreeMap<Double,String> goodbyeReplyLines;
    TreeMap<Double,String> antiDirtyWordsReplyLines;
    TreeMap<Double,String> antiOverwatchGameReplyLines;

    static Gson gson = new Gson();
    static AutoReplyLinesCluster INSTANCE;

    static String DEFAULT_AUTOREPLY_JSON_PATH = "src/main/resources/cluster/autoreply.json";

    static {
        try {
            INSTANCE = gson.fromJson(Files.newReader(new File(DEFAULT_AUTOREPLY_JSON_PATH), Charsets.UTF_8), AutoReplyLinesCluster.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    AutoReplyLinesCluster(){};

    public static void loadReplyLinesFromPreset(){
        try{
            //读取文件
            File file = new File(DEFAULT_AUTOREPLY_JSON_PATH);
            BufferedReader readable = Files.newReader(file, Charsets.UTF_8);

            //反序列化
            INSTANCE = gson.fromJson(readable,AutoReplyLinesCluster.class);
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public static AutoReplyLinesCluster getInstance(){
        return INSTANCE;
    }

    //根据类型和权重挑选回复消息
    static String pickReply(ReplyType type) {
        TreeMap<Double,String> selectedLines;
        switch (type){
            case ANTI_OVERWATCH_GAME:
                selectedLines = getInstance().antiOverwatchGameReplyLines;
                break;
            case GOODBYE:
                selectedLines = getInstance().goodbyeReplyLines;
                break;
            case ANTI_DIRTY_WORDS:
            default:
                selectedLines = getInstance().antiDirtyWordsReplyLines;
                break;
        }
        double randomWeight = selectedLines.lastKey() * Math.random();
        SortedMap<Double, String> tailMap = selectedLines.tailMap(randomWeight, false);
        return selectedLines.get(tailMap.firstKey());
    }

    //回复消息
    public static void reply(MessageEvent event, ReplyType type){
        event.getSubject().sendMessage(pickReply(type));
    }

    @Override
    public String toString() {
        return "AutoReplyLinesCluster{" +
                "goodbyeReplyLines=" + goodbyeReplyLines +
                ", antiDirtyWordsReplyLines=" + antiDirtyWordsReplyLines +
                ", antiOverwatchGameReplyLines=" + antiOverwatchGameReplyLines +
                '}';
    }

    //回复的类型
    public enum ReplyType{
        ANTI_OVERWATCH_GAME,
        ANTI_DIRTY_WORDS,
        GOODBYE
    }

}
