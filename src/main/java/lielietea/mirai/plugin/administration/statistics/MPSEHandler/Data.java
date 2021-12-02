package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import java.util.Date;

public class Data {
    private int friendMessage;
    private int groupMessage;
    private int failedMessage;
    private Date date;

    Data(){
        this.friendMessage = 0;
        this.groupMessage = 0;
        this.failedMessage = 0;
        this.date = null;
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

    public int getFriendMessage() {
        return friendMessage;
    }

    public void setFriendMessage(int friendMessage) {
        this.friendMessage = friendMessage;
    }

    public int getGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(int groupMessage) {
        this.groupMessage = groupMessage;
    }

    public int getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(int failedMessage) {
        this.failedMessage = failedMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
