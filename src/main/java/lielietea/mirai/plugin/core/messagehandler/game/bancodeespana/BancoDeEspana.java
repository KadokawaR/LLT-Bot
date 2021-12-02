package lielietea.mirai.plugin.core.messagehandler.game.bancodeespana;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Write;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BancoDeEspana {

    final static String BANK_RECORD_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "bankrecord.json";

    final static File file = new File(BANK_RECORD_PATH);
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();

    public BankRecord bankRecord;

    public static void initialize() throws IOException {
        getINSTANCE().bankRecord = openRecord();
    }

    BancoDeEspana(){}

    private static final BancoDeEspana INSTANCE;
    static {
        INSTANCE = new BancoDeEspana();
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BancoDeEspana getINSTANCE() {
        return INSTANCE;
    }

    //读取记录
    public static BankRecord openRecord() throws IOException {
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(new FileInputStream(BANK_RECORD_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert is != null;
        BufferedReader br = new BufferedReader(is);
        Gson gson = new Gson();
        return gson.fromJson(Read.fromReader(br), BankRecord.class);
    }

    //写入记录
    void writeRecord() throws IOException {
        writeLock.lock();
        try {
            serialize();
        } finally {
            writeLock.unlock();
        }

    }

    //序列化+写入单例
    void serialize() {
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BANK_RECORD_PATH), StandardCharsets.UTF_8));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(getINSTANCE().bankRecord);
        Write.cover(jsonString,BANK_RECORD_PATH);
    }

    //如没有该ID则新建账户
    public static void touchAccount(long ID){
        if (!accountExists(ID)) {
            //BankAccount BA = new BankAccount(ID);
            BankAccount BA = new BankAccount(ID);
            getINSTANCE().bankRecord.bankAccountList.add(BA);
        }
    }

    //检查该ID是否开户
    static boolean accountExists(long ID){
        if (getINSTANCE().bankRecord.bankAccountList == null) return false; else {
            for (BankAccount BA : getINSTANCE().bankRecord.bankAccountList) {
                if (BA.ID == ID) return true;
            }
        }
        return false;

    }

    //向特定账户加减钱
    public void moneyCalculator(Currency kind, int index, double money){
        switch(kind){
            case PumpkinPesos: getINSTANCE().bankRecord.bankAccountList.get(index).pumpkinPesos+=(long)money; return;
            case Akaoni: getINSTANCE().bankRecord.bankAccountList.get(index).akaoni+=money; return;
            case Antoninianus: getINSTANCE().bankRecord.bankAccountList.get(index).antoninianus+=money; return;
            case Adventurers: getINSTANCE().bankRecord.bankAccountList.get(index).adventurers+=money; return;
            case Other: otherCurrencyProtocol(index, money);
        }
    }

    //向特定账户设置特定金额
    public void moneySetter(Currency kind, int index, double money){
        switch(kind){
            case PumpkinPesos: getINSTANCE().bankRecord.bankAccountList.get(index).pumpkinPesos=(long)money; return;
            case Akaoni: getINSTANCE().bankRecord.bankAccountList.get(index).akaoni=money; return;
            case Antoninianus: getINSTANCE().bankRecord.bankAccountList.get(index).antoninianus=money; return;
            case Adventurers: getINSTANCE().bankRecord.bankAccountList.get(index).adventurers=money; return;
            case Other: otherCurrencyProtocol(index, money);
        }
    }


    //判定账户里有没有足够的钱
    boolean hasEnoughMoney(Currency kind, double money, int index){
        switch (kind) {
            case PumpkinPesos:
                return getINSTANCE().bankRecord.bankAccountList.get(index).pumpkinPesos>=(long)money;
            case Akaoni:
                return getINSTANCE().bankRecord.bankAccountList.get(index).akaoni>=money;
            case Antoninianus:
                return getINSTANCE().bankRecord.bankAccountList.get(index).antoninianus>=money;
            case Adventurers:
                return getINSTANCE().bankRecord.bankAccountList.get(index).adventurers>=money;
            case Other:
                return otherCurrencyProtocolCheck(index, money);
        }
        return false;
    }

    //加钱！
    public boolean addMoney(long ID, double money, Currency kind){
        int index = 0;
        touchAccount(ID);
        for ( BankAccount BA : getINSTANCE().bankRecord.bankAccountList){
            if (BA.ID == ID){
                moneyCalculator(kind,index,money);
                //保存记录
                try {
                    writeRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            index += 1;
        }
        return false;
    }

    //扣钱！不够返回false
    public boolean minusMoney(long ID, double money, Currency kind){
        int index = 0;
        touchAccount(ID);
        for ( BankAccount BA : getINSTANCE().bankRecord.bankAccountList){
            if (BA.ID == ID) {
                if (hasEnoughMoney(kind,money,index)){
                    moneyCalculator(kind,index,-money);
                    //保存记录
                    try {
                        writeRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
            index += 1;
        }
        return false;
    }

    //扣钱！不够直接扣光
    public boolean minusMoneyMaybeAllIn(long ID, double money, Currency kind){
        int index = 0;
        touchAccount(ID);
        for ( BankAccount BA : getINSTANCE().bankRecord.bankAccountList){
            if (BA.ID == ID) {
                if (hasEnoughMoney(kind,money,index)){
                    moneyCalculator(kind,index,-money);
                    //保存记录
                    try {
                        writeRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    moneySetter(kind,index,0);
                }
            }
            index += 1;
        }
        return false;
    }

    public void otherCurrencyProtocol(int index, double money){
    }

    public boolean otherCurrencyProtocolCheck(int index, double money){
        return false;
    }
}
