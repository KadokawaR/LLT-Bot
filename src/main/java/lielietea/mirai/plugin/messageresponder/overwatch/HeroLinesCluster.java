package lielietea.mirai.plugin.messageresponder.overwatch;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.util.Collection;
import java.util.Random;

class HeroLinesCluster {
    Multimap<Hero,String> ultimateAbilityHeroLines;
    Multimap<Hero,String> commonHeroLines;

    @SuppressWarnings("rawtypes")
    static final Gson gson = new GsonBuilder().registerTypeAdapter(Multimap.class,new HeroLinesMultimapTypeAdapter()).setPrettyPrinting().create();
    static final Random rand = new Random();
    static HeroLinesCluster INSTANCE;

    static final String DEFAULT_HEROLINES_JSON_PATH = "/cluster/herolines.json";

    static {
        InputStream is = HeroLinesCluster.class.getResourceAsStream(DEFAULT_HEROLINES_JSON_PATH);
        BufferedReader br =new BufferedReader(new InputStreamReader(is));
        INSTANCE = gson.fromJson(br, HeroLinesCluster.class);
    }

    HeroLinesCluster(){}

    public static HeroLinesCluster getInstance(){
        return INSTANCE;
    }

    //重载默认herolines.json
    public static void reloadReplyLinesFromPreset(){
        InputStream is = HeroLinesCluster.class.getResourceAsStream(DEFAULT_HEROLINES_JSON_PATH);
        BufferedReader br =new BufferedReader(new InputStreamReader(is));
        INSTANCE = gson.fromJson(br, HeroLinesCluster.class);
    }

    //随机挑选大招台词
    static String pickUltimateAbilityHeroLineByRandomHero() {
        Hero randomHero = Hero.values()[rand.nextInt(Hero.values().length)];
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(randomHero);
        return (String) lines.toArray()[rand.nextInt(lines.size())];
    }

    //随机挑选某位英雄的大招台词
    static String pickUltimateAbilityHeroLine(Hero hero){
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(hero);
        return (String) lines.toArray()[rand.nextInt(lines.size())];
    }


    //回复消息
    public static void reply(MessageEvent event){
        event.getSubject().sendMessage(pickUltimateAbilityHeroLineByRandomHero());
    }

    //根据选择英雄来回复消息
    public static void reply(MessageEvent event, Hero hero){
        event.getSubject().sendMessage(pickUltimateAbilityHeroLine(hero));
    }

    @Override
    public String toString() {
        return "HeroLinesCluster{" +
                "ultimateAbilityHeroLines=" + ultimateAbilityHeroLines +
                ", commonHeroLines=" + commonHeroLines +
                '}';
    }
}
