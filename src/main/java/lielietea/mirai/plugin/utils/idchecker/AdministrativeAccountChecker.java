package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测某个账号是否为管理员
 */
public class AdministrativeAccountChecker implements IdentityChecker<MessageEvent> {
    static final List<Long> adminList = new ArrayList<>(List.of(
            2955808839L,
            1811905537L
    ));

    @Override
    public boolean checkIdentity(MessageEvent event) {
        for(Long qqID : adminList){
            if(qqID == event.getSender().getId()) return true;
        }
        return false;
    }
}
