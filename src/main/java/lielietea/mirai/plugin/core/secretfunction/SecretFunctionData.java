package lielietea.mirai.plugin.core.secretfunction;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.List;

public class SecretFunctionData {

    List<Long> antiWithdraw;
    List<Long> repeater;

    SecretFunctionData(){
        this.antiWithdraw = new ArrayList<>();
        this.repeater = new ArrayList<>();
    }

    public boolean canDoRepeater(GroupMessageEvent event){
        return this.repeater.contains(event.getGroup().getId());
    }

    public boolean canDoRepeater(long groupID){
        return this.repeater.contains(groupID);
    }

    public boolean canDoAntiWithdraw(GroupMessageEvent event){
        return this.antiWithdraw.contains(event.getGroup().getId());
    }

    public boolean canDoAntiWithdraw(long groupID){
        return this.antiWithdraw.contains(groupID);
    }

}
