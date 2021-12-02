package lielietea.mirai.plugin.core.messagehandler.game.fish;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.BancoDeEspana;
import lielietea.mirai.plugin.core.messagehandler.game.bancodeespana.Currency;
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
        if (event.getMessage().contentToString().contains("/fish")||event.getMessage().contentToString().equals("开始钓鱼")){
            if (!isInFishingProcessFlag.contains(event.getSender().getId())){
                isInFishingProcessFlag.add(event.getSender().getId());
                getFish(event);
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

    public static void getFish(MessageEvent event){
        Random random = new Random();
        int time = 3+random.nextInt(4);
        int itemNumber = 3+random.nextInt(6);
        event.getSubject().sendMessage("本次钓鱼预计时间为"+time+"分钟。");
        final Timer[] timer = {new Timer()};
        timer[0].schedule(new TimerTask() {
            @Override
            public void run() {
                //随机生成包含鱼的code和数量的Map
                Map<Integer,Integer> fishList = getItemIDRandomly(itemNumber);
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

                mcb.append("\n共值").append(String.valueOf(totalValue)).append("南瓜比索。");
                //向银行存钱
                BancoDeEspana.getINSTANCE().addMoney(event.getSender().getId(),totalValue, Currency.PumpkinPesos);
                //发送消息
                event.getSubject().sendMessage(mcb.asMessageChain());
                //解除正在钓鱼的flag
                isInFishingProcessFlag.remove(event.getSender().getId());
                timer[0].cancel();
                timer[0] = null;

            }
        }, time*60*1000);
    }

    public static Map<Integer,Integer> getItemIDRandomly(int amount){
        List<Integer> Weight = new ArrayList<>();
        List<Fish> fishList = INSTANCE.loadedFishingList;
        Map<Integer,Integer> fishMap = new HashMap<>();
        //用210-价格来作为每个物品的权重
        int totalWeight = 0;
        for (Fish fish: fishList){
            Weight.add((210-fish.price));
            totalWeight = totalWeight + 210 - fish.price;
        }

        for(int j=0;j<amount;j++) {
            Random random = new Random();
            int randomNumber = random.nextInt(totalWeight);

            //随机一个数，依次减去每一条鱼的权重，如果小于0则返回该index
            int randomIndex = -1;

            for (int i = 0; i < fishList.size(); i++) {
                if ((randomNumber - Weight.get(i)) < 0) {
                    randomIndex = i;
                    break;
                } else {
                    randomNumber = randomNumber - Weight.get(i);
                }
            }

            //防止出问题出现index=-1
            if (randomIndex == -1) randomIndex = 0;
            //通过index返回鱼的code
            Fish fish = fishList.get(randomIndex);
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
}
