package lielietea.mirai.plugin.core.game.zeppelin.map;

import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MapGenerator {

    static BufferedImage backgroundImage;
    static final String ZEPPELIN_BG_PATH = "/zeppelin/PirateMap.png";
    static {
        try {
            InputStream is = MapGenerator.class.getResourceAsStream(ZEPPELIN_BG_PATH);
            assert is != null;
            backgroundImage = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage drawAllCities(BufferedImage originalImage){
        Graphics2D g2d = originalImage.createGraphics();
        return null;
    }

    public static BufferedImage drawGeoSystem(BufferedImage originalImage){
        Graphics2D g2d = originalImage.createGraphics();
        return null;
    }

    public static BufferedImage drawActivity(BufferedImage original, ActivityInfo activityInfo){
        return null;
    }

}
