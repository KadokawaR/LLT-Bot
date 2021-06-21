package lielietea.mirai.plugin.messageresponder.getsomedogs;

import lielietea.mirai.plugin.messageresponder.CloseRequiredHandler;
import lielietea.mirai.plugin.messageresponder.MessageHandler;
import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DogImage implements MessageHandler<MessageEvent>, CloseRequiredHandler {
    static class ImageSource{
        static String DOG_CEO_RANDOM = "https://dog.ceo/api/breeds/image/random";
        static String DOG_CEO_SHIBA = "https://dog.ceo/api/breed/shiba/images/random";
        static String DOG_CEO_HUSKY = "https://dog.ceo/api/breed/husky/images/random";
        static String SHIBE_ONLINE = "https://shibe.online/api/shibes";
        static String RANDOM_DOG = "https://random.dog/woof.json";
        static String PLACE_DOG = "https://place.dog/300/200";
    }

    static final List<MessageType> type = new ArrayList<>(Collections.singletonList(MessageType.GROUP));

    ExecutorService executor;

    public DogImage() {
        this.executor = Executors.newCachedThreadPool();
    }

    public boolean getShiba(MessageEvent event){
        //匹配词为 "[Oo]k [Ss]hiba" "/[Ss]hiba" "柴犬" "柴柴"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Shiba")) {
            this.executor.submit(() -> {
                try {
                    Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breed/shiba/images/random", ImageURLResolver.Source.DOG_CEO);
                    url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    public boolean getHusky(MessageEvent event){
        //匹配词为 "[Oo]k [Hh]usky" "/[Hh]usky" "二哈" "哈士奇"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Husky")) {
            this.executor.submit(() -> {
                try {
                    Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breed/husky/images/random", ImageURLResolver.Source.DOG_CEO);
                    url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    public boolean getRandomDog(MessageEvent event){
        //匹配词为 "[Oo]k [Dd]oggie" "/[Dd]og[s ]" "狗狗" “来点狗”
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Dog")) {
            this.executor.submit(() -> {
                try {
                    Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breeds/image/random", ImageURLResolver.Source.DOG_CEO);
                    url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }


    @Override
    public boolean handleMessage(MessageEvent event) {
        //测试功能，先这么写，定下了具体怎么搞后再说
        return getHusky(event) || getShiba(event) || getRandomDog(event);
    }

    @Override
    public String getName() {
        return "OK Doggie";
    }

    @Override
    public boolean isOnBeta() {
        return true;
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
