package lielietea.mirai.plugin.core.game.fish;

import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;
import lielietea.mirai.plugin.administration.statistics.GameCenterCount;
import lielietea.mirai.plugin.core.bank.PumpkinPesoWindow;
import com.google.gson.Gson;

import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fishing extends FishingUtil{

    public static final int FISHING_COST = 800;
    public static final int MAX_COUNT_IN_ONE_HOUR = 60;

    public enum Time{
        Day,
        All,
        Night
    }

    public enum Waters{
        Amur(1), //A
        Caroline(2), //B
        Chishima(3), //C
        General(4);

        private final int code;

        Waters(int code) { this.code = code; }

        public int getCode(){
            return this.code;
        }

    }

    static class Fish{
        int code;
        String name;
        int price;
        Time time;
    }

    static class FishingList{
        List<Fish> fishingList;
    }

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    static final List<Long> isInFishingProcessFlag = new ArrayList<>();
    final List<Fish> loadedFishingList;
    List<Date> fishRecord;

    Fishing() {
        loadedFishingList = new ArrayList<>();
        fishRecord = new ArrayList<>();
        String FISHINGLIST_PATH = "/fishing/FishingList.json";
        InputStream is = Fishing.class.getResourceAsStream(FISHINGLIST_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        FishingList fl = gson.fromJson(br,FishingList.class);
        loadedFishingList.addAll(fl.fishingList);
        touchRecord();
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final Fishing INSTANCE = new Fishing();

    public static Fishing getINSTANCE() {
        return INSTANCE;
    }

    static final String FISH_INFO_PATH = "/pics/fishing/fishinfo.png";
    static final String HANDBOOK_PATH = "/pics/fishing/handbook.png";

    public static void go(MessageEvent event){

        String message = event.getMessage().contentToString();

        if(event instanceof GroupMessageEvent) {
            if (!GroupConfigManager.fishConfig((GroupMessageEvent) event) || !ConfigHandler.getConfig(event).getGroupFC().isFish()) return;
        } else {
            if(!ConfigHandler.getConfig(event).getFriendFC().isFish()) return;
        }

        if(message.equalsIgnoreCase("/fishhelp")){

            try (InputStream img = Fishing.class.getResourceAsStream(FISH_INFO_PATH)) {
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Harbor.count(event);
            GameCenterCount.count(GameCenterCount.Functions.FishingInfo);
            return;
        }

        if(message.equalsIgnoreCase("/handbook")){
            try (InputStream img = Fishing.class.getResourceAsStream(HANDBOOK_PATH)) {
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Harbor.count(event);
            GameCenterCount.count(GameCenterCount.Functions.FishingHandbook);
            return;
        }

        if(message.equalsIgnoreCase("/collection")){
            MessageChainBuilder mcb = mcbProcessor(event);
            mcb.append("??????????????????????????????").append(String.valueOf(handbookProportion(event.getSender().getId()))).append("%\n\n");
            try {
                mcb.append(Contact.uploadImage(event.getSubject(),ImageSender.getBufferedImageAsSource(getHandbook(event))));
                event.getSubject().sendMessage(mcb.asMessageChain());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Harbor.count(event);
            GameCenterCount.count(GameCenterCount.Functions.FishingCollection);
            return;
        }

        if (message.toLowerCase().startsWith("/fish")){
            if (!isInFishingProcessFlag.contains(event.getSender().getId())){

                getFish(event,getWater(message));

                Harbor.count(event);
                return;

            } else {
                MessageChainBuilder mcb = new MessageChainBuilder();

                if (event.getClass().equals(GroupMessageEvent.class)){
                    mcb.append((new At(event.getSender().getId()))).append(" ");
                }

                mcb.append("??????????????????????????????");
                event.getSubject().sendMessage(mcb.asMessageChain());
                Harbor.count(event);
                GameCenterCount.count(GameCenterCount.Functions.FishingNotReadyYet);
                return;

            }
        }

        if(message.equalsIgnoreCase("/endfish")){
            MessageChainBuilder mcb = new MessageChainBuilder();
            if (event.getClass().equals(GroupMessageEvent.class)){
                mcb.append((new At(event.getSender().getId()))).append(" ");
            }
            if(isInFishingProcessFlag.contains(event.getSender().getId())) {
                isInFishingProcessFlag.remove(event.getSender().getId());
                event.getSubject().sendMessage(mcb.append("?????????????????????").asMessageChain());
            } else {
                event.getSubject().sendMessage(mcb.append("?????????????????????").asMessageChain());
            }

            Harbor.count(event);
            GameCenterCount.count(GameCenterCount.Functions.EndFishing);
        }

    }

    public static void getFish(MessageEvent event,Waters waters){
        MessageChainBuilder mcb = mcbProcessor(event);
        Random random = new Random();

        //???????????????????????????
        if(!waters.equals(Waters.General)){
            if(PumpkinPesoWindow.hasEnoughMoney(event,FISHING_COST)){
                PumpkinPesoWindow.minusMoney(event.getSender().getId(),FISHING_COST);
                mcb.append("???????????????????????????").append(String.valueOf(FISHING_COST)).append("???????????????");
            } else {
                event.getSubject().sendMessage(mcb.append("?????????????????????????????????????????????").asMessageChain());
                return;
            }
        }

        updateRecord();
        int recordInOneHour = fishInOneHour(getINSTANCE().fishRecord);
        int time = 3+random.nextInt(4)+recordInOneHour;//??????????????????
        int itemNumber = 3+random.nextInt(2);

        getINSTANCE().fishRecord.add(new Date());
        isInFishingProcessFlag.add(event.getSender().getId());

        mcb.append("???????????????????????????").append(String.valueOf(time)).append("?????????");
        if(event instanceof GroupMessageEvent) mcb.append("?????????????????????????????????/fishhelp??????????????????????????????????????????????????????????????????????????????/endfish ?????????????????????");
        else mcb.append("?????????????????????????????????/fishhelp??????????????????????????????????????????????????????????????????????????????/endfish ?????????????????????");
        event.getSubject().sendMessage(mcb.asMessageChain());

        executor.schedule(new fishRunnable(event,itemNumber,waters,recordInOneHour),time, TimeUnit.MINUTES);
        GameCenterCount.count(GameCenterCount.Functions.FishingGo);
    }

    public static Map<Integer,Integer> getItemIDRandomly(int amount,Waters waters){
        List<Integer> Weight = new ArrayList<>();
        List<Fish> fishList = INSTANCE.loadedFishingList;
        Map<Integer,Integer> fishMap = new HashMap<>();

        boolean isInDaytime = isInDaytime();
        List<Fish> actualFishList = new ArrayList<>();

        //???????????????Fish????????????????????????????????????
        for(Fish fish:fishList){
            if(fish.code/100!=waters.code&&fish.code/100!=Waters.General.code) continue;
            if(fish.time.equals(Time.All)) actualFishList.add(fish);
            if(isInDaytime&&fish.time.equals(Time.Day)) actualFishList.add(fish);
            if(!isInDaytime&&fish.time.equals(Time.Night)) actualFishList.add(fish);
        }

        //????????????Fish?????????????????????
        int maxPrice = 0;
        for(Fish fish:fishList){
            if(fish.price>maxPrice) maxPrice=fish.price;
        }

        //????????????+50-????????????????????????????????????
        int totalWeight = 0;
        for (Fish fish: actualFishList){
            Weight.add((maxPrice + 50 - fish.price));
            totalWeight = totalWeight + maxPrice + 50 - fish.price;
        }

        for(int j=0;j<amount;j++) {
            Random random = new Random();
            int randomNumber = random.nextInt(totalWeight);

            //??????????????????????????????????????????????????????????????????0????????????index
            int randomIndex = 0;

            for (int i = 0; i < fishList.size(); i++) {
                if ((randomNumber - Weight.get(i)) < 0) {
                    randomIndex = i;
                    break;
                } else {
                    randomNumber = randomNumber - Weight.get(i);
                }
            }

            //??????index????????????code
            Fish fish = actualFishList.get(randomIndex);
            if(fishMap.containsKey(fish.code)){
                int amountOfFish = fishMap.get(fish.code);
                fishMap.replace(fish.code, amountOfFish+1);
            } else {
                fishMap.put(fish.code, 1);
            }
        }
        return fishMap;
    }

    static Fish getFishFromCode (int code){
        for (Fish fish: INSTANCE.loadedFishingList){
            if (fish.code == code){
                return fish;
            }
        }
        return null;
    }

    //???????????????????????????AT
    public static MessageChainBuilder mcbProcessor(MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if (event.getClass().equals(GroupMessageEvent.class)){
            mcb.append((new At(event.getSender().getId()))).append(" ");
        }
        return mcb;
    }

    static class fishRunnable implements Runnable {

        MessageEvent event;
        int itemNumber;
        Waters waters;
        int recordInOneHour;

        fishRunnable(MessageEvent event,int itemNumber,Waters waters,int recordInOneHour){
            this.event=event;
            this.itemNumber=itemNumber;
            this.waters=waters;
            this.recordInOneHour=recordInOneHour;
        }

        @Override
        public void run(){
            try {
                //????????????????????????code????????????Map
                if(!isInFishingProcessFlag.contains(event.getSender().getId())) return;

                Map<Integer, Integer> fishList = getItemIDRandomly(itemNumber, waters);
                MessageChainBuilder mcb = new MessageChainBuilder();
                if (event.getClass().equals(GroupMessageEvent.class)) {
                    mcb.append((new At(event.getSender().getId()))).append(" ");
                }
                mcb.append("???????????????\n\n");

                int totalValue = 0;
                for (Map.Entry<Integer, Integer> entry : fishList.entrySet()) {
                    Fish fish = getFishFromCode(entry.getKey());
                    assert fish != null;
                    mcb.append(fish.name).append("x").append(String.valueOf(entry.getValue())).append("?????????").append(String.valueOf(fish.price * entry.getValue())).append("????????????\n");
                    totalValue = totalValue + fish.price * entry.getValue();
                }

                totalValue = (int) (totalValue * (1F + (float)recordInOneHour * 0.05F));
                mcb.append("\n?????????????????????").append(String.valueOf(1F + (float)recordInOneHour * 0.05F)).append("?????????").append(String.valueOf(totalValue)).append("???????????????\n\n").append(Contact.uploadImage(event.getSubject(), ImageSender.getBufferedImageAsSource(getImage(new ArrayList<>(fishList.keySet())))));
                //???????????????
                PumpkinPesoWindow.addMoney(event.getSender().getId(), totalValue);
                //??????????????????
                saveRecord(event.getSender().getId(), new ArrayList<>(fishList.keySet()));
                //????????????
                event.getSubject().sendMessage(mcb.asMessageChain());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //?????????????????????flag
                isInFishingProcessFlag.remove(event.getSender().getId());
            }
        }
    }
}
