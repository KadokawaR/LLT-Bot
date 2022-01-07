package lielietea.mirai.plugin.core.game.zeppelin.function;

import com.google.gson.Gson;
import lielietea.mirai.plugin.core.bank.PumpkinPesoWindow;
import lielietea.mirai.plugin.core.bank.SenoritaCounter;
import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.AircraftUtils;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.data.ModeInfo;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.Notice;
import lielietea.mirai.plugin.core.game.zeppelin.interaction.UIUtils;
import lielietea.mirai.plugin.core.game.zeppelin.map.CityInfoUtils;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;
import net.mamoe.mirai.event.events.MessageEvent;
import org.junit.Ignore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Shop {

    List<ModeInfo> modeInfoList;

    static final int CHANGE_FEE = Config.CHANGE_FEE;

    static class modeList{
        List<ModeInfo> modes;
        modeList() {this.modes = new ArrayList<>(); }
        modeList(List<ModeInfo> modes){
            this.modes = modes;
        }
    }

    Shop(){
        modeInfoList = new ArrayList<>();
        modeInfoList.addAll(ini());
    }

    static final Shop INSTANCE = new Shop();
    public static Shop getInstance(){return INSTANCE;}

    public static List<ModeInfo> ini(){
        Gson gson = new Gson();
        String PATH = "/zeppelin/AircraftModes.json";
        InputStream is = Shop.class.getResourceAsStream(PATH);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<ModeInfo> res = gson.fromJson(br, modeList.class).modes;
        try {
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void changeAircraft(ModeInfo newMode,long playerID){
        AircraftInfo ai = Aircraft.get(playerID);
        assert ai!=null;
        ai.setAttackFactor(newMode.getAttack());
        ai.setMoneyFactor(newMode.getMoney());
        ai.setSpeedFactor(newMode.getSpeed());
        Aircraft.updateRecord(ai);
    }

    public static ModeInfo getModeInfo(String mode){
        for(ModeInfo mi:getInstance().modeInfoList){
            if (mi.getCode().equals(mode)) return mi;
        }
        return null;
    }

    public static boolean isInCorrectTown(String mode, String cityCode){
        if(!existsMode(mode)) return false;
        String manufacturer = Objects.requireNonNull(getModeInfo(mode)).getCompany();
        if(Arrays.asList("SGP","NKC","WHN").contains(cityCode)) return true;
        switch(manufacturer){
            case "KHA":
                return Arrays.asList("KHA","VLA").contains(cityCode);
            case "INU":
                return Arrays.asList("TKY","SPR").contains(cityCode);
            case "TSF":
                return Arrays.asList("URU","LNZ").contains(cityCode);
        }
        return false;
    }

    public static boolean existsMode(String mode){
        for(ModeInfo mi: getInstance().modeInfoList){
            if(mi.getCode().equals(mode)) return true;
        }
        return false;
    }

    public static String activity(MessageEvent event){
        long playerID = event.getSender().getId();
        if(!Aircraft.exist(playerID)) return(Notice.NOT_REGISTERED);

        Coordinate coord = Objects.requireNonNull(Aircraft.get(playerID)).getCoordinate();
        if(!CityInfoUtils.isInCity(coord)) return Notice.NOT_IN_CITY;

        String mode = event.getMessage().contentToString().toUpperCase();
        mode = UIUtils.deleteKeywords(mode, Arrays.asList("/CHANGESHIP","更换飞艇"));
        if(!existsMode(mode)) return Notice.ILLEGAL_MODE;

        String cityCode = CityInfoUtils.getCityCode(coord);
        if(!isInCorrectTown(mode,cityCode)) return Notice.NOT_IN_TARGET_CITY;

        if(Activity.isInActivity(playerID)) return Notice.IS_IN_ACTIVITY;

        if(!PumpkinPesoWindow.hasEnoughMoney(event,CHANGE_FEE)) return Notice.DOESNT_HAVE_ENOUGH_MONEY;

        PumpkinPesoWindow.minusMoney(event,CHANGE_FEE);
        changeAircraft(Objects.requireNonNull(getModeInfo(mode)),playerID);
        return "已成功切换您的飞艇至"+ Objects.requireNonNull(getModeInfo(mode)).getCompany()+"-"+mode+"，花费"+CHANGE_FEE+"南瓜比索。";
    }

}
