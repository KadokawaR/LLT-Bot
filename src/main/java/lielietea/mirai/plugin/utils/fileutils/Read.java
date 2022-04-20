package lielietea.mirai.plugin.utils.fileutils;

import lielietea.mirai.plugin.core.game.fish.Fishing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Read {

    public static String fromReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null) {
            builder.append(temp);
        }
        return builder.toString();
    }

    public static String fromFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8))){
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
        }
        return builder.toString();
    }

}
