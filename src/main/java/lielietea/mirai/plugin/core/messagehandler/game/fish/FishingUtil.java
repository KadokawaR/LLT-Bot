package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FishingUtil {
    class FishRecord{
        long ID;
        List<Integer>  recordList;
    }

    FishingUtil(){
        initialize();
    }

    void initialize(){
        
    }

    public static void saveRecord(){
        String FISHINGLIST_PATH = "/fishing/FishingList.json";
        InputStream is = JetPackUtil.class.getResourceAsStream(FISHINGLIST_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        Fishing.FishingList fl = gson.fromJson(br, Fishing.FishingList.class);
    }


}
