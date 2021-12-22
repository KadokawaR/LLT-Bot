package lielietea.mirai.plugin.core.bank;

import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Write;
import love.marblegate.thevaultnorthkinchovsk.legacyparser.BancoDeEspanaParser;
import love.marblegate.thevaultnorthkinchovsk.vault.BalanceDisplayFormat;
import love.marblegate.thevaultnorthkinchovsk.vault.Currency;
import love.marblegate.thevaultnorthkinchovsk.vault.EasyVault;
import love.marblegate.thevaultnorthkinchovsk.vault.Vault;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.math.BigDecimal;

public class SenoritaCounter {
    final static String BANK_RECORD_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "bankrecord.json";
    private static Vault<Long, Currency> VAULT;

    static{
        // 临时代码，将原来的银行存档先备份为 bankrecord_backup.json
        File backup = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "bankrecord_backup.json");
        if(!backup.exists()){
            try{
                FileReader fileReader = new FileReader(BANK_RECORD_PATH);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String temp =  Read.fromReader(bufferedReader);
                bufferedReader.close();
                Write.cover(temp,System.getProperty("user.dir") + File.separator + "data" + File.separator + "bankrecord_backup.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //然后再将老版本的的转换为新版本
        BancoDeEspanaParser.parse(backup, new File(BANK_RECORD_PATH));

        // 初始化 Vault实例
        VAULT = new EasyVault(new File(BANK_RECORD_PATH), BalanceDisplayFormat.INTEGER);
    }

    //通过ID和kind获得具体数量
    public static BigDecimal getCertainNumber(long ID, Currency kind){
        return VAULT.get(ID,kind);
    }

    //获取格式化的展示数量
    public static String getDisplayNumber(long ID, Currency kind){
        return VAULT.balance(ID,kind);
    }


    public static void go(MessageEvent event){
        PumpkinPesoWindow.checkMoney(event);
        PumpkinPesoWindow.moneyLaundry(event);
    }

    public static Vault<Long, Currency> getVAULT() {
        return VAULT;
    }
}
