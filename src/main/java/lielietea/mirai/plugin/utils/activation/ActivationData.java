package lielietea.mirai.plugin.utils.activation;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;
import java.util.List;

public class ActivationData {
    List<Long> activatedGroupID;
    List<Long> userPermissionList;
    List<ActivationRecord> recordList;

    ActivationData(){
        this.activatedGroupID = new ArrayList<>();
        this.userPermissionList = new ArrayList<>();
        this.recordList = new ArrayList<>();
    }
}
