package lielietea.mirai.plugin.core.game.zeppelin.data;

import java.util.ArrayList;
import java.util.List;

public class PirateGuildInfo {
    private long presidentID;
    private List<Long> memberIDList;

    PirateGuildInfo(long presidentID){
        this.presidentID = presidentID;
        this.memberIDList = new ArrayList<>();
    }

    public long getPresidentID() {
        return presidentID;
    }

    public void setPresidentID(long presidentID) {
        this.presidentID = presidentID;
    }

    public List<Long> getMemberIDList() {
        return memberIDList;
    }

    public void setMemberIDList(List<Long> memberIDList) {
        this.memberIDList = memberIDList;
    }
}
