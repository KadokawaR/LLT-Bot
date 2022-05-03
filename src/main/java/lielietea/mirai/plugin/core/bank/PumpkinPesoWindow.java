package lielietea.mirai.plugin.core.bank;

import lielietea.mirai.plugin.administration.statistics.GameCenterCount;
import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;

import java.math.BigDecimal;

public class PumpkinPesoWindow {
    public static void checkMoney(MessageEvent event,String message) {
        if (message.equals("/bank") || message.equals("查询余额")) {
            MessageChainBuilder mcb = new MessageChainBuilder();

            if (event.getClass().equals(GroupMessageEvent.class)) {
                mcb.append((new At(event.getSender().getId())));
            }

            GameCenterCount.count(GameCenterCount.Functions.BankCheck);

            mcb.append("您的余额为");
            mcb.append(" ").append(String.valueOf(SenoritaCounter.getDisplayNumber(event.getSender().getId(), Currencies.PUMPKIN_PESO))).append(" 南瓜比索");
            event.getSubject().sendMessage(mcb.asMessageChain());
            return;
        }

        if (event instanceof GroupMessageEvent) {
            if (message.toLowerCase().startsWith("/bank") && message.contains("@")) {
                if (IdentityUtil.isAdmin(event)) {
                    for (SingleMessage sm : event.getMessage()) {
                        String SMM = sm.contentToString();
                        if (SMM.startsWith("@")) {
                            long ID = Long.parseLong(SMM.replace("@", ""));
                            String money = SenoritaCounter.getDisplayNumber(ID, Currencies.PUMPKIN_PESO);
                            if (money == null) {
                                event.getSubject().sendMessage("未能查询该用户");
                            } else {
                                MessageChainBuilder mcb = new MessageChainBuilder().append(new At(ID))
                                        .append("的余额是")
                                        .append(money)
                                        .append("南瓜比索。");
                                event.getSubject().sendMessage(mcb.asMessageChain());
                            }
                            return;
                        }
                    }
                }
            }
        }

        if (IdentityUtil.isAdmin(event) && message.toLowerCase().startsWith("/bank ")) {
            String[] messageSplit = message.split(" ");
            if (messageSplit.length != 2) {
                event.getSubject().sendMessage("查询格式错误。");
                return;
            }
            String money = SenoritaCounter.getDisplayNumber(Long.parseLong(messageSplit[1]), Currencies.PUMPKIN_PESO);
            if (money == null) {
                event.getSubject().sendMessage("未能查询该用户");
            } else {
                event.getSubject().sendMessage("该用户的余额是" + money + "南瓜比索。");
            }
        }
    }

    public static void moneyLaundry(MessageEvent event,String message) {
        if (!IdentityUtil.isAdmin(event)) return;

        if (message.startsWith("/laundry ")) {
            //如果有负号就是扣钱了
            if (message.contains("-")) {
                String amount = message.replace("/laundry -", "");
                BigDecimal amountBD = new BigDecimal(amount);
                minusMoney(event.getSender().getId(), amountBD);
                //不然就是加钱
            } else {
                String amount = message.replace("/laundry ", "");
                BigDecimal amountBD = new BigDecimal(amount);
                addMoney(event.getSender().getId(), amountBD);
            }
            return;
        }

        if (message.toLowerCase().startsWith("/set")) {

            if(message.contains(" @")) message = message.replace(" @","@");

            String[] messageSplit = message.split(" |@");

            if (messageSplit.length != 3) {
                event.getSubject().sendMessage("设置金额失败。");
                return;
            }

            if (event instanceof GroupMessageEvent) {
                if (message.contains("@")) {
                    setMoney(Long.parseLong(messageSplit[1].replace("@","")), new BigDecimal(messageSplit[2]));
                }
            }

            setMoney(Long.parseLong(messageSplit[1]), new BigDecimal(messageSplit[2]));
            event.getSubject().sendMessage("已设置成功。");
        }

    }

    public static boolean hasEnoughMoney(MessageEvent event, int money) {
        return hasEnoughMoney(event, new BigDecimal(money));
    }

    public static boolean hasEnoughMoney(MessageEvent event, BigDecimal money) {
        BigDecimal amount = SenoritaCounter.getCertainNumber(event.getSender().getId(), Currencies.PUMPKIN_PESO);
        return amount.compareTo(money) >= 0;
    }

    public static boolean hasEnoughMoney(long playerID, int money) {
        BigDecimal amount = SenoritaCounter.getCertainNumber(playerID, Currencies.PUMPKIN_PESO);
        return amount.compareTo(new BigDecimal(money)) >= 0;
    }

    public static void setMoney(long ID, int money) {
        setMoney(ID, new BigDecimal(money));
    }

    public static void setMoney(long ID, BigDecimal money) {
        SenoritaCounter.getVAULT().set(ID, Currencies.PUMPKIN_PESO, money);
    }

    public static void minusMoney(MessageEvent event, int money) {
        minusMoney(event.getSender().getId(), money);
    }

    public static void minusMoney(long ID, int money) {
        minusMoney(ID, new BigDecimal(money));
    }

    public static void minusMoney(long ID, BigDecimal money) {
        SenoritaCounter.getVAULT().set(ID, Currencies.PUMPKIN_PESO, SenoritaCounter.getVAULT().get(ID, Currencies.PUMPKIN_PESO).subtract(money));
    }

    public static void minusMoneyMaybeAllIn(MessageEvent event, int money) {
        minusMoneyMaybeAllIn(event.getSender().getId(), money);
    }

    public static void minusMoneyMaybeAllIn(long ID, int money) {
        minusMoneyMaybeAllIn(ID, new BigDecimal(money));
    }

    public static void minusMoneyMaybeAllIn(long ID, BigDecimal money) {
        boolean tryWithdraw = SenoritaCounter.getVAULT().withdraw(ID, Currencies.PUMPKIN_PESO, money);
        if (!tryWithdraw)
            SenoritaCounter.getVAULT().set(ID, Currencies.PUMPKIN_PESO, BigDecimal.ZERO);
    }

    public static void addMoney(MessageEvent event, int money) {
        addMoney(event.getSender().getId(), money);
    }

    public static void addMoney(long ID, int money) {
        addMoney(ID, new BigDecimal(money));
    }

    public static void addMoney(long ID, BigDecimal money) {
        SenoritaCounter.getVAULT().set(ID, Currencies.PUMPKIN_PESO, SenoritaCounter.getVAULT().get(ID, Currencies.PUMPKIN_PESO).add(money));
    }
}