package lielietea.mirai.plugin.overwatch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

// 在研究Gson的序列化和反序列化
public class HeroLines {
    public class Hero{
        public String name;
        public ArrayList<String> lines;
    }
    public void fromJson(){
        Gson object = new Gson();
        Hero hero = object.fromJson("overwatch/herolines.json", Hero.class);
    }
}
