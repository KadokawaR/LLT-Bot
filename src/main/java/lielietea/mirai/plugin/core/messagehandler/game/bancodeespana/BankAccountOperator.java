package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

public class BankAccountOperator {

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



}
