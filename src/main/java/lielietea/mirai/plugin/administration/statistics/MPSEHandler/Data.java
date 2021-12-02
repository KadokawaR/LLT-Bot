package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import java.util.Date;

public class Data {
    int friendMessage;
    int groupMessage;
    int failedMessage;
    Date date;

    Data(){
        this.friendMessage = 0;
        this.groupMessage = 0;
        this.failedMessage = 0;
        date = null;
    }

    Data(Date date){
        this.friendMessage = 0;
        this.groupMessage = 0;
        this.failedMessage = 0;
        this.date = date;
    }

    Data(Date date, int friendMessage, int groupMessage, int failedMessage){
        this.friendMessage = friendMessage;
        this.groupMessage = groupMessage;
        this.failedMessage = failedMessage;
        this.date = date;
    }

    Data(int friendMessage, int groupMessage, int failedMessage){
        this.friendMessage = friendMessage;
        this.groupMessage = groupMessage;
        this.failedMessage = failedMessage;
        this.date = null;
    }
}
