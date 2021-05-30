package lielietea.mirai.plugin.utils;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class Echo {
    static ArrayList<String> Overwatch_array = new ArrayList<String>(Arrays.asList(
            "狗都不玩",
            "狗都不玩",
            "狗都不玩",
            "不会吧不会吧不会真的有人还在玩吧？"
    ));

    static ArrayList<String> motherfucker_array = new ArrayList<String>(Arrays.asList(
            "Watch your language",
            "嘴巴放干净点"
    ));

    static ArrayList<String> byebye_array = new ArrayList<String>(Arrays.asList(
            "886",
            "再见！",
            "Bye!",
            "下次再见"
    ));

    public static void sendSentence(MessageEvent event, String word, String sentence){
        int result = event.getMessage().contentToString().indexOf(word);
        if (result != -1){
            event.getSubject().sendMessage(sentence);
        }
    }
    public static int getWordsArrayNum(ArrayList<String> array){ return array.size(); }

    public static int getRandomArrayNum(int arraySize, ArrayList<String> array){
        int random = (int)(Math.random()*array.size());
        return random;
    }

    public static ArrayList<String> getArrayList(String word){
        switch(word){
            case "overwatch":
            case "Overwatch":
            case "守望先锋":
                return Overwatch_array;
            case "motherfucker":
            case "草泥马":
            case "操你妈":
            case "日你妈":
                return motherfucker_array;
            case "下线":
            case "88":
                return byebye_array;
        }
        return (ArrayList<String>)null;
    }

    public static String sentence(ArrayList<String> array){
        return array.get(getRandomArrayNum(getWordsArrayNum(array),array));
    }

    public static void send(MessageEvent event, String word){
        sendSentence(event,word,sentence(getArrayList(word)));
    }

    public static void sendAll(MessageEvent event){
        send(event, "overwatch");
        send(event, "Overwatch");
        send(event, "守望先锋");
        send(event, "motherfucker");
        send(event, "草泥马");
        send(event, "操你妈");
        send(event, "日你妈");
        send(event, "下线");
        send(event, "88");
    }
}
