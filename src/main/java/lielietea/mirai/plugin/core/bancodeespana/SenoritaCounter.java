package lielietea.mirai.plugin.core.bancodeespana;

import lielietea.mirai.plugin.administration.statistics.GameCenterCount;
import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class SenoritaCounter {

    //获得该ID的index
    public static Integer getIndex(long ID){
        int index = 0;
        for ( BankAccount BA: BancoDeEspana.getINSTANCE().bankRecord.bankAccountList){
            if (BA.ID==ID){
                return index;
            }
            index += 1;
        }
        return null;
    }

    //通过ID和kind获得具体数量
    public static Double getCertainNumber(long ID, Currency kind){
        Integer index = getIndex(ID);
        if (index!=null){
            switch (kind) {
                case PumpkinPesos:
                    return (double)BancoDeEspana.getINSTANCE().bankRecord.bankAccountList.get(index).pumpkinPesos;
                case Akaoni:
                    return BancoDeEspana.getINSTANCE().bankRecord.bankAccountList.get(index).akaoni;
                case Antoninianus:
                    return BancoDeEspana.getINSTANCE().bankRecord.bankAccountList.get(index).antoninianus;
                case Adventurers:
                    return BancoDeEspana.getINSTANCE().bankRecord.bankAccountList.get(index).adventurers;
                case Other:
                    return null;
            }
        }
        return null;
    }


    public static void checkMoney(MessageEvent event){
        if (event.getMessage().contentToString().equals("/bank")||event.getMessage().contentToString().equals("查询余额")){
            BancoDeEspana.touchAccount(event.getSender().getId());
            MessageChainBuilder mcb = new MessageChainBuilder();

            GameCenterCount.count(GameCenterCount.Functions.BankCheck);

            if (event.getClass().equals(GroupMessageEvent.class)){
                mcb.append((new At(event.getSender().getId())));
            }

            mcb.append("您的余额为");
            mcb.append(" ").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.PumpkinPesos))).append(" 南瓜比索");
            //mcb.append("Akaoni：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Akaoni))).append("\n");
            //mcb.append("Antoninianus：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Antoninianus))).append("\n");
            //mcb.append("Adventurer's：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Adventurers)));
            event.getSubject().sendMessage(mcb.asMessageChain());
            return;
        }

        if(IdentityUtil.isAdmin(event)&&event.getMessage().contentToString().contains("/bank ")){
            String message = event.getMessage().contentToString();
            String[] messageSplit = message.split(" ");
            if(messageSplit.length!=2){
                event.getSubject().sendMessage("查询格式错误。");
                return;
            }
            Double money = getCertainNumber(Long.parseLong(messageSplit[1]), Currency.PumpkinPesos);
            if(money==null){
                event.getSubject().sendMessage("未能查询该用户");
            } else {
                event.getSubject().sendMessage("该用户的银行余额是"+money+"南瓜比索。");
            }
        }
    }

    public static void moneyLaundry(MessageEvent event){
        if(!IdentityUtil.isAdmin(event)) return;
        if (event.getMessage().contentToString().contains("/laundry ")){
            //如果有负号就是扣钱了
            if (event.getMessage().contentToString().contains("-")){
                String amount = event.getMessage().contentToString().replace("/laundry -", "");
                long amountL = Long.parseLong(amount);
                if(!BancoDeEspana.getINSTANCE().minusMoney(event.getSender().getId(),amountL,Currency.PumpkinPesos)){
                    event.getSubject().sendMessage("操作失败！");
                }
                //不然就是加钱
            } else {
                String amount = event.getMessage().contentToString().replace("/laundry ", "");
                long amountL = Long.parseLong(amount);
                if (!BancoDeEspana.getINSTANCE().addMoney(event.getSender().getId(), amountL, Currency.PumpkinPesos)) {
                    event.getSubject().sendMessage("操作失败！");
                }
            }
        }

        if(event.getMessage().contentToString().contains("/set ")){
            String message = event.getMessage().contentToString();
            String[] messageSplit = message.split(" ");
            if(messageSplit.length!=3){
                event.getSubject().sendMessage("设置金额失败。");
                return;
            }
            BancoDeEspana.getINSTANCE().moneySetter(Currency.PumpkinPesos,Long.parseLong(messageSplit[1]),Double.parseDouble(messageSplit[2]));
            event.getSubject().sendMessage("已设置成功。");
        }

    }

    public static boolean hasEnoughMoney(MessageEvent event, int money){
        Double amount = getCertainNumber(event.getSender().getId(),Currency.PumpkinPesos);
        if (amount!=null) {
            return (amount >= money);
        } else return false;
    }

    public static void minusMoney(MessageEvent event, int money){
        BancoDeEspana.getINSTANCE().minusMoney(event.getSender().getId(),money,Currency.PumpkinPesos);
    }

    public static void minusMoney(long ID, int money){
        BancoDeEspana.getINSTANCE().minusMoney(ID,money,Currency.PumpkinPesos);
    }

    public static void minusMoneyMaybeAllIn(MessageEvent event, int money){
        BancoDeEspana.getINSTANCE().minusMoneyMaybeAllIn(event.getSender().getId(),money,Currency.PumpkinPesos);
    }

    public static void minusMoneyMaybeAllIn(long ID, int money){
        BancoDeEspana.getINSTANCE().minusMoneyMaybeAllIn(ID,money,Currency.PumpkinPesos);
    }

    public static void addMoney(MessageEvent event, int money){
        BancoDeEspana.getINSTANCE().addMoney(event.getSender().getId(),money,Currency.PumpkinPesos);
    }

    public static void addMoney(long ID, int money){
        BancoDeEspana.getINSTANCE().addMoney(ID,money,Currency.PumpkinPesos);
    }

    public static void go(MessageEvent event){
        checkMoney(event);
        moneyLaundry(event);
    }
}
