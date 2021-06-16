package lielietea.mirai.plugin.messageresponder.getsomedogs;

import net.mamoe.mirai.event.events.MessageEvent;

public class OkDoggie {
    public static void getShiba(MessageEvent event){
        //匹配词为 "[Oo]k [Ss]hiba" "/[Ss]hiba" "柴犬" "柴柴"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Shiba")) {
            String urlPath = null;
            try {
                urlPath = GetImage.getJsonUrlSubDog("https://dog.ceo/api/breed/shiba/images/random/alt");
            } catch (Exception e) {
                e.printStackTrace();
            }
            GetImage.sendImage(event, urlPath);
        }
    }

    public static void getHusky(MessageEvent event){
        //匹配词为 "[Oo]k [Hh]usky" "/[Hh]usky" "二哈" "哈士奇"
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Husky")) {
            String urlPath = null;
            try {
                urlPath = GetImage.getJsonUrlSubDog("https://dog.ceo/api/breed/husky/images/random/alt");
            } catch (Exception e) {
                e.printStackTrace();
            }
            GetImage.sendImage(event, urlPath);
        }
    }

    public static void getRandomDog(MessageEvent event){
        //匹配词为 "[Oo]k [Dd]oggie" "/[Dd]og[s ]" "狗狗" “来点狗”
        //现在先临时使用一下
        if (event.getMessage().contentToString().contains("Dog")) {
            String urlPath = null;
            try {
                urlPath = GetImage.getJsonUrlDog("https://dog.ceo/api/breeds/image/random");
            } catch (Exception e) {
                e.printStackTrace();
            }
            GetImage.sendImage(event, urlPath);
        }
    }

    public static void sendDoggie(MessageEvent event){
        getHusky(event);
        getShiba(event);
        getRandomDog(event);
    }
}
