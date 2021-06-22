package lielietea.mirai.plugin.utils.image;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class ImageURLResolver {
    public static Optional<URL> resolve(String urlString, Source source) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()));
        //获取来自dog.ceo的狗
        if(source==Source.DOG_CEO){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JsonObject jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
            if(jsonObject.getAsJsonPrimitive("status").getAsString().equals("success")){
                return Optional.of(new URL(jsonObject.getAsJsonPrimitive("message").toString().replaceAll("\"","")));
            }
            return Optional.empty();
        }
        //获得来自shiba.online的狗
        else if(source==Source.SHIBE_ONLINE){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return Optional.of(new URL(JsonParser.parseString(sb.toString()).getAsJsonArray().get(0).toString().replaceAll("\"","")));
        }
        else if(source==Source.RADNOM_GOD){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return Optional.of(new URL(JsonParser.parseString(sb.toString()).getAsJsonObject().getAsJsonPrimitive("url").toString().replaceAll("\"","")));
        }
        //获得来自place.dog的狗，默认尺寸为300x200
        else{
            return Optional.of(new URL("https://place.dog/300/200"));
        }
    }

    public enum Source {
        DOG_CEO,
        //https://dog.ceo/
        SHIBE_ONLINE,
        //https://shibe.online/
        RADNOM_GOD,
        //https://random.dog/
        PLACE_DOG
        //https://place.dog/

    }
}
