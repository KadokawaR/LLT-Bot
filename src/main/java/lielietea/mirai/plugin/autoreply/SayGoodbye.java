package lielietea.mirai.plugin.autoreply;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.SortedMap;
import java.util.TreeMap;

public class SayGoodbye {
    //这里是权重树哦
    static TreeMap<Double,String> weightedReply = new TreeMap<Double,String>(){
        {
            /*
            在此处添加内容
            weightedReply.put(double 累计权重,string 回复);
            请注意是累计权重！
             */
            weightedReply.put(1D,"886");
            weightedReply.put(2D,"再见！");
            weightedReply.put(3D,"Bye!");
            weightedReply.put(4D,"下次再见");
        }
    };

    //
    static String pickReply() {
        double randomWeight = weightedReply.lastKey() * Math.random();
        SortedMap<Double, String> tailMap = weightedReply.tailMap(randomWeight, false);
        return weightedReply.get(tailMap.firstKey());
    }

    public static void reply(MessageEvent event){
        event.getSubject().sendMessage(pickReply());
    }


}
