package lielietea.mirai.plugin.admintools.blacklist;

import lielietea.mirai.plugin.admintools.Operation;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.List;

class BlacklistOperation {

    static abstract class SpecificBlacklistOperation implements Operation {
        MessageEvent event;
        long id;
        boolean isGroup;

        public SpecificBlacklistOperation(MessageEvent event, long id, boolean isGroup) {
            this.event = event;
            this.id = id;
            this.isGroup = isGroup;
        }
    }

    //添加对象到黑名单
    static class AddToBlacklist extends SpecificBlacklistOperation {
        String reason;

        public AddToBlacklist(MessageEvent event, long id, boolean isGroup, String reason) {
            super(event, id, isGroup);
            this.reason = reason;
        }


        @Override
        public void execute() {
            boolean success = BlacklistManager.getInstance().add(id, reason, isGroup);
            if (success) {
                BlacklistManager.getInstance().saveBlackList();
                event.getSubject().sendMessage("已成功将" + (isGroup ? "群 " : "用户 ") + id + " 添加到黑名单中。");
            } else {
                event.getSubject().sendMessage((isGroup ? "群 " : "用户 ") + id + " 已在黑名单中，信息如下：\n" + BlacklistManager.getInstance().getSpecificInform(id, isGroup));
            }
        }
    }

    static class RemoveFromBlacklist extends SpecificBlacklistOperation {

        public RemoveFromBlacklist(MessageEvent event, long id, boolean isGroup) {
            super(event, id, isGroup);
        }

        @Override
        public void execute() {
            boolean success = BlacklistManager.getInstance().remove(id, isGroup);
            if (success) {
                BlacklistManager.getInstance().saveBlackList();
                event.getSubject().sendMessage("已成功将" + (isGroup ? "群 " : "用户 ") + id + " 从黑名单中移除。");
            } else {
                event.getSubject().sendMessage((isGroup ? "群 " : "用户 ") + id + " 并不在黑名单中。");
            }
        }
    }


    static class EditBlackListNote extends SpecificBlacklistOperation {
        String note;
        boolean override;

        public EditBlackListNote(MessageEvent event, long id, boolean isGroup, String note, boolean override) {
            super(event, id, isGroup);
            this.note = note;
            this.override = override;
        }


        @Override
        public void execute() {
            boolean success = BlacklistManager.getInstance().editNote(id, note, isGroup, override);
            if (success) {
                BlacklistManager.getInstance().saveBlackList();
                event.getSubject().sendMessage("已成功为" + (isGroup ? "群 " : "用户 ") + id + (override ? " 修改" : " 添加") + "备注内容。更新后信息如下：\n" + BlacklistManager.getInstance().getSpecificInform(id, isGroup));
            } else {
                event.getSubject().sendMessage((isGroup ? "群 " : "用户 ") + id + " 并不在黑名单中。");
            }
        }
    }

    static class ResetBanReason extends SpecificBlacklistOperation {
        String reason;

        public ResetBanReason(MessageEvent event, long id, boolean isGroup, String reason) {
            super(event, id, isGroup);
            this.reason = reason;
        }

        @Override
        public void execute() {
            boolean success = BlacklistManager.getInstance().setReason(id, reason, isGroup);
            if (success) {
                BlacklistManager.getInstance().saveBlackList();
                event.getSubject().sendMessage("已成功修改" + (isGroup ? "群 " : "用户 ") + id + " 的封禁原因。更新后信息如下：\n" + BlacklistManager.getInstance().getSpecificInform(id, isGroup));
            } else {
                event.getSubject().sendMessage((isGroup ? "群 " : "用户 ") + id + " 并不在黑名单中。");
            }
        }
    }


    static class SearchInBlackList extends SpecificBlacklistOperation {

        public SearchInBlackList(MessageEvent event, long id, boolean isGroup) {
            super(event, id, isGroup);
        }

        @Override
        public void execute() {
            event.getSubject().sendMessage(BlacklistManager.getInstance().getSpecificInform(id, isGroup));
        }
    }

    static class GetAllInBlackList implements Operation {
        MessageEvent event;
        boolean isGroup;
        boolean detailed;

        public GetAllInBlackList(MessageEvent event, boolean isGroup, boolean detailed) {
            this.event = event;
            this.isGroup = isGroup;
            this.detailed = detailed;
        }


        @Override
        public void execute() {
            if (detailed) event.getSubject().sendMessage(BlacklistManager.getInstance().getAllInform(isGroup));
            else {
                List<String> contents = BlacklistManager.getInstance().getAllDetailedInform(isGroup);
                for (String string : contents) {
                    event.getSubject().sendMessage(string);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    static class ReloadBlackList implements Operation {
        MessageEvent event;

        public ReloadBlackList(MessageEvent event) {
            this.event = event;
        }

        @Override
        public void execute() {
            BlacklistManager.getInstance().reloadBlackList();
            event.getSubject().sendMessage("黑名单重载完成。");
        }
    }
}
