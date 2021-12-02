package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

import com.google.gson.annotations.SerializedName;

public class BankAccount {
    long ID;
    @SerializedName("pk")
    long pumpkinPesos;
    @SerializedName("ak")
    double akaoni;
    @SerializedName("an")
    double antoninianus;
    @SerializedName("ad")
    double adventurers;
    @SerializedName("ai")
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
