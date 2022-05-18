package lielietea.mirai.plugin.utils.activation.data;

import java.util.ArrayList;
import java.util.List;

public class BotActivationData {

    public List<Long> activatedGroupIDs;
    public List<Long> authorizedUsers;
    public List<GroupEntryRecord> groupEntryRecords;

    BotActivationData(){
        this.activatedGroupIDs = new ArrayList<>();
        this.authorizedUsers = new ArrayList<>();
        this.groupEntryRecords = new ArrayList<>();
    }

    public boolean recordContainsGroup(long groupID){
        for(GroupEntryRecord ger:groupEntryRecords){
            if(ger.groupID==groupID) return true;
        }
        return false;
    }

}
