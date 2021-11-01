package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fishing{
    static class Fish{
        int code;
        String name;
        int price;
    }

    static class FishingList{
        List<Fish> fishingList;
    }

    final List<Fish> loadedFishingList;

    Fishing() {
        loadedFishingList = new ArrayList<>();
        String FISHINGLIST_PATH = "/fishing/FishingList.json";
        InputStream is = JetPackUtil.class.getResourceAsStream(FISHINGLIST_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        FishingList fl = gson.fromJson(br,FishingList.class);
        loadedFishingList.addAll(fl.fishingList);
    }

    static final Fishing INSTANCE = new Fishing();

    public static Fishing getINSTANCE() {
        return INSTANCE;
    }

    public static void go(MessageEvent event){
        if (event.getMessage().contentToString().contains("/fish")){

        }
    }

    public static void getFish(MessageEvent event){
        Random random = new Random();

    }
}
