package lielietea.mirai.plugin.core.messagehandler.feedback;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.MessageHandler;
import lielietea.mirai.plugin.utils.idchecker.GroupID;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public class FeedBack implements MessageHandler<FriendMessageEvent> {

    static FeedBack INSTANCE;

    public static FeedBack getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public boolean match(FriendMessageEvent event) {
        //TODO:黑名单系统
        return event.getMessage().contentToString().contains("意见反馈");
    }

    @Override
    public MessageChainPackage handle(FriendMessageEvent event) {
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event, this);
        builder.addTask(() -> event.getBot().getGroup(GroupID.DEV).sendMessage("来自" + event.getSubject().getId() + " - " + event.getSubject().getNick() + "的反馈意见：\n\n" + event.getMessage().contentToString()));
        builder.addMessage("您的意见我们已经收到。");
        return builder.build();
    }

    @Override
    public String getName() {
        return "意见反馈";
    }
}
