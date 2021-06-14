package lielietea.mirai.plugin.utils.idchecker;

import lielietea.mirai.plugin.viponly.GrandVIPServiceDepartment;
import net.mamoe.mirai.event.events.MessageEvent;

public class VIPChecker  implements IdentityChecker<MessageEvent>{
    @Override
    public boolean checkIdentity(MessageEvent event) {
        for(GrandVIPServiceDepartment.VIP vip: GrandVIPServiceDepartment.VIP.values()){
            if(vip.matches(event.getSender().getId())){
                return true;
            }
        }
        return false;
    }
}
