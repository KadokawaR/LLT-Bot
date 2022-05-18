package lielietea.mirai.plugin.utils.activation.data;

import net.mamoe.mirai.Bot;

import java.util.ArrayList;
import java.util.List;

public class ActivationData {

    public List<ActivationDataOfSingleBot> activationDataList;

    public ActivationData(){
        this.activationDataList = new ArrayList<>();
    }

    public ActivationDataOfSingleBot getBotsData(Bot bot){
        for(ActivationDataOfSingleBot adsb: this.activationDataList){
            if(adsb.botName.equalsBot(bot)) return adsb;
        }
        return null;
    }

}
