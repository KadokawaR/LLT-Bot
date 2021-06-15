package lielietea.mirai.plugin.messageresponder.feastinghelper.dinnerpicker;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class FoodCluster {
    List<String> foods;
    List<String> foodsWithoutPizza;

    static final String DEFAULT_FOOD_TEXT = "/THUOCL/THUOCL_food.txt";
    static Random rand = new Random();

    public FoodCluster() {
        foods = new ArrayList<>();
        InputStream is = FoodCluster.class.getResourceAsStream(DEFAULT_FOOD_TEXT);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
        String str;
        for(;;){
            try{
                str = br.readLine();
                if(str==null){
                    break;
                }
                str = str.substring(0,str.indexOf("\t"));
                foods.add(str);
            } catch (IOException e){
                e.printStackTrace();
                Logger.getGlobal().warning("读取食品列表失败！");
                break;
            }
        }
        foodsWithoutPizza = new ArrayList<>();
        for(String food:foods){
            if(!food.contains("匹萨")&&!food.contains("比萨"))
                foodsWithoutPizza.add(food);
        }
    }

    static FoodCluster INSTANCE = new FoodCluster();

    public static FoodCluster getINSTANCE() {
        return INSTANCE;
    }


    static void reply(MessageEvent event,Mode mode){
        if(mode==Mode.COMMON){
            event.getSubject().sendMessage(new At(event.getSender().getId()).plus(" "+pickFood()));
        }else if(mode==Mode.PIZZA){
            event.getSubject().sendMessage(new At(event.getSender().getId()).plus(" "+pickPizza()));
        }
    }

    //随机选三种吃的
    static String pickFood(){
        StringBuilder stringBuilder = new StringBuilder();
        int flag = rand.nextInt(2);
        if(flag==0){
            stringBuilder.append("今天要不要尝尝 ");
        } else {
            stringBuilder.append("要不要来点 ");
        }
        List<String> foods = new ArrayList<>();
        for(int i=0;i<3;i++){
            String food = getINSTANCE().foods.get(rand.nextInt(getINSTANCE().foods.size()));
            if(foods.contains(food)){
                i--;
            } else {
                foods.add(food);
            }
        }
        for(String food:foods){
            stringBuilder.append(food);
            if(!food.equals(foods.get(foods.size()-1))){
                stringBuilder.append(" ");
            }
        }
        if(flag==0){
            stringBuilder.append("？");
        } else {
            stringBuilder.append(" 吃吃？");
        }
        return stringBuilder.toString();
    }

    //随机添加3-10项配料
    static String pickPizza(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("您点的 ");
        int ingredientSum = rand.nextInt(8)+3;
        List<String> ingredients = new ArrayList<>();
        for(int i=0;i<ingredientSum;i++){
            String ingredient = getINSTANCE().foodsWithoutPizza.get(rand.nextInt(getINSTANCE().foodsWithoutPizza.size()));
            if(ingredients.contains(ingredient)){
                i--;
            } else {
                ingredients.add(ingredient);
            }
        }
        for(String ingredient:ingredients){
            stringBuilder.append(ingredient);
        }
        stringBuilder.append("披萨 做好了");
        return stringBuilder.toString();
    }

    enum Mode{
        COMMON,
        PIZZA
    }
}
