package lielietea.mirai.plugin.core.messagehandler.responder.lovelypicture;

import lielietea.mirai.plugin.core.dispatcher.MessageDispatcher;
import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

class AnimalImagePusher implements Runnable {
    GroupMessageEvent event;
    String imageSource;
    ImageURLResolver.Source uRLResolver;
    String type;

    public AnimalImagePusher(GroupMessageEvent event, String imageSource, String type, ImageURLResolver.Source uRLResolver) {
        this.event = event;
        this.imageSource = imageSource;
        this.type = type;
        this.uRLResolver = uRLResolver;
    }

    @Override
    public void run() {
        MessageChainPackage.Builder builder = new MessageChainPackage.Builder(event,LovelyImage.INSTANCE);
        try {
            Optional<URL> url = ImageURLResolver.resolve(imageSource, uRLResolver);
            url.ifPresent(url1 -> builder.addTask(() -> ImageSender.sendImageFromURL(event.getSubject(), url1)));
        } catch (IOException e) {
            builder.addMessage("非常抱歉，获取"+type+"图的渠道好像出了一些问题，图片获取失败");
            builder.addNote(e.toString());
        } finally{
            //发送给MessageDispatcher去处理
            MessageDispatcher.getINSTANCE().handleMessageChainPackage(builder.build());
        }

    }
}
