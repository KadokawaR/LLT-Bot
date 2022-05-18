package lielietea.mirai.plugin.utils.activation.data;

import lielietea.mirai.plugin.utils.multibot.MultiBotHandler;
import net.mamoe.mirai.Bot;

import java.util.Date;

public class GroupEntryRecord {
    public long groupID;
    public long invitorID;
    public MultiBotHandler.BotName botName;
    public long date;

    public GroupEntryRecord(long groupID, long invitorID, Bot bot){
        this.date = new Date().getTime();
        this.groupID = groupID;
        this.invitorID = invitorID;
        this.botName = MultiBotHandler.BotName.get(bot);
    }

    public boolean equalsRecord(GroupEntryRecord record){
        return this.groupID==record.groupID&&this.botName.equals(record.botName);
    }
}
