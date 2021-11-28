package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

import lielietea.mirai.plugin.administration.AdminTools;
import lielietea.mirai.plugin.utils.IdentityUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class SenoritaCounter extends BankAccountOperator{

    public static void checkMoney(MessageEvent event){
        if (event.getMessage().contentToString().equals("/bank")||event.getMessage().contentToString().equals("查询余额")){
            BancoDeEspana.getINSTANCE().touchAccount(event.getSender().getId());
            MessageChainBuilder mcb = new MessageChainBuilder();

            if (event.getClass().equals(GroupMessageEvent.class)){
                mcb.append((new At(event.getSender().getId())));
            }

            mcb.append("您的余额为：\n\n");
            mcb.append("南瓜比索：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.PumpkinPesos))).append("\n");
            mcb.append("Akaoni：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Akaoni))).append("\n");
            mcb.append("Antoninianus：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Antoninianus))).append("\n");
            mcb.append("Adventurer's：").append(String.valueOf(getCertainNumber(event.getSender().getId(), Currency.Adventurers)));
            event.getSubject().sendMessage(mcb.asMessageChain());
        }
    }

    public static void moneyLaundry(MessageEvent event){
        if ((IdentityUtil.isAdmin(event))&&(event.getMessage().contentToString().contains("/laundry "))){
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
    }

    public static void go(MessageEvent event){
        checkMoney(event);
        moneyLaundry(event);
    }
}
