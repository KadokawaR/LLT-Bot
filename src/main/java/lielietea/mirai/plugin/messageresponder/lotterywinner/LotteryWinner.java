package lielietea.mirai.plugin.messageresponder.lotterywinner;

import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LotteryWinner{
    static Timer timer = new Timer();
    static Map<Long,Boolean> c4ActivationFlags = new HashMap<>();

    static{
        //每日6点定时清空C4触发标记
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,6);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Date date=calendar.getTime();
        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date = calendar.getTime();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LotteryWinner.c4ActivationFlags.clear();
            }
        },
                date,
                24 * 60 * 60 * 1000);
    }

    public static boolean botPermissionChecker(GroupMessageEvent event){
        return ((event.getGroup().getBotPermission().equals(MemberPermission.OWNER))||(event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR)));
    }

    public static boolean senderPermissionChecker(GroupMessageEvent event){
        return ((event.getSender().getPermission().equals(MemberPermission.OWNER))||(event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR)));
    }

    public static void okBummer(GroupMessageEvent event) {
        //抽取倒霉蛋
        List<NormalMember> candidates = event.getGroup().getMembers().stream().filter(normalMember -> normalMember.getPermission().equals(MemberPermission.MEMBER)).collect(Collectors.toList());
        int random = new Random().nextInt(candidates.size());

        //禁言倒霉蛋
        try {
            if (botPermissionChecker(event)) {
                candidates.get(random).mute(300);
                if (!senderPermissionChecker(event)) {
                    event.getSender().mute(300);
                }
            }
        } catch (PermissionDeniedException e) {
            e.printStackTrace();
            Logger.getGlobal().warning("禁言失败，没有管理员权限！");
        } finally {
            event.getGroup().sendMessage("Ok Bummer! " + candidates.get(random).getNick());
        }
    }


    public static void okWinner(GroupMessageEvent event){

        //获取当日幸运数字
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        long numOfTheDay = (year+month*10000+date*1000000)*100000000000L/event.getGroup().getId();

        //获取当日幸运儿
        List<NormalMember> candidates = new ArrayList<>(event.getGroup().getMembers());
        long guyOfTheDay = numOfTheDay % candidates.size();

        //广播结果
        event.getGroup().sendMessage("Ok Winner! "+candidates.get(Math.toIntExact(guyOfTheDay)).getNick());
    }

    public static void okC4(GroupMessageEvent event){
        if (!c4ActivationFlags.get(event.getGroup().getId())){
            List<NormalMember> candidates = new ArrayList<>(event.getGroup().getMembers());

            int random = new Random().nextInt(candidates.size());
            if (candidates.get(random).getPermission().equals(MemberPermission.OWNER)){
                //禁言全群
                try {
                    event.getGroup().getSettings().setMuteAll(true);
                } catch (PermissionDeniedException e) {
                    e.printStackTrace();
                    Logger.getGlobal().warning("全群禁言失败，没有管理员权限！");
                } finally {
                    event.getGroup().sendMessage("中咧！");
                    //设置C4在该群已经触发
                    c4ActivationFlags.put(event.getGroup().getId(),true);
                }

                //设置10分钟后解禁
                timer.schedule(new TimerTask() {
                    public void run() {
                        try {
                            event.getGroup().getSettings().setMuteAll(false);
                        } catch (PermissionDeniedException e) {
                            e.printStackTrace();
                            Logger.getGlobal().warning("解除全群禁言失败，没有管理员权限！");
                        }
                    }
                }, 600000);

            }
            else{
                event.getGroup().sendMessage("没有中！");
            }
        }
    }
}