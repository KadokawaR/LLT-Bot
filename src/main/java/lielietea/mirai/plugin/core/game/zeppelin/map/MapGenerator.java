package lielietea.mirai.plugin.core.game.zeppelin.map;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.data.ActivityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.AircraftInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.CityInfo;
import lielietea.mirai.plugin.core.game.zeppelin.data.Coordinate;
import lielietea.mirai.plugin.core.game.zeppelin.processor.Activity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MapGenerator {

    static Font font = new Font("zpix",Font.PLAIN,20);
    static final String ZEPPELIN_BG_PATH = "/zeppelin/PirateMap.png";
    static Color NEON_GREEN = new Color(0,218,148);

    static final int CITY_CIRCLE = 10;

    public static BufferedImage getBackgroundImage(){
        BufferedImage backgroundImage = null;
        try {
            InputStream is = MapGenerator.class.getResourceAsStream(ZEPPELIN_BG_PATH);
            assert is != null;
            backgroundImage = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }

    public static BufferedImage drawAllCities(BufferedImage originalImage){
        Graphics2D g2d = originalImage.createGraphics();
        g2d.setColor(NEON_GREEN);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(CityInfo c:CityInfoUtils.getINSTANCE().cityInfoList){
            //g2d.fillOval((int)c.coordinate.x-CITY_CIRCLE/2,(int)c.coordinate.y-CITY_CIRCLE/2,CITY_CIRCLE,CITY_CIRCLE);
            //g2d.drawString(c.code,(int) c.coordinate.x+20, (int)c.coordinate.y+5);
            g2d.fillOval(c.coordinate.x.intValue()-CITY_CIRCLE/2,c.coordinate.y.intValue()-CITY_CIRCLE/2,CITY_CIRCLE,CITY_CIRCLE);
            g2d.drawString(c.code, c.coordinate.x.intValue()+20, c.coordinate.y.intValue()+5);
        }
        g2d.dispose();
        return originalImage;
    }

    public static BufferedImage drawGeoSystem(BufferedImage originalImage){
        Graphics2D g2d = originalImage.createGraphics();
        g2d.setColor(NEON_GREEN);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int i=100;i<1500;i+=200){
            g2d.drawLine(i,0,i, Config.MAX_Y_POSITION);
            g2d.drawString(String.valueOf(i),i+20,25);
        }

        for(int i=150;i<2283;i+=200){
            g2d.drawLine(0,i,Config.MAX_X_POSITION, i);
            g2d.drawString(String.valueOf(i),5,i+20);
        }

        g2d.dispose();
        return originalImage;
    }

    public static BufferedImage drawActivity(BufferedImage originalImage,boolean showNormal, boolean showPolice, boolean showPirate, boolean showPhantom){
        Graphics2D g2d = originalImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(ActivityInfo ac: Activity.getInstance().activities){
            try {
                AircraftInfo ai = Aircraft.get(ac.getPlayerID());
                switch (Objects.requireNonNull(ai).getShipKind()) {
                    case NormalShip:
                        if (showNormal) {
                            g2d.setColor(Color.WHITE);
                            //g2d.drawLine((int) ac.getDeparture().x, (int) ac.getDeparture().y, (int) ai.getCoordinate().x, (int) ai.getCoordinate().y);
                            //g2d.fillOval((int) ai.getCoordinate().x - 1, (int) ai.getCoordinate().y - 1, 2, 2);
                            //g2d.drawString(ai.getName() + " " + ai.getMode(), (int) ai.getCoordinate().x + 7, (int) ai.getCoordinate().y + 5);
                            g2d.drawLine(ac.getDeparture().x.intValue(), ac.getDeparture().y.intValue(), ai.getCoordinate().x.intValue(), ai.getCoordinate().y.intValue());
                            g2d.fillOval(ai.getCoordinate().x.intValue() - 1, ai.getCoordinate().y.intValue() - 1, 2, 2);
                            g2d.drawString(ai.getName() + " " + ai.getMode(), ai.getCoordinate().x.intValue() + 7, ai.getCoordinate().y.intValue() + 5);
                        }
                        break;
                    case Pirate:
                        if (showPirate) {
                            g2d.setColor(Color.RED);
                            g2d.drawLine(ac.getDeparture().x.intValue(), ac.getDeparture().y.intValue(), ai.getCoordinate().x.intValue(), ai.getCoordinate().y.intValue());
                            g2d.fillOval(ai.getCoordinate().x.intValue() - 1, ai.getCoordinate().y.intValue() - 1, 2, 2);
                            g2d.drawString(ai.getName() + " " + ai.getMode(), ai.getCoordinate().x.intValue() + 7, ai.getCoordinate().y.intValue() + 5);
                        }
                        break;
                    case Police:
                        if (showPolice) {
                            g2d.setColor(Color.BLUE);
                            g2d.drawLine(ac.getDeparture().x.intValue(), ac.getDeparture().y.intValue(), ai.getCoordinate().x.intValue(), ai.getCoordinate().y.intValue());
                            g2d.fillOval(ai.getCoordinate().x.intValue() - 1, ai.getCoordinate().y.intValue() - 1, 2, 2);
                            g2d.drawString(ai.getName() + " " + ai.getMode(), ai.getCoordinate().x.intValue() + 7, ai.getCoordinate().y.intValue() + 5);
                        }

                        break;
                    case PhantomShip:
                        if (showPhantom) {
                            g2d.setColor(Color.WHITE);
                            g2d.drawLine(ac.getDeparture().x.intValue(), ac.getDeparture().y.intValue(), ai.getCoordinate().x.intValue(), ai.getCoordinate().y.intValue());
                            g2d.fillOval(ai.getCoordinate().x.intValue() - 1, ai.getCoordinate().y.intValue() - 1, 2, 2);
                            g2d.drawString(ai.getName() + " " + ai.getMode(), ai.getCoordinate().x.intValue() + 7, ai.getCoordinate().y.intValue() + 5);
                        }
                        break;
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        g2d.dispose();
        return originalImage;
    }

    public static BufferedImage drawActivity(BufferedImage originalImage, ActivityInfo activityInfo){
        Graphics2D g2d = originalImage.createGraphics();
        g2d.setColor(Color.ORANGE);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(ActivityInfo ac: Activity.getInstance().activities){
            if(ac.equals(activityInfo)){
                AircraftInfo ai = Aircraft.get(ac.getPlayerID());
                assert ai != null;
                g2d.drawLine(ac.getDeparture().x.intValue(),ac.getDeparture().y.intValue(), ai.getCoordinate().x.intValue(), ai.getCoordinate().y.intValue());
                g2d.fillOval(ai.getCoordinate().x.intValue() - 1, ai.getCoordinate().y.intValue() - 1, 2, 2);
                g2d.drawString(ai.getName() + " " + ai.getMode(), ai.getCoordinate().x.intValue() + 7, ai.getCoordinate().y.intValue() + 5);
                break;
            }
        }

        g2d.dispose();
        return originalImage;
    }

    @SuppressWarnings("ConstantConditions")
    public static BufferedImage draw(){
        BufferedImage bi = drawGeoSystem(getBackgroundImage());
        bi = drawAllCities(bi);
        bi = drawActivity(bi, true, true, true, true);
        return bi;
    }

}
