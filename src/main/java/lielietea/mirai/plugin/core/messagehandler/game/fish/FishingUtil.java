package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FishingUtil {

    final static String FISHING_RECORD_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "fishrecord.json";

    static class SingleRecord{
        long ID;
        List<Integer>  recordList;
    }

    static class FishingRecord{ List<SingleRecord> singleRecords;}

    public static FishingRecord openRecord() throws IOException {
        InputStreamReader is = new InputStreamReader(new FileInputStream(FISHING_RECORD_PATH));
        BufferedReader br = new BufferedReader(is);
        Gson gson = new Gson();
        return gson.fromJson(Read.fromReader(br), FishingRecord.class);
    }

    public static void saveRecord(long ID, int itemID){
        try {
            FishingRecord fr = openRecord();
            for ( int index = 0; index < fr.singleRecords.size(); index++){
                if (fr.singleRecords.get(index).ID==ID){
                    fr.singleRecords.get(index).recordList.add(itemID);
                    break;
                }
            }
            Gson gson = new Gson();
            Write.cover(gson.toJson(fr),FISHING_RECORD_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
