package lielietea.mirai.plugin.messageresponder.autoreply;

import com.google.gson.Gson;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings("ConstantConditions")
class AutoReplyLinesCluster {
    TreeMap<Double,String> goodbyeReplyLines;
    TreeMap<Double,String> antiDirtyWordsReplyLines;
    TreeMap<Double,String> antiOverwatchGameReplyLines;

    static final Gson gson = new Gson();
    static AutoReplyLinesCluster INSTANCE;

    static final String DEFAULT_AUTOREPLY_JSON_PATH = "/cluster/autoreply.json";


    static {
        InputStream is = AutoReplyLinesCluster.class.getResourceAsStream(DEFAULT_AUTOREPLY_JSON_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        INSTANCE = gson.fromJson(br, AutoReplyLinesCluster.class);
    }

    AutoReplyLinesCluster(){}

    public static void loadReplyLinesFromPreset(){
        InputStream is = AutoReplyLinesCluster.class.getResourceAsStream(DEFAULT_AUTOREPLY_JSON_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        INSTANCE = gson.fromJson(br, AutoReplyLinesCluster.class);
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
