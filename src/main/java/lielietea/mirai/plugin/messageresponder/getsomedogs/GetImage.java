package lielietea.mirai.plugin.messageresponder.getsomedogs;

import com.google.gson.Gson;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImage {

    public static String getJsonFile(String urlPath) throws Exception {
        URL url = new URL(urlPath);
        BufferedReader reader = new BufferedReader
                (new InputStreamReader(url.openStream()));
        String line;
        String jsonstring = "";
        while ((line = reader.readLine()) != null) {
            jsonstring = jsonstring + line;
        }
        reader.close();
        return jsonstring;
    }

    public static String getJsonUrlSubDog(String urlPath) throws Exception{
        Gson gson = new Gson();
        SubDog subdog = gson.fromJson(getJsonFile(urlPath), SubDog.class);
        if (subdog.status.equals("success")) {
            return subdog.message[0];
        }
        return null;
    }
    public static String getJsonUrlDog(String urlPath) throws Exception {
        Gson gson = new Gson();
        Dog dog = gson.fromJson(getJsonFile(urlPath), Dog.class);
        if (dog.status.equals("success")) {
            return dog.message;
        }
        return null;
    }

    public static void sendImage(MessageEvent event,String urlPath){
        event.getSubject().sendMessage(net.mamoe.mirai.contact.Contact.uploadImage(event.getSubject(), getInputStream(urlPath)));

    }


    public static InputStream getInputStream(String urlPath) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlPath);
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(3000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                // 从服务器返回一个输入流
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}