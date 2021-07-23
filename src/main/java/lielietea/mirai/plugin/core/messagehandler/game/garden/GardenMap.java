package lielietea.mirai.plugin.core.messagehandler.game.garden;

import lielietea.mirai.plugin.core.messagehandler.game.garden.propertyenum.PlantSeed;
import lielietea.mirai.plugin.utils.image.ImageCreater;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GardenMap extends GardenUtils{

    public static List<Integer> tileList(GardenWorld gw, long groupID){
        List<Integer> integerList = new ArrayList<>();
        for(int i=0; i<gw.group.get(getGroupGarden(groupID,gw)).layout.size();i++){
            if (convertToGS(gw.group.get(getGroupGarden(groupID,gw))).get(i).isMature) {
                integerList.add(-1);
            }
            else{
                integerList.add(gw.group.get(getGroupGarden(groupID,gw)).layout.get(i).object);
            }
        }
        return integerList;
    }

    public static BufferedImage getSeedImage(int i){
        String ps;
        if(i==-1){
            ps = "NotMature";
        } else {
            ps = PlantSeed.values()[i].toString();
        }
        try {
            return ImageCreater.getImageFromResource("/pics/garden/seed/"+ps+".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage assembleImage(GardenWorld gw, long groupID){
        List<Integer> integerList = tileList(gw,groupID);
        BufferedImage imgBig = null;
        for(int i1=0;i1<integerList.size()/6;i1++){
            BufferedImage img = null;
            for(int i2=0;i2<6;i2++){
                if (img!=null) {
                    img = ImageCreater.addImageAtRight(img, Objects.requireNonNull(getSeedImage(integerList.get(i1 * 6 + i2))));
                } else{
                    img = getSeedImage(integerList.get(i1*6+i2));
                }
            }
            if (imgBig!=null) {
                assert img != null;
                imgBig = ImageCreater.addImageAtBottom(imgBig,img);
            } else{
                imgBig = img;
            }
        }
        return imgBig;
    }

    public static String assembleText(GardenWorld gw, long groupID){
        List<Integer> integerList = tileList(gw,groupID);
        StringBuilder result= new StringBuilder();
        int count = 0;
        for (Integer integer : integerList) {
            if (integer == -1){
                result.append("X");
            } else {
                result.append(integer);
            }
            if (count < 6) {
                result.append("|");
            } else {
                result.append("\n");
                count = 0;
            }
            count += 1;
        }
        return result.toString();
    }
    
}