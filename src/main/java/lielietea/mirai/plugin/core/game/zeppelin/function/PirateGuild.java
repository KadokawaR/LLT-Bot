package lielietea.mirai.plugin.core.game.zeppelin.function;

import java.util.ArrayList;
import java.util.List;

public class PirateGuild {
    long ID;
    long presidentID;
    String homePortCode;
    List<Long> memberID;

    public PirateGuild(long ID,long presidentID, String homePortCode){
        this.ID=ID;
        this.presidentID=presidentID;
        this.homePortCode=homePortCode;
        this.memberID = new ArrayList<>();
    }
}
