package lielietea.mirai.plugin.admintools.blacklist;

import lielietea.mirai.plugin.admintools.Operation;
import net.mamoe.mirai.console.command.Command;

import java.util.Date;
import java.util.Optional;

class CommandParser {
    public static Optional<Command> parse(String command){
        //TODO 没写完，先随便丢个占位
        return Optional.empty();
    }


    static abstract class SpecificBlacklistOperation implements Operation{
        long id;
    }

    public static class AddToBlacklist extends SpecificBlacklistOperation {
        String reason;
        boolean isGroup;

        public AddToBlacklist(long id, String reason, boolean isGroup) {
            this.id = id;
            this.reason = reason;
            this.isGroup = isGroup;
        }

        @Override
        public void execute() {
            if(isGroup) BlacklistManager.getInstance().blockedGroup.add(new BlockedContact(id,reason,new Date()));
            else BlacklistManager.getInstance().blockedUser.add(new BlockedContact(id,reason,new Date()));
            BlacklistManager.getInstance().saveBlackList();
        }
    }

    public static class RemoveFromBlacklist extends SpecificBlacklistOperation{
        long id;
        boolean isGroup;

        public RemoveFromBlacklist(long id, boolean isGroup) {
            this.isGroup = isGroup;
        }

        @Override
        public void execute() {
            boolean flag = false;
            if(isGroup) flag = BlacklistManager.getInstance().removeGroup(id);
            else flag = BlacklistManager.getInstance().removeUser(id);
            if(flag){
                BlacklistManager.getInstance().saveBlackList();
                //TODO 播报移除成功
            } else {
                //TODO 播报移除失败
            }
        }
    }


    public static class EditBlackListNote extends SpecificBlacklistOperation{
        String note;
        boolean override;
        boolean isGroup;
        @Override
        public void execute() {
            //TODO BlackListManager缺个方法，不过今天先写到这里
        }
    }


    public static class SearchInBlackList extends SpecificBlacklistOperation{
        boolean isGroup;

        public SearchInBlackList(long id, boolean isGroup) {
            this.id = id;
            this.isGroup = isGroup;
        }

        @Override
        public void execute() {
            String result = BlacklistManager.getInstance().getSpecificInfomation(id,isGroup);
            //TODO 根据情况播报
        }
    }

    public static class ReloadBlackList implements Operation{
        @Override
        public void execute() {
            BlacklistManager.getInstance().reloadBlackList();
        }
    }
}
