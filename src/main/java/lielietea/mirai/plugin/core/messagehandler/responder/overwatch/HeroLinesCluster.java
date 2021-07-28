package lielietea.mirai.plugin.core.messagehandler.responder.overwatch;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Random;

class HeroLinesCluster {
    Multimap<Hero, String> ultimateAbilityHeroLines;
    //实际上普通台词我们压根就没写
    //先放着吧，估计也用不上
    Multimap<Hero, String> commonHeroLines;

    @SuppressWarnings("rawtypes")
    static final Gson gson = new GsonBuilder().registerTypeAdapter(Multimap.class, new HeroLinesMultimapTypeAdapter()).setPrettyPrinting().create();
    static final Random rand = new Random();
    static final HeroLinesCluster INSTANCE;

    static final String DEFAULT_HEROLINES_JSON_PATH = "/cluster/herolines.json";

    static {
        InputStream is = HeroLinesCluster.class.getResourceAsStream(DEFAULT_HEROLINES_JSON_PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        INSTANCE = gson.fromJson(br, HeroLinesCluster.class);
    }

    HeroLinesCluster() {
    }

    public static HeroLinesCluster getInstance() {
        return INSTANCE;
    }

    //随机挑选大招台词
    static String pickUltimateAbilityHeroLineByRandomHero() {
        Hero randomHero = Hero.values()[rand.nextInt(Hero.values().length)];
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(randomHero);
        return (String) lines.toArray()[rand.nextInt(lines.size())];
    }

    //随机挑选某位英雄的大招台词
    static String pickUltimateAbilityHeroLine(Hero hero) {
        Collection<String> lines = INSTANCE.ultimateAbilityHeroLines.get(hero);
        return (String) lines.toArray()[rand.nextInt(lines.size())];
    }


    //回复消息
    public static void reply(MessageEvent event) {
        //我们还没有加入英雄的普通台词，目前默认回复大招台词
        event.getSubject().sendMessage(pickUltimateAbilityHeroLineByRandomHero());
    }

    //根据选择英雄来回复消息
    public static void reply(MessageEvent event, Hero hero) {
        //我们还没有加入英雄的普通台词，目前默认回复大招台词
        event.getSubject().sendMessage(pickUltimateAbilityHeroLine(hero));
    }
}
