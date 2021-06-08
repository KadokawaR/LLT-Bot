package lielietea.mirai.plugin.overwatch;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.autoreply.Hero;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class HeroLinesCluster {
    Multimap<Hero,String> ultimateAbilityHeroLines;
    Multimap<Hero,String> commonHeroLines;

    static HeroLinesCluster INSTANCE = new HeroLinesCluster();
    static Random rand = new Random();

    public static HeroLinesCluster getInstance(){
        return INSTANCE;
    }

    HeroLinesCluster(){
        loadReplyLinesFromPreset();
    }

    public static void loadReplyLinesFromPreset(){
        try{
            //读取文件
            File file = new File("src/main/resources/herolines.json");
            BufferedReader readable = Files.newReader(file, Charsets.UTF_8);
            String jsonString = CharStreams.toString(readable);

            //反序列化
            Gson gson = new GsonBuilder().registerTypeAdapter(Multimap.class,new HeroLinesMultimapTypeAdapter()).setPrettyPrinting().create();
            INSTANCE = gson.fromJson(readable, HeroLinesCluster.class);
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    //随机挑选大招台词
    static String pickUltimateAbilityHeroLineByRandomHero() {
        Hero randomHero = Hero.values()[rand.nextInt(Hero.values().length)];
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(randomHero);
        String line = (String) lines.toArray()[rand.nextInt(lines.size())];
        return line;
    }

    //随机挑选某位英雄的大招台词
    static String pickUltimateAbilityHeroLine(Hero hero){
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(hero);
        String line = (String) lines.toArray()[rand.nextInt(lines.size())];
        return line;
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
