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


    public void setPumpkinPesos(long pumpkinPesos){
        this.pumpkinPesos = pumpkinPesos;
    }

    public void setAkaoni(double akaoni){
        this.akaoni = akaoni;
    }

    public void setAntoninianus(double antoninianus){
        this.antoninianus = antoninianus;
    }

    public void setAdventurers(double adventurers){
        this.adventurers = adventurers;
    }

    public void setAdditionalInfo(String additionalInfo){
        this.additionalInfo = additionalInfo;
    }

}
