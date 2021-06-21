package lielietea.mirai.plugin.messageresponder.mahjong;

import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryMachine;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class MahjongRiddle {

    static class riddleFactor{
        int[] answerNum;
        boolean[] isGuessed;
        riddleFactor(int[] i, boolean[]b){
            answerNum = i;
            isGuessed = b;
        }
    }

    static int RIDDLE_LENGTH = 5;

    static int[] answerNum = getRandomNum(RIDDLE_LENGTH);
    static boolean[] isGuessed = new boolean[RIDDLE_LENGTH];
    static riddleFactor rf = new riddleFactor(answerNum, isGuessed);

    static Random rand = new Random();
    static Timer timer = new Timer(true);
    static Map<Long,riddleFactor> riddleResetFlags = new HashMap<>();

    static{
        //每180s清空谜语重置标记
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               MahjongRiddle.riddleResetFlags.clear();
                           }
                       }, 180 * 1000);
    }

    //生成小于124的若干个随机数，用于生成麻将牌
    public  static int[] getRandomNum(int num){
        int[] randomNum = new int[num];
        for(int i=0;i<num;i++){
            assert rand != null;
            randomNum[i] = rand.nextInt(124); }
        Arrays.sort(randomNum);
        return randomNum;
    }

    //通过数组获得麻将名称
    public static String[] getRandomTiles(int[] num) {
        String[] Tiles = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            Tiles[i] = FortuneTeller.getMahjong(num[i]);
        }
        return Tiles;
    }

    //用一个String生成连图
    public static BufferedImage getTileImage(String[] Tiles) throws IOException {
        BufferedImage img0 = null;
        for (String tile : Tiles) {
            String MAHJONG_PIC_PATH = "/pics/mahjong/" + tile + ".png";
            InputStream is = MahjongRiddle.class.getResourceAsStream(MAHJONG_PIC_PATH);
            BufferedImage img = ImageIO.read(is);
            int w1=0;
            int h1=480;
            if (img0!=null) {
                w1 = img0.getWidth();
            }
            int w2 = img.getWidth();
            int h2 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            if (img0!=null) {
                ImageArrayOne = img0.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
            }
            int[] ImageArrayTwo = new int[w2 * h2];
            ImageArrayTwo = img.getRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2);

            // 生成新图片
            BufferedImage img1;
            img1 = new BufferedImage(w1 + w2, h1, BufferedImage.TYPE_INT_RGB);
            if (img0!=null) {
                img1.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            }
            img1.setRGB(w1, 0, w2, h2, ImageArrayTwo, 0, w2);
            img0=img1;
        }
        return img0;
    }

    //麻将图片发送测试
    public static void sendTileImage(GroupMessageEvent event) throws IOException {
        if (event.getMessage().contentToString().contains("麻将测试")) {
            BufferedImage image = getTileImage(getRandomTiles(getRandomNum(5)));
            InputStream is = null;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageOutputStream imOut;
            imOut = ImageIO.createImageOutputStream(bs);
            ImageIO.write(image, "png", imOut);
            is = new ByteArrayInputStream(bs.toByteArray());
            event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), is));
        }
    }


    //判定消息里面是否有答案
    public static boolean gotAnswer(String[] answer,GroupMessageEvent event){
        for (String s : answer) {
            if (event.getMessage().contentToString().contains(s)) {
                return true;
            }
        }
        return false;
    }

    //把缺德的万字和东字转成简体
    public static String[] transformAnswer(int[] answerNum){
        String[] transformedAnswer = new String[answerNum.length];
        for(int i : answerNum){
            if((i>=72)&&(i<108)){
                ArrayList<String> chineseNum = new ArrayList<>(Arrays.asList(
                        "一","二","三","四","五","六","七","八","九"
                ));
                transformedAnswer[i] = (chineseNum.get(Math.toIntExact(i % 9))+"万");
            }
            else if (((i>=108)&&(i<124))&&(i%4==0)){
                transformedAnswer[i]="东风";
            }
            else{
                transformedAnswer[i] = FortuneTeller.getMahjong(i);
            }
        }
        return transformedAnswer;
    }

    //boolean数组里如果全部True就返回true
    public static boolean isAllTrue(boolean[] isGuessed){
        for(boolean i : isGuessed){ if (!i){ return false; } }
        return true;
    }

    public static void riddleStart(GroupMessageEvent event){
        String[] narrowAnswer = getRandomTiles(answerNum);
        String[] transformedAnswer = transformAnswer(answerNum);

        if(!riddleResetFlags.containsKey(event.getGroup().getId())){
            riddleResetFlags.put(event.getGroup().getId(),rf);
        }





        if(isAllTrue(riddleResetFlags.get(event.getGroup().getId()).isGuessed)){
            MahjongRiddle.riddleResetFlags.clear();
        }
    }
}
