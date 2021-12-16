package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.BancoDeEspana;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.Currency;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.SenoritaCounter;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;
import lielietea.mirai.plugin.core.messagehandler.game.montecarlo.roulette.Roulette;
import lielietea.mirai.plugin.utils.IdentityUtil;
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

public class Fishing extends FishingUtil{

    public static final int FISHING_COST = 800;

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

        private Waters(int code) { this.code = code; }

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

    static final List<Long> isInFishingProcessFlag = new ArrayList<>();

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
        touchRecord();
    }

    static final Fishing INSTANCE = new Fishing();

    public static Fishing getINSTANCE() {
        return INSTANCE;
    }

    static final String FISH_INFO_PATH = "/pics/fishing/fishinfo.png";
    static final String HANDBOOK_PATH = "/pics/fishing/handbook.png";

    public static void go(MessageEvent event){
        if(event.getMessage().contentToString().equals("/fishinfo")){

            try (InputStream img = Fishing.class.getResourceAsStream(FISH_INFO_PATH)) {
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if(event.getMessage().contentToString().equals("/handbook")){
            try (InputStream img = Fishing.class.getResourceAsStream(HANDBOOK_PATH)) {
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(event.getMessage().contentToString().equals("/collection")){
            MessageChainBuilder mcb = mcbProcessor(event);
            mcb.append("您的图鉴完成度目前为").append(String.valueOf(handbookProportion(event.getSender().getId()))).append("%\n\n");
            try {
                mcb.append(Contact.uploadImage(event.getSubject(),ImageSender.getBufferedImageAsSource(getHandbook(event))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getSubject().sendMessage(mcb.asMessageChain());
            return;
        }

        if (event.getMessage().contentToString().contains("/fish")){
            if (!isInFishingProcessFlag.contains(event.getSender().getId())){
                isInFishingProcessFlag.add(event.getSender().getId());
                getFish(event,getWater(event.getMessage().contentToString()));
            } else {
                MessageChainBuilder mcb = new MessageChainBuilder();
                if (event.getClass().equals(GroupMessageEvent.class)){
                    mcb.append((new At(event.getSender().getId()))).append(" ");
                }
                mcb.append("上次抛竿还在进行中。");
                event.getSubject().sendMessage(mcb.asMessageChain());
            }
        }

    }

    public static void getFish(MessageEvent event,Waters waters){
        Random random = new Random();
        int time = 3+random.nextInt(4);
        int itemNumber = 3+random.nextInt(2);
        MessageChainBuilder mcb = mcbProcessor(event);
        if(IdentityUtil.isAdmin(event)) time=0;

        //非常规水域进行扣费
        if(!waters.equals(Waters.General)){
            if(SenoritaCounter.hasEnoughMoney(event,FISHING_COST)){
                SenoritaCounter.minusMoney(event.getSender().getId(),FISHING_COST);
                mcb.append("已收到您的捕鱼费用").append(String.valueOf(FISHING_COST)).append("南瓜比索。");
            } else {
                event.getSubject().sendMessage(mcb.append("您的南瓜比索数量不够，请检查。").asMessageChain());
                return;
            }
        }

        event.getSubject().sendMessage(mcb.append("本次钓鱼预计时间为").append(String.valueOf(time)).append("分钟。").asMessageChain());
        final Timer[] timer = {new Timer()};
        timer[0].schedule(new TimerTask() {
            @Override
            public void run() {
                //随机生成包含鱼的code和数量的Map
                Map<Integer,Integer> fishList = getItemIDRandomly(itemNumber,waters);
                MessageChainBuilder mcb = new MessageChainBuilder();
                if (event.getClass().equals(GroupMessageEvent.class)){
                    mcb.append((new At(event.getSender().getId()))).append(" ");
                }
                mcb.append("您钓到了：\n\n");

                int totalValue = 0;
                for (Map.Entry<Integer, Integer> entry : fishList.entrySet()){
                    Fish fish = getFishFromCode(entry.getKey());
                    assert fish != null;
                    mcb.append(fish.name).append("x").append(String.valueOf(entry.getValue())).append("，价值").append(String.valueOf(fish.price*entry.getValue())).append("南瓜比索\n");
                    totalValue = totalValue + fish.price*entry.getValue();
                }

                mcb.append("\n共值").append(String.valueOf(totalValue)).append("南瓜比索。\n").append(Contact.uploadImage(event.getSubject(), ImageSender.getBufferedImageAsSource(getImage(new ArrayList<>(fishList.keySet())))));
                //向银行存钱
                BancoDeEspana.getINSTANCE().addMoney(event.getSender().getId(),totalValue, Currency.PumpkinPesos);
                //存储钓鱼信息
                saveRecord(event.getSender().getId(),new ArrayList<>(fishList.keySet()));
                //发送消息
                event.getSubject().sendMessage(mcb.asMessageChain());
                //解除正在钓鱼的flag
                isInFishingProcessFlag.remove(event.getSender().getId());
                timer[0].cancel();
                timer[0] = null;

            }
        }, time*60*1000);
    }

    public static Map<Integer,Integer> getItemIDRandomly(int amount,Waters waters){
        List<Integer> Weight = new ArrayList<>();
        List<Fish> fishList = INSTANCE.loadedFishingList;
        Map<Integer,Integer> fishMap = new HashMap<>();

        boolean isInDaytime = isInDaytime();
        List<Fish> actualFishList = new ArrayList<>();

        //获得实际的Fish列表，根据水域、时间获得
        for(Fish fish:fishList){
            if(fish.code/100!=waters.code&&fish.code/100!=Waters.General.code) continue;
            if(fish.time.equals(Time.All)) actualFishList.add(fish);
            if(isInDaytime&&fish.time.equals(Time.Day)) actualFishList.add(fish);
            if(!isInDaytime&&fish.time.equals(Time.Night)) actualFishList.add(fish);
        }

        //计算实际Fish列表里的最大值
        int maxPrice = 0;
        for(Fish fish:fishList){
            if(fish.price>maxPrice) maxPrice=fish.price;
        }

        //用最大值+50-价格来作为每个物品的权重
        int totalWeight = 0;
        for (Fish fish: actualFishList){
            Weight.add((maxPrice + 50 - fish.price));
            totalWeight = totalWeight + maxPrice + 50 - fish.price;
        }

        for(int j=0;j<amount;j++) {
            Random random = new Random();
            int randomNumber = random.nextInt(totalWeight);

            //随机一个数，依次减去每一条鱼的权重，如果小于0则返回该index
            int randomIndex = 0;

            for (int i = 0; i < fishList.size(); i++) {
                if ((randomNumber - Weight.get(i)) < 0) {
                    randomIndex = i;
                    break;
                } else {
                    randomNumber = randomNumber - Weight.get(i);
                }
            }

            //通过index返回鱼的code
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

    //给群聊的消息前面加AT
    public static MessageChainBuilder mcbProcessor(MessageEvent event){
        MessageChainBuilder mcb = new MessageChainBuilder();
        if (event.getClass().equals(GroupMessageEvent.class)){
            mcb.append((new At(event.getSender().getId()))).append(" ");
        }
        return mcb;
    }
}
