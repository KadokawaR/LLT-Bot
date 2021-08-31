package lielietea.mirai.plugin.core.messagehandler.feedback;

import lielietea.mirai.plugin.core.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.MessageHandler;
import lielietea.mirai.plugin.utils.MessageUtil;
import net.mamoe.mirai.event.events.MessageEvent;

public class FeedBack implements MessageHandler<MessageEvent> {

    static FeedBack INSTANCE = new FeedBack();

    public static FeedBack getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public boolean match(MessageEvent event) {
        //TODO:黑名单系统
        return event.getMessage().contentToString().contains("意见反馈");
    }

    @Override
    public MessageChainPackage handle(MessageEvent event) {
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event, this);
        builder.addTask(() -> MessageUtil.notifyDevGroup("来自" + event.getSender().getId() + " - " + event.getSenderName() + "的反馈意见：\n\n" + event.getMessage().contentToString()));
        builder.addMessage("您的意见我们已经收到。");
        return builder.build();
    }

    @Override
    public String getName() {
        return "意见反馈";
    }

}
