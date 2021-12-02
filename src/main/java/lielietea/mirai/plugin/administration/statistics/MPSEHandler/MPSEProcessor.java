package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.fish.FishingUtil;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MPSEProcessor {

    final static String FILE_DIR_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Statistics";
    final static String FILE_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "Statistics" + File.separator + "MPSEData.json";

    public static boolean touchDataFIle(){
        File dir = new File(FILE_DIR_PATH);
        File json = new File(FILE_PATH);
        if(!dir.exists()){
            try {
                dir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!json.exists()){
            try {
                json.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json.exists();
    }

    public static DataList openData() throws IOException {
        return new Gson().fromJson(Read.fromReader(new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PATH)))), DataList.class);
    }

    public static void writeData(DataList dataList) throws IOException {
        Write.cover(new Gson().toJson(dataList),FILE_PATH);
    }

    public static Date updateDaysByGetTime(Date dateTime/*日期*/,int n/*加减天数*/) {
        return new Date(dateTime.getTime() + n * 24 * 60 * 60 * 1000L);
    }

    public static Date updateMinutesByGetTime(Date dateTime/*日期*/,int n/*加减分钟*/) {
        return new Date(dateTime.getTime() + n * 60 * 1000L);
    }

    public static void updateDataList(){
        int groupMessageCount = MessagePostSendEventHandler.INSTANCE.groupMessageCount;
        int friendMessageCount = MessagePostSendEventHandler.INSTANCE.FriendMessageCount;
        int failedMessageCount = MessagePostSendEventHandler.INSTANCE.failedMessageCount;
        Date date = new Date();
        Date dateAWeekAgo = updateDaysByGetTime(date,-7);
        Data data = new Data(date,friendMessageCount, groupMessageCount,failedMessageCount);
        MessagePostSendEventHandler.getINSTANCE().dataList.datas.add(data);
        MessagePostSendEventHandler.getINSTANCE().dataList.datas.removeIf(dt -> dt.date.before(dateAWeekAgo));
    }

}
