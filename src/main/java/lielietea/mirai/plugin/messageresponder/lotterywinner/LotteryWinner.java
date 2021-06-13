package lielietea.mirai.plugin.messageresponder.lotterywinner;

import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.security.Permission;
import java.util.*;
import java.util.stream.Collectors;

public class LotteryWinner{
    public static boolean botPermissionChecker(GroupMessageEvent event){
        return ((event.getGroup().getBotPermission().equals(MemberPermission.OWNER))||(event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR)));
    }

    public static boolean senderPermissionChecker(GroupMessageEvent event){
        return ((event.getSender().getPermission().equals(MemberPermission.OWNER))||(event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR)));
    }

    public static void okBummer(GroupMessageEvent event){
        //Iterator<NormalMember> test = event.getGroup().getMembers().stream().iterator();
        List<NormalMember> testa = event.getGroup().getMembers().stream().filter(normalMember -> normalMember.getPermission().equals(MemberPermission.MEMBER)).collect(Collectors.toList());
        int random = new Random().nextInt(testa.size());
        event.getGroup().sendMessage("Ok Bummer! "+testa.get(random).getNick());
        if (botPermissionChecker(event)) {
            testa.get(random).mute(300);
            if (!senderPermissionChecker(event)) {
                event.getSender().mute(300);
            }
        }
        else{
            event.getGroup().sendMessage("Bot没有足够高的权限，请授予Bot管理员权限");
        }
    }

    public static void okWinner(GroupMessageEvent event){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        long numOfTheDay = (year+month*10000+date*1000000)*100000000000L/event.getGroup().getId();
        List<NormalMember> testa = event.getGroup().getMembers().stream().collect(Collectors.toList());
        long guyOfTheDay = numOfTheDay % testa.size();
        event.getGroup().sendMessage("Ok Winner! "+testa.get(Math.toIntExact(guyOfTheDay)).getNick());
    }

    static boolean boomed = false;
    public static void okC4(GroupMessageEvent event){
        if (!boomed){
            List<NormalMember> testa = event.getGroup().getMembers().stream().collect(Collectors.toList());

            int random = new Random().nextInt(testa.size());
            if (testa.get(random).getPermission().equals(MemberPermission.OWNER)){
                event.getGroup().sendMessage("中咧！");
                event.getGroup().getSettings().setMuteAll(true);
                event.getGroup().getSettings().setMuteAll(false);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        event.getGroup().getSettings().setMuteAll(true);
                    }
                }, 600000);

                boomed=false;
            }
            else{
                event.getGroup().sendMessage("没有中！");
            }
        }
    }
}