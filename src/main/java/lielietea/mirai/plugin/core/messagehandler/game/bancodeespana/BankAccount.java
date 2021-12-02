package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

import com.google.gson.annotations.SerializedName;

public class BankAccount {
    long ID;
    @SerializedName(value = "pk",alternate = {"pumpkinPesos"}) long pumpkinPesos;
    @SerializedName(value = "ak",alternate = {"akaoni"}) double akaoni;
    @SerializedName(value = "an",alternate = {"antoninianus"}) double antoninianus;
    @SerializedName(value = "ad",alternate = {"adventurers"}) double adventurers;
    @SerializedName(value = "ai",alternate = {"additionalInfo"}) String additionalInfo;

    public BankAccount(long id){
        ID = id;
        pumpkinPesos = 0;
        akaoni = 0;
        antoninianus = 0;
        adventurers = 0;
        additionalInfo = "";
    }
}
