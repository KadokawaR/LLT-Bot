package lielietea.mirai.plugin.core.messagehandler.responder.mahjong;

import lielietea.mirai.plugin.core.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FortuneTeller implements MessageResponder<MessageEvent> {

    static final List<MessageResponder.MessageType> types = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));

    public static int getMahjongOfTheDay(MessageEvent event) {
        //获取当日幸运数字
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        long datetime = year * 1000L + month * 100 + date;
        Random random = new Random(event.getSender().getId() + datetime);
        return random.nextInt(144);
    }

    public static String getMahjong(int mahjongOfTheDay) {
        ArrayList<String> chineseNum = new ArrayList<>(Arrays.asList(
                "一", "二", "三", "四", "五", "六", "七", "八", "九"
        ));
        ArrayList<String> fengXiang = new ArrayList<>(Arrays.asList(
                "東", "南", "西", "北"
        ));
        ArrayList<String> zhongFaBai = new ArrayList<>(Arrays.asList(
                "红中", "發财", "白板"
        ));
        ArrayList<String> huaPai = new ArrayList<>(Arrays.asList(
                "春", "夏", "秋", "冬", "梅", "兰", "竹", "菊"
        ));
        int mahjongNumero;
        if (mahjongOfTheDay < 36) {
            mahjongNumero = mahjongOfTheDay % 9;
            return (chineseNum.get(mahjongNumero) + "筒");
        } else if (mahjongOfTheDay < 72) {
            mahjongNumero = mahjongOfTheDay % 9;
            return (chineseNum.get(mahjongNumero) + "条");
        } else if (mahjongOfTheDay < 108) {
            mahjongNumero = mahjongOfTheDay % 9;
            return (chineseNum.get(mahjongNumero) + "萬");
        } else if (mahjongOfTheDay < 124) {
            mahjongNumero = mahjongOfTheDay % 4;
            return (fengXiang.get(mahjongNumero) + "风");
        } else if (mahjongOfTheDay < 136) {
            mahjongNumero = mahjongOfTheDay % 3;
            return (zhongFaBai.get(mahjongNumero));
        } else {
            mahjongNumero = mahjongOfTheDay - 136;
            return ("花牌（" + huaPai.get(mahjongNumero) + "）");
        }
    }

    public static String whatDoesMahjongSay(MessageEvent event) {
        int mahjongOfTheDay = getMahjongOfTheDay(event);
        int mahjongNumero;
        if (mahjongOfTheDay < 36) {
            mahjongNumero = mahjongOfTheDay % 9;
        } else if (mahjongOfTheDay < 72) {
            mahjongNumero = (mahjongOfTheDay - 36) % 9 + 9;
        } else if (mahjongOfTheDay < 108) {
            mahjongNumero = (mahjongOfTheDay - 72) % 9 + 18;

        } else if (mahjongOfTheDay < 124) {
            mahjongNumero = (mahjongOfTheDay - 108) % 4 + 27;
        } else if (mahjongOfTheDay < 136) {
            mahjongNumero = (mahjongOfTheDay - 124) % 3 + 31;
        } else {
            mahjongNumero = Math.toIntExact(mahjongOfTheDay) - 102;
        }
        return "\n今天的占卜麻将牌是: " + getMahjong(mahjongOfTheDay) + "\n运势是: " + MahjongSay.luck.get(mahjongNumero) + "\n" + MahjongSay.saying.get(mahjongNumero);
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return types;
    }

    @Override
    public boolean match(MessageEvent event) {
        return event.getMessage().contentToString().equals("麻将") || event.getMessage().contentToString().contains("求签");
    }


    @Override
    public MessageChainPackage handle(MessageEvent event) {
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event, this);
        builder.addTask(() -> {
            final String mahjongPicPath = "/pics/mahjong/" + getMahjong(getMahjongOfTheDay(event)) + ".png";
            try (InputStream img = FortuneTeller.class.getResourceAsStream(mahjongPicPath)) {
                assert img != null;
                event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), img));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        builder.addMessage(new At(event.getSender().getId()).plus(whatDoesMahjongSay(event)));
        return builder.build();
    }

    @Override
    public String getName() {
        return "麻将占卜";
    }
}
