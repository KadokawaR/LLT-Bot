package lielietea.mirai.plugin.utils.idchecker;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 检测某个账号是否为管理员
 */
public class AdministrativeAccountChecker implements IdentityChecker<MessageEvent> {
    static final List<Long> adminList = new ArrayList<>(Arrays.asList(
            2955808839L, //KADOKAWA
            1811905537L //MARBLEGATE
    ));

    @Override
    public boolean checkIdentity(MessageEvent event) {
        for (Long qqID : adminList) {
            if (qqID == event.getSender().getId()) return true;
        }
        return false;
    }
}
