package lielietea.mirai.plugin.utils.fileutils;

import java.io.BufferedReader;
import java.io.IOException;

public class Read {

    public static String fromReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null) {
            builder.append(temp);
        }
        return builder.toString();
    }

}
