package lielietea.mirai.plugin.core.messagehandler;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageChainPackage {
    final Contact source;
    final Contact sender;
    final List<Object> action = new ArrayList<>();
    final String handlerName;
    final UUID handlerUUID;
    Contact target;
    String note;

    MessageChainPackage(MessageEvent source, MessageChain message, MessageHandler messageHandler) {
        this.source = source.getSubject();
        sender = source.getSender();
        this.target = source.getSubject();
        action.add(message);
        handlerName = messageHandler.getName();
        handlerUUID = messageHandler.getUUID();
    }

    MessageChainPackage(MessageEvent source, MessageHandler messageHandler) {
        this.source = source.getSubject();
        sender = source.getSender();
        this.target = source.getSubject();
        handlerName = messageHandler.getName();
        handlerUUID = messageHandler.getUUID();
    }

    public static MessageChainPackage getDefaultImpl(MessageEvent source, String message, MessageHandler messageHandler) {
        return new MessageChainPackage(source, new MessageChainBuilder().append(message).build(), messageHandler);
    }

    public static MessageChainPackage getDefaultImpl(MessageEvent source, MessageChain message, MessageHandler messageHandler) {
        return new MessageChainPackage(source, message, messageHandler);
    }

    public void execute() {
        for (Object obj : action) {
            if (obj instanceof MessageChain) {
                target.sendMessage((MessageChain) obj);
            } else if (obj instanceof Runnable) {
                ((Runnable) obj).run();
            }
        }
    }

    public Contact getSender() {
        return sender;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public UUID getHandlerUUID() {
        return handlerUUID;
    }

    public String getNote() {
        return note;
    }

    public Contact getSource() {
        return source;
    }

    public static class Builder {
        final MessageChainPackage onBuild;

        public Builder(MessageEvent event, MessageHandler messageHandler) {
            onBuild = new MessageChainPackage(event, messageHandler);
        }

        public Builder changeTarget(Contact target) {
            onBuild.target = target;
            return this;
        }

        public Builder addMessage(String message) {
            onBuild.action.add(new MessageChainBuilder().append(message).build());
            return this;
        }

        public Builder addMessage(MessageChain message) {
            onBuild.action.add(message);
            return this;
        }

        public Builder addTask(Runnable task) {
            onBuild.action.add(task);
            return this;
        }

        public Builder addNote(String note) {
            if (onBuild.note == null)
                onBuild.note = note;
            else {
                StringBuilder builder = new StringBuilder(onBuild.note);
                onBuild.note = builder.append(note).toString();
            }
            return this;
        }

        public MessageChainPackage build() {
            return onBuild;
        }
    }

}
