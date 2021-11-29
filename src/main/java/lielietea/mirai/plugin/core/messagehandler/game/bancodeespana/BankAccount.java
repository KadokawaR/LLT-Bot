package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

public class BankAccount {
    long ID;
    long pumpkinPesos;
    double akaoni;
    double antoninianus;
    double adventurers;
    String additionalInfo;


    public BankAccount(long id){
        ID = id;
        pumpkinPesos = 0;
        akaoni = 0;
        antoninianus = 0;
        adventurers = 0;
        additionalInfo = "";
    }
}
