package lielietea.mirai.plugin.utils.activation;

import lielietea.mirai.plugin.utils.StandardTimeUtil;

import java.util.Date;

public class EnterGroupRecord {
    long groupID;
    Date date;

    EnterGroupRecord(long groupID){
        this.groupID = groupID;
        this.date = new Date();
    }

    public boolean outOfDate(int day,int hour, int min, int second){
        return (new Date().getTime()-this.date.getTime())>= StandardTimeUtil.getPeriodLengthInMS(day,hour,min,second);
    }

    public boolean outOfDate(){
        return outOfDate(3,0,0,0);
    }

    //todo 增加 deactivate 的删除好友+群聊的功能
    //todo 增加群聊和用户的黑名单判定
    //todo 增加滚动检测退群功能
}
