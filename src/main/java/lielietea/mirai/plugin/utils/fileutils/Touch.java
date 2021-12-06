package lielietea.mirai.plugin.utils.fileutils;

import java.io.File;
import java.io.IOException;

public class Touch {

    public static boolean file(String path){
        File file = new File(path);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.exists();
    }

    public static boolean dir(String path){
        File file = new File(path);

        if(!file.exists()){
            file.mkdir();
        }
        return file.exists();
    }

}
