package lielietea.mirao.plugin.utils;

import java.util.Random;

public class Dice {
    public static int roll(int bound){
        return new Random(System.nanoTime()).nextInt(bound)+1;
    }

    public static String getResultString(int bound){
        return String.valueOf(roll(bound));
    }

    public static boolean isDiceCommand(int bound, String input){
        return new StringBuilder("/dice").append(bound).toString().equals(input);
    }

}
