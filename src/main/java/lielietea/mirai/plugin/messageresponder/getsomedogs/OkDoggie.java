package lielietea.mirai.plugin.messageresponder.getsomedogs;

import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class OkDoggie {
    public static void getShiba(MessageEvent event){
        //匹配词为 "[Oo]k [Ss]hiba" "/[Ss]hiba" "柴犬" "柴柴"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Shiba")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breed/shiba/images/random", ImageURLResolver.Source.SHIBA_ONLINE);
                        url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void getHusky(MessageEvent event){
        //匹配词为 "[Oo]k [Hh]usky" "/[Hh]usky" "二哈" "哈士奇"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Husky")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breed/husky/images/random", ImageURLResolver.Source.SHIBA_ONLINE);
                        url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void getRandomDog(MessageEvent event){
        //匹配词为 "[Oo]k [Dd]oggie" "/[Dd]og[s ]" "狗狗" “来点狗”
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Dog")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Optional<URL> url = ImageURLResolver.resolve("https://dog.ceo/api/breeds/image/random", ImageURLResolver.Source.SHIBA_ONLINE);
                        url.ifPresent(url1 -> ImageSender.sendImageFromURL(event.getSubject(), url1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void sendDoggie(MessageEvent event){
        getHusky(event);
        getShiba(event);
        getRandomDog(event);
    }
}
