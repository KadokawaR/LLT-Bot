package lielietea.mirai.plugin.overwatch;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Deprecated
public class HeroLines {
    static List<String> allHeroVoiceLines = new ArrayList<>(Arrays.asList(
            //Ana
            "释放你的怒火吧！","纳米激素已注入！","你被强化了！快上！","纳米激素已注射。把敌人全干掉吧！",
            //Baptiste
            "矩阵启动，开火！","Vide bal sou yo!",
            //Brigitte
            "英雄集结","Alla till mig!",
            //Lucio
            "哦，在这停顿！","尝尝我们的厉害！",
            //Mercy
            "英雄不朽！","我的奴仆是不死的！","Heroes never die!","Til Valhalla!",
            //Moira
            "听从我的意志！","屈服于我",
            //Zenyatta
            "遁入智瞳","感受宁静","遁入虚无",
            //Ashe
            "鲍勃，别傻愣着！","快冲上去，鲍勃！",
            //Bastion
            "堡垒说得对","Do Do Do Do!",
            //Doomfist
            "毁！天！灭！地！！！","铁！拳！强！攻！！！",
            //Echo
            "适应性回路已启动",
            //Genji
            "竜神の剣を喰らえ！","人龙合一！",
            //Hanzo
            "让巨龙吞噬你","竜が我が敌を喰らう",
            //Junkrat
            "女士们先生们，炸弹轮胎滚起来啦！","Fire in the hole!",
            //McCree
            "午时已到","都站好了",
            //Mei
            "冻住，不许走！","冻住，不许走！",
            //Pharah
            "天降正义！","天降正啊啊啊！","火箭弹幕来袭",
            //Reaper
            "正在肃清敌人","死吧！死吧！死吧！",
            //Soldier:76
            "我看到你们了","战术目镜启动",
            //Sombra
            "Apagando las luces!","电磁脉冲启动",
            //Symmetra
            "Yahi param vaastavikta hai!","现实由我的意志来掌控",
            //Torbjorn
            "熔火核心！",
            //Tracer
            "炸弹来咯！",
            //Widowmaker
            "没人可以躲过我的眼睛","一枪，一个",
            //D.Va
            "这太imba了！","正在启动自毁程序！",
            //Reinhardt
            "吃我一锤！！！","吃我一锤！！！",
            //Roadhog
            "少少糖",
            //Orisa
            "放弃抵抗！","武装起来，发动攻势！",
            //Sigma
            "这是什么旋律？","宇宙在向我歌唱！",
            //Winston
            "<猩猩咆哮>",
            //Wrecking Ball
            "制止进入","地雷禁区已部署",
            //Zarya
            "随意开火吧！","Огонь по готовности!"
    ));

    static String pickLines(){
        Random random=new Random();
        return allHeroVoiceLines.get(random.nextInt(allHeroVoiceLines.size()));
    }

    public static void sendHeroLines(MessageEvent event){ event.getSubject().sendMessage(pickLines()); }
}
