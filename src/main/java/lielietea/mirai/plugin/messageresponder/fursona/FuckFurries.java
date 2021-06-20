package lielietea.mirai.plugin.messageresponder.fursona;

import com.google.gson.Gson;
import lielietea.mirai.plugin.messageresponder.mahjong.FortuneTeller;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Random;

public class FuckFurries {
    enum wordType{
        Species,Era,Location,Reason,Action,Color,Adjective1,Adjective2,Tops,Bottoms,Suits,Hats,Bags,Items
    }


    public static Fursona getFursonaJson(){
        final String FURSONA_PATH = "/cluster/fursona.json";
        InputStream is = FortuneTeller.class.getResourceAsStream(FURSONA_PATH);
        assert is != null;
        BufferedReader br =new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        return gson.fromJson(br, Fursona.class);
    }

    public static String createFurryFucker(Fursona furfur, MessageEvent event){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        long datetime = year* 1000L +month*100+date;
        Random random = new Random(event.getSender().getId()+datetime);
        String era = furfur.Action[random.nextInt(furfur.Action.length)];
        String location = furfur.Location[random.nextInt(furfur.Location.length)];
        int random1 = random.nextInt(furfur.Reason.length);
        int random2 =random1;
        while(random1 == random2){
            random2 = random.nextInt(furfur.Reason.length);
        }
        String reason1 =  furfur.Reason[random1];
        String reason2 =  furfur.Reason[random2];
        String action = furfur.Action[random.nextInt(furfur.Action.length)];

        //还没写完


    }

}
