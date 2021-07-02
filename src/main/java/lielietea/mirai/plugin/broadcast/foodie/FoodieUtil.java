package lielietea.mirai.plugin.broadcast.foodie;

import com.google.gson.Gson;
import lielietea.mirai.plugin.utils.json.JsonFile;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodieUtil {
    class Foodie{
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

