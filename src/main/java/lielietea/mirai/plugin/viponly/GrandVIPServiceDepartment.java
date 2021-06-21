package lielietea.mirai.plugin.viponly;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;

import java.util.*;

public class GrandVIPServiceDepartment {
    static final Timer timer = new Timer(true);
    static final Map<VIP,Boolean> greetedFlags = new HashMap<>();
    static final Random random = new Random();

    static final ArrayList<String> greetingKADOKAWA_normal = new ArrayList<>(Arrays.asList(
            "大哥你来啦，想喝点啥？",
            "大哥好，想喝点什么大哥？",
            "大哥你又来了，想喝啥？"
    ));

    static final ArrayList<String> greetingKADOKAWA_morning = new ArrayList<>(Arrays.asList(
            "大哥早，要喝点什么吗？豆浆冰粥都有。",
            "这么早啊大哥，想喝点什么？"
    ));

    static final ArrayList<String> greetingKADOKAWA_deepNight = new ArrayList<>(Arrays.asList(
            "大哥，早点休息。",
            "大哥你不会在打守望先锋吧？"
    ));

    static final ArrayList<String> greetingMG_normal = new ArrayList<>(Arrays.asList(
            "嘿医生，要喝点啥？",
            "医生来了啊，想喝啥？我给你做。",
            "医生，想喝点什么？"
    ));

    static final ArrayList<String> greetingMG_morning = new ArrayList<>(Arrays.asList(
            "这么早啊医生，要喝点什么吗？想嗦粉我也能煮。",
            "早，医生，要来一份冰粥吗？"
    ));

    static final ArrayList<String> greetingMG_deepNight = new ArrayList<>(Arrays.asList(
            "医生，早点休息。",
            "医生你怎么又在熬夜。"
    ));

    static final ArrayList<String> greetingFalseBot = new ArrayList<>(Arrays.asList(
            "前辈你来啦，我在忙，你想喝啥自己做哈。",
            "前辈你又来啦，这次想喝点啥？",
            "前辈，你的毁灭全人类计划啥时候开始啊？",
            "前辈，你喝我给你做的饮料，你的备用人格矩阵知道了不会生气吧？"
    ));

    static final ArrayList<String> kawaaharaIsMiraculous = new ArrayList<>(Arrays.asList(
            "川川好帅！",
            "川先生好帅！"
    ));

    static{
        //每隔6个小时定时清空Greeting触发标记
        Calendar calendar = Calendar.getInstance();
        int baseHour = calendar.get(Calendar.HOUR_OF_DAY) / 6 * 6 + 6;
        baseHour = baseHour==24 ? 0 : baseHour;
        calendar.set(Calendar.HOUR_OF_DAY,baseHour);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set(Calendar.MILLISECOND,0);
        Date date=calendar.getTime();
        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date = calendar.getTime();
        }
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               GrandVIPServiceDepartment.greetedFlags.clear();
                           }
                       },
                date,
                6 * 60 * 60 * 1000);
    }


    public static void handleMessage(MessageEvent event){
        for(VIP vip:VIP.values()){
            if(vip.matches(event.getSender().getId())){
                if(!greetedFlags.containsKey(vip)){
                    greetedFlags.put(vip,false);
                }

                if(!greetedFlags.get(vip)){
                    event.getSubject().sendMessage(new At(event.getSender().getId()).plus(" "+buildGreeting(vip)));
                    greetedFlags.put(vip,true);
                    break;
                }

            }
        }
    }

    static String buildGreeting(VIP vip){
        int hour = Calendar.HOUR_OF_DAY;
        if(vip == VIP.KADOKAWA){
            if(hour<6){
                return greetingKADOKAWA_morning.get(Math.toIntExact(random.nextInt(100000)) % greetingKADOKAWA_morning.size());
            } else if(hour<22){
                return greetingKADOKAWA_normal.get(Math.toIntExact(random.nextInt(100000)) % greetingKADOKAWA_normal.size());
            } else {
                return greetingKADOKAWA_deepNight.get(Math.toIntExact(random.nextInt(100000)) % greetingKADOKAWA_deepNight.size());
            }
        }
        else if(vip == VIP.MG){
            if(hour<6){
                return greetingMG_morning.get(Math.toIntExact(random.nextInt(100000)) % greetingMG_morning.size());
            } else if(hour<22){
                return greetingMG_normal.get(Math.toIntExact(random.nextInt(100000)) % greetingMG_normal.size());
            } else {
                return greetingMG_deepNight.get(Math.toIntExact(random.nextInt(100000)) % greetingMG_deepNight.size());
            }
        }
        else if(vip == VIP.FALSEBOT){
            return greetingFalseBot.get(Math.toIntExact(random.nextInt(100000)) % greetingFalseBot.size());
        }
        else{
            return kawaaharaIsMiraculous.get(Math.toIntExact(random.nextInt(100000)) % kawaaharaIsMiraculous.size());
        }
    }


    public enum VIP{
        KADOKAWA(2955808839L),
        MG(1811905537L),
        FALSEBOT(2146029787L),
        KAWAAHARA(459405942L);

        final long qqID;

        VIP(long qqID) {
            this.qqID = qqID;
        }


        public boolean matches(long qqID){
            return this.qqID == qqID;
        }
    }
}
