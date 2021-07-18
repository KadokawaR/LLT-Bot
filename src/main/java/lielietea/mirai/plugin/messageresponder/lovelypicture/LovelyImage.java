package lielietea.mirai.plugin.messageresponder.lovelypicture;

import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LovelyImage implements MessageHandler<GroupMessageEvent> {

    public LovelyImage(MessageMatcher<MessageEvent> dogCommandMessageMatcher, MessageMatcher<MessageEvent> shibaCommandMessageMatcher, MessageMatcher<MessageEvent> huskyCommandMessageMatcher, MessageMatcher<MessageEvent> berneseCommandMessageMatcher, MessageMatcher<MessageEvent> malamuteCommandMessageMatcher, MessageMatcher<MessageEvent> germanShepherdCommandMessageMatcher, MessageMatcher<MessageEvent> samoyedCommandMessageMatcher, MessageMatcher<MessageEvent> catCommandMessageMatcher) {
        this.dogCommandMessageMatcher = dogCommandMessageMatcher;
        this.shibaCommandMessageMatcher = shibaCommandMessageMatcher;
        this.huskyCommandMessageMatcher = huskyCommandMessageMatcher;
        this.berneseCommandMessageMatcher = berneseCommandMessageMatcher;
        this.malamuteCommandMessageMatcher = malamuteCommandMessageMatcher;
        this.germanShepherdCommandMessageMatcher = germanShepherdCommandMessageMatcher;
        this.samoyedCommandMessageMatcher = samoyedCommandMessageMatcher;
        this.catCommandMessageMatcher = catCommandMessageMatcher;
        this.executor = Executors.newCachedThreadPool();
    }

    static class ImageSource{
        static final String DOG_CEO_HUSKY = "https://dog.ceo/api/breed/husky/images/random";
        static final String DOG_CEO_BERNESE = "https://dog.ceo/api/breed/mountain/bernese/images/random";
        static final String DOG_CEO_MALAMUTE = "https://dog.ceo/api/breed/malamute/images/random";
        static final String DOG_CEO_GSD = "https://dog.ceo/api/breed/germanshepherd/images/random";
        static final String DOG_CEO_SAMOYED = "https://dog.ceo/api/breed/samoyed/images/random";
        static final String SHIBE_ONLINE_SHIBA = "https://shibe.online/api/shibes";
        static final String SHIBE_ONLINE_CAT = "https://shibe.online/api/cats";
        static final String RANDOM_DOG = "https://random.dog/woof.json";
    }

    final MessageMatcher<MessageEvent> dogCommandMessageMatcher;
    final MessageMatcher<MessageEvent> shibaCommandMessageMatcher;
    final MessageMatcher<MessageEvent> huskyCommandMessageMatcher;
    final MessageMatcher<MessageEvent> berneseCommandMessageMatcher;
    final MessageMatcher<MessageEvent> malamuteCommandMessageMatcher;
    final MessageMatcher<MessageEvent> germanShepherdCommandMessageMatcher;
    final MessageMatcher<MessageEvent> samoyedCommandMessageMatcher;
    final MessageMatcher<MessageEvent> catCommandMessageMatcher;


    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    final ExecutorService executor;

    void getDog(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取狗狗>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.RANDOM_DOG, ImageURLResolver.Source.RADNOM_DOG);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"狗",e);
            }
        });
    }

    void getShiba(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取柴犬>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.SHIBE_ONLINE_SHIBA, ImageURLResolver.Source.SHIBE_ONLINE);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"柴犬",e);
            }
        });
    }

    void getHusky(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取哈士奇>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_HUSKY, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"哈士奇",e);
            }
        });
    }

    void getBernese(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取伯恩山>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_BERNESE, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"伯恩山",e);
            }
        });
    }

    void getMalamute(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取阿拉斯加>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_MALAMUTE, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"阿拉斯加",e);
            }
        });
    }

    void getGSD(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取德牧>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_GSD, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"德牧",e);
            }
        });
    }

    void getSamoyed(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取萨摩耶>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_SAMOYED, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"萨摩耶",e);
            }
        });
    }

    void getCat(GroupMessageEvent event){
        event.getSubject().sendMessage("正在获取猫咪>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.SHIBE_ONLINE_CAT, ImageURLResolver.Source.SHIBE_ONLINE);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                LovelyImage.notifyImageFailToObtain(event,"猫",e);
            }
        });
    }

    public static void notifyImageFailToObtain(GroupMessageEvent event,String type,IOException e){
        //logger.warn("群（"+event.getGroup().getId()+"）"+event.getGroup().getName()+"请求获取"+type+"图，但Bot尝试通过URL获取图片失败",e);
        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("非常抱歉，获取"+type+"图的渠道好像出了一些问题，图片获取失败"));
    }


    @Override
    public boolean handleMessage(GroupMessageEvent event) {
        if(dogCommandMessageMatcher.matches(event)){
            getDog(event);
            return true;
        } else if(shibaCommandMessageMatcher.matches(event)){
            getShiba(event);
            return true;
        } else if(huskyCommandMessageMatcher.matches(event)){
            getHusky(event);
            return true;
        } else if(berneseCommandMessageMatcher.matches(event)){
            getBernese(event);
            return true;
        } else if(malamuteCommandMessageMatcher.matches(event)){
            getMalamute(event);
            return true;
        } else if(germanShepherdCommandMessageMatcher.matches(event)){
            getGSD(event);
            return true;
        } else if(samoyedCommandMessageMatcher.matches(event)){
            getSamoyed(event);
            return true;
        } else if(catCommandMessageMatcher.matches(event)){
            getCat(event);
            return true;
        }
        return false;
    }

    @Override
    public String getFunctionName() {
        return "OK Animal";
    }


    @NotNull
    @Override
    public List<MessageType> types() {
        return type;
    }


    @Override
    public void onclose() {
        executor.shutdown();
    }
}
