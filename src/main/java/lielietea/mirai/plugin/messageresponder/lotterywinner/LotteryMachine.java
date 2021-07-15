package lielietea.mirai.plugin.messageresponder.lotterywinner;

import lielietea.mirai.plugin.utils.Notice;
import lielietea.mirai.plugin.utils.image.ImageCreater;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LotteryMachine {
    static final Timer timer = new Timer(true);
    static final Map<Long,Boolean> c4ActivationFlags = new HashMap<>();
    static final Random rand = new Random();

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
                LotteryMachine.c4ActivationFlags.clear();
            }
        },
                date,
                24 * 60 * 60 * 1000);
    }

    public static boolean botPermissionChecker(GroupMessageEvent event){
        return ((event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR))||(event.getGroup().getBotPermission().equals(MemberPermission.OWNER)));
    }

    public static boolean senderPermissionChecker(GroupMessageEvent event){
        return ((event.getSender().getPermission().equals(MemberPermission.ADMINISTRATOR))||(event.getSender().getPermission().equals(MemberPermission.OWNER)));
    }

    public static void okBummer(GroupMessageEvent event) {
        if (botPermissionChecker(event)) {
            //抽取倒霉蛋
            List<NormalMember> candidates = event.getGroup().getMembers().stream().filter(normalMember -> normalMember.getPermission().equals(MemberPermission.MEMBER)).collect(Collectors.toList());
            NormalMember victim = candidates.get(rand.nextInt(candidates.size()));

            //禁言倒霉蛋
            //顺便把发送者禁言了

            //try {
            victim.mute(120);
            //如果发送者不是管理员，那么发送者也将被禁言
            if (!(senderPermissionChecker(event))) {
                event.getSender().mute(120);
            }
            //} catch (PermissionDeniedException e) {
            //logger.error("禁言失败，群（"+event.getGroup().getId()+"）"+event.getGroup().getName()+"尝试发起OK Bummer功能，但该群并未授予Bot管理员权限！",e);
            //} finally {
            if (victim.getId() == event.getSender().getId()) {
                event.getGroup().sendMessage("Ok Bummer! " + victim.getNick() + "\n" +
                        event.getSender().getNick() + "尝试随机极限一换一。他成功把自己换出去了！");
            } else if ((senderPermissionChecker(event))) {
                //如果发送者是管理员，那么提示
                event.getGroup().sendMessage(new PlainText("Ok Bummer! " + victim.getNick() + "\n管理员")
                        .plus(new At(event.getSender().getId()))
                        .plus(new PlainText(" 随机带走了 "))
                        .plus(new At(victim.getId())));
            } else {
                //如果发送者不是管理员，那么提示
                event.getGroup().sendMessage(new PlainText("Ok Bummer! " + victim.getNick() + "\n")
                        .plus(new At(event.getSender().getId()))
                        .plus(new PlainText(" 以自己为代价随机带走了 "))
                        .plus(new At(victim.getId())));
            }
        }
        //}
        else {
            event.getGroup().sendMessage(Notice.BOT_NO_ADMIN_PERMISSION);
        }
    }
    //}

    public static void okWinner(GroupMessageEvent event) throws IOException {

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
        ImageCreater.sendImage(ImageCreater.createWinnerImage(candidates.get(Math.toIntExact(guyOfTheDay))),event);
    }

    public static void okC4(GroupMessageEvent event){
        if (botPermissionChecker(event)){
            if(!c4ActivationFlags.containsKey(event.getGroup().getId())){
                c4ActivationFlags.put(event.getGroup().getId(),false);
            }
            if (!c4ActivationFlags.get(event.getGroup().getId())){
                double ratio = 1D/Math.sqrt(event.getGroup().getMembers().size());

                if (rand.nextDouble()<ratio){
                    //禁言全群
                    try {
                        event.getGroup().getSettings().setMuteAll(true);
                    } catch (PermissionDeniedException e) {
                        //logger.error("禁言失败，群（"+event.getGroup().getId()+"）"+event.getGroup().getName()+"尝试发起OK C4功能，但该群并未授予Bot管理员权限！",e);
                    } finally {
                        event.getGroup().sendMessage("中咧！");
                        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("成功触发了C4！大家一起恭喜TA！"));
                        //设置C4在该群已经触发
                        c4ActivationFlags.put(event.getGroup().getId(),true);
                    }

                    //设置5分钟后解禁
                    timer.schedule(new TimerTask() {
                        public void run() {
                            try {
                                event.getGroup().getSettings().setMuteAll(false);
                            } catch (PermissionDeniedException e) {
                                //logger.error("解除全群禁言失败，BOT在群（"+event.getGroup().getId()+"）"+event.getGroup().getName()+"中尝试解除因OK C4产生的全群禁言，但该群并未授予Bot管理员权限！",e);
                            }
                        }
                    }, 300000);

                }
                else{
                    event.getGroup().sendMessage(new At(event.getSender().getId()).plus("没有中！"));
                }
            }
        } else {
            event.getGroup().sendMessage(Notice.BOT_NO_ADMIN_PERMISSION);
        }
    }
}