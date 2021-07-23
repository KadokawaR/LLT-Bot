package lielietea.mirai.plugin.core.broadcast.foodie;

import com.google.gson.Gson;
import lielietea.mirai.plugin.utils.json.JsonFile;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

public class FoodieUtil {
    static class Foodie{
        String image;
    }


    public static String getJsonUrlFoodie(String urlPath) throws Exception{
        Gson gson = new Gson();
        Foodie fd = gson.fromJson(JsonFile.read(urlPath), Foodie.class);
        return fd.image;
    }

    public static void sendFoodieImage(MessageEvent event,String urlPath) throws Exception {
        event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), JsonFile.getInputStream(getJsonUrlFoodie(urlPath))));

    }


}

