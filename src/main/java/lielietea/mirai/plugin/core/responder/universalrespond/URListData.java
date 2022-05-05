package lielietea.mirai.plugin.core.responder.universalrespond;

import lielietea.mirai.plugin.core.responder.universalrespond.respondenum.ListKind;
import lielietea.mirai.plugin.core.responder.universalrespond.respondenum.UserKind;

import java.util.ArrayList;
import java.util.List;

public class URListData {

    ListKind listKind;
    UserKind userKind;
    List<Long> IDList;

    URListData(ListKind listKind,UserKind userKind,Long ID){
        this.IDList=new ArrayList<Long>(){{add(ID);}};
        this.listKind=listKind;
        this.userKind=userKind;
    }

}
