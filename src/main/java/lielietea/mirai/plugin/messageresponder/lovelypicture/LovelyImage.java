package lielietea.mirai.plugin.messageresponder.lovelypicture;

import lielietea.mirai.plugin.messageresponder.CloseRequiredHandler;
import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import lielietea.mirai.plugin.utils.messagematcher.MessageMatcher;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LovelyImage implements MessageHandler<MessageEvent>, CloseRequiredHandler {
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
        static String DOG_CEO_HUSKY = "https://dog.ceo/api/breed/husky/images/random";
        static String DOG_CEO_BERNESE = "https://dog.ceo/api/breed/mountain/bernese/images/random";
        static String DOG_CEO_MALAMUTE = "https://dog.ceo/api/breed/malamute/images/random";
        static String DOG_CEO_GSD = "https://dog.ceo/api/breed/germanshepherd/images/random";
        static String DOG_CEO_SAMOYED = "https://dog.ceo/api/breed/samoyed/images/random";
        static String SHIBE_ONLINE_SHIBA = "https://shibe.online/api/shibes";
        static String SHIBE_ONLINE_CAT = "https://shibe.online/api/cats";
        static String RANDOM_DOG = "https://random.dog/woof.json";
    }

    MessageMatcher<MessageEvent> dogCommandMessageMatcher;
    MessageMatcher<MessageEvent> shibaCommandMessageMatcher;
    MessageMatcher<MessageEvent> huskyCommandMessageMatcher;
    MessageMatcher<MessageEvent> berneseCommandMessageMatcher;
    MessageMatcher<MessageEvent> malamuteCommandMessageMatcher;
    MessageMatcher<MessageEvent> germanShepherdCommandMessageMatcher;
    MessageMatcher<MessageEvent> samoyedCommandMessageMatcher;
    MessageMatcher<MessageEvent> catCommandMessageMatcher;


    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    final ExecutorService executor;

    void getDog(MessageEvent event){
        event.getSubject().sendMessage("正在获取狗狗>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.RANDOM_DOG, ImageURLResolver.Source.RADNOM_DOG);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getShiba(MessageEvent event){
        event.getSubject().sendMessage("正在获取柴犬>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.SHIBE_ONLINE_SHIBA, ImageURLResolver.Source.SHIBE_ONLINE);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getHusky(MessageEvent event){
        event.getSubject().sendMessage("正在获取哈士奇>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_HUSKY, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getBernese(MessageEvent event){
        event.getSubject().sendMessage("正在获取伯恩山>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_BERNESE, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getMalamute(MessageEvent event){
        event.getSubject().sendMessage("正在获取阿拉斯加>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_MALAMUTE, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getGSD(MessageEvent event){
        event.getSubject().sendMessage("正在获取德牧>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_GSD, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getSamoyed(MessageEvent event){
        event.getSubject().sendMessage("正在获取萨摩耶>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.DOG_CEO_SAMOYED, ImageURLResolver.Source.DOG_CEO);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void getCat(MessageEvent event){
        event.getSubject().sendMessage("正在获取猫咪>>>>>>>");
        this.executor.submit(() -> {
            try {
                Optional<URL> url = ImageURLResolver.resolve(ImageSource.SHIBE_ONLINE_CAT, ImageURLResolver.Source.SHIBE_ONLINE);
                url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public boolean handleMessage(MessageEvent event) {
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
    public String getName() {
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
