package lielietea.mirai.plugin.utils.multibot.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    FunctionConfig friendFC;
    FunctionConfig groupFC;
    ResponseConfig rc;

    public Config(){
        this.friendFC = new FunctionConfig();
        this.groupFC = new FunctionConfig();
        this.rc = new ResponseConfig();
    }

    public ResponseConfig getRc() {
        return rc;
    }

    public void setRc(ResponseConfig rc) {
        this.rc = rc;
    }

    public void setAnswerFriend(boolean answerFriend){
        this.getRc().setAnswerFriend(answerFriend);
    }

    public void setAddFriend(boolean addFriend){
        this.getRc().setAddFriend(addFriend);
    }

    public void setAddGroup(boolean addGroup){
        this.getRc().setAddGroup(addGroup);
    }

    public void setAnswerGroup(boolean answerGroup){
        this.getRc().setAnswerGroup(answerGroup);
    }

    public void setAutoAnswer(boolean autoAnswer){
        this.getRc().setAutoAnswer(autoAnswer);
    }


    public FunctionConfig getFriendFC() {
        return friendFC;
    }

    public void setFriendFC(FunctionConfig friendFC) {
        this.friendFC = friendFC;
    }

    public FunctionConfig getGroupFC() {
        return groupFC;
    }

    public void setGroupFC(FunctionConfig groupFC) {
        this.groupFC = groupFC;
    }
}




