package lielietea.mirai.plugin.utils.activation;

public class ActivationRecord {
    long group;
    long user;

    ActivationRecord(long group,long user){
        this.group=group;
        this.user=user;
    }
}
