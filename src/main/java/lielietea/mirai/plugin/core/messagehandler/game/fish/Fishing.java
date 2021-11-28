package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.jetpack.JetPackUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Fishing{
    static class Fish{
        int code;
        String name;
        int price;
    }

    static class FishingList{
        List<Fish> fishingList;
    }

    static final List<Long> isInFishingProcessFlag = new ArrayList<>();

    final List<Fish> loadedFishingList;
    static final Timer timerFishing = new Timer(true);

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
            if (!isInFishingProcessFlag.contains(event.getSender().getId())){
                isInFishingProcessFlag.add(event.getSender().getId());
                getFish(event);
            } else {
                MessageChainBuilder mcb = new MessageChainBuilder();
                if (event.getClass().equals(GroupMessageEvent.class)){
                    mcb.append((new At(event.getSender().getId())));
                }
                mcb.append("上次抛竿还在进行中。");
                event.getSubject().sendMessage(mcb.asMessageChain());
            }
        }
    }

    public static void getFish(MessageEvent event){
        Random random = new Random();
        int time = 3+random.nextInt(4);
        int itemNumber = 3+random.nextInt(6);
        event.getSubject().sendMessage("本次钓鱼预计时间为"+time+"分钟。");
        timerFishing.schedule(new TimerTask() {
            @Override
            public void run() {

                MessageChainBuilder mcb = new MessageChainBuilder();
                if (event.getClass().equals(GroupMessageEvent.class)){
                    mcb.append((new At(event.getSender().getId())));
                }

                mcb.append("您钓到了：\n\n");
                int totalValue = 0;

                for(int i = 0; i<itemNumber;i++){
                    Fish fish = getFishFromCode(getItemIDRandomly());
                    assert fish != null;
                    FishingUtil.saveRecord(event.getSender().getId(),fish.code);
                    mcb.append(fish.name).append("x1").append("，价值").append(String.valueOf(fish.price)).append("鱼币\n");
                    totalValue = totalValue + fish.price;
                }

                mcb.append("\n共值").append(String.valueOf(totalValue)).append("鱼币。");
                event.getSubject().sendMessage(mcb.asMessageChain());
                isInFishingProcessFlag.remove(event.getSender().getId());
            }
        }, time*60000);
    }

    public static int getItemIDRandomly(){
        List<Integer> Weight = new ArrayList<>();
        List<Fish> fishList = INSTANCE.loadedFishingList;

        //用210-价格来作为每个物品的权重
        int totalWeight = 0;
        for (Fish fish: fishList){
            Weight.add((210-fish.price));
            totalWeight = totalWeight + 210 - fish.price;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(totalWeight);


        //随机一个数，依次减去每一条鱼的权重，如果小于0则返回该index
        int randomIndex = -1;

        for (int i = 0 ; i<fishList.size(); i++){
            if ((randomNumber-Weight.get(i))<0){
                randomIndex = i;
                break;
            } else {
                randomNumber = randomNumber - Weight.get(i);
            }
        }

        //通过index返回鱼的code
        if (randomIndex == -1) randomIndex = 0;
        return fishList.get(randomIndex).code;
    }

    static Fish getFishFromCode (int code){
        for (Fish fish: INSTANCE.loadedFishingList){
            if (fish.code == code){
                return fish;
            }
        }
        return null;
    }
}
