package lielietea.mirai.plugin.core.messagehandler;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageChainPackage {
    final Contact sender;
    Contact target;
    final List<Object> action = new ArrayList<>();
    final String handlerName;
    final UUID handlerUUID;
    String note;

    public void execute(){
        for(Object obj:action){
            if(obj instanceof MessageChain){
                target.sendMessage((MessageChain) obj);
            } else if(obj instanceof Runnable){
                ((Runnable) obj).run();
            }
        }
    }

    MessageChainPackage(MessageEvent source,MessageChain message,MessageHandler messageHandler){
        sender = source.getSender();
        this.target = source.getSubject();
        action.add(message);
        handlerName = messageHandler.getName();
        handlerUUID = messageHandler.getUUID();
    }

    MessageChainPackage(MessageEvent source,MessageHandler messageHandler){
        sender = source.getSender();
        this.target = source.getSender();
        handlerName = messageHandler.getName();
        handlerUUID = messageHandler.getUUID();
    }

    public static MessageChainPackage getDefaultImpl(MessageEvent source,String message,MessageHandler messageHandler){
        return new MessageChainPackage(source,new MessageChainBuilder().append(message).build(),messageHandler);
    }

    public static MessageChainPackage getDefaultImpl(MessageEvent source,MessageChain message,MessageHandler messageHandler){
        return new MessageChainPackage(source,message,messageHandler);
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

    public static class Builder{
        final MessageChainPackage onBuild;

        public Builder(MessageEvent event,MessageHandler messageHandler) {
            onBuild = new MessageChainPackage(event,messageHandler);
        }

        public Builder changeTarget(Contact target){
            onBuild.target = target;
            return this;
        }

        public Builder addMessage(String message){
            onBuild.action.add(new MessageChainBuilder().append(message).build());
            return this;
        }

        public Builder addMessage(MessageChain message){
            onBuild.action.add(message);
            return this;
        }

        public Builder addTask(Runnable task){
            onBuild.action.add(task);
            return this;
        }

        public Builder addNote(String note){
            if(onBuild.note==null)
                onBuild.note=note;
            else{
                StringBuilder builder = new StringBuilder(onBuild.note);
                onBuild.note = builder.append(note).toString();
            }
            return this;
        }

        public MessageChainPackage build(){
            return onBuild;
        }
    }

}
