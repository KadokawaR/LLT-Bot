package lielietea.mirai.plugin.messageresponder.mahjong;

import lielietea.mirai.plugin.messageresponder.lotterywinner.LotteryMachine;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class MahjongRiddle {
    static int RIDDLE_LENGTH = 5;

    static Random rand = new Random();

    static class riddleFactor{
        int[] answerNum;
        boolean[] isGuessed;
    }

    static riddleFactor rf = new riddleFactor();

    static Timer timer = new Timer(true);
    static Map<Long,riddleFactor> riddleResetFlags = new HashMap<>();


    //生成小于124的若干个随机数，用于生成麻将牌
    public  static int[] getRandomNum(int num){
        int[] randomNum = new int[num];
        for(int i=0;i<num;i++){
            assert rand != null;
            randomNum[i] = rand.nextInt(124); }
        Arrays.sort(randomNum);
        return randomNum;
    }

    //通过数组获得正式麻将名称
    public static String[] resolveRandomTiles(int[] num) {
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
    public static void sendTileImage(BufferedImage image, GroupMessageEvent event) throws IOException {
        InputStream is;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageOutputStream imOut;
        imOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(image, "png", imOut);
        is = new ByteArrayInputStream(bs.toByteArray());
        event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), is));
    }


    //判定消息里面是否有答案
    public static boolean gotAnswer(int[] answerNum,GroupMessageEvent event){
        String[] answer = transformAnswer(answerNum);
        for (String s : answer) {
            if (event.getMessage().contentToString().contains(s)) {
                return true;
            }
        }
        return false;
    }

    //如果猜中了则改变rf里面的boolean数组
    public static riddleFactor setIsGuessed(riddleFactor rf, GroupMessageEvent event){
        String[] answer = transformAnswer(rf.answerNum);
        for(int i = 0; i< rf.answerNum.length;i++){
            if (event.getMessage().contentToString().contains(answer[i])){
                rf.isGuessed[i] = true;
            }
        }
        return rf;
    }

    //把缺德的万字和东字转成简体
    public static String[] transformAnswer(int[] answerNum){
        String[] transformedAnswer = new String[answerNum.length];
        for(int i=0;i<answerNum.length;i++){
            if((answerNum[i]>=72)&&(answerNum[i]<108)){
                ArrayList<String> chineseNum = new ArrayList<>(Arrays.asList(
                        "一","二","三","四","五","六","七","八","九"
                ));
                transformedAnswer[i] = (chineseNum.get(Math.toIntExact(answerNum[i] % 9))+"万");
            }
            else if (((answerNum[i]>=108)&&(answerNum[i]<124))&&(answerNum[i]%4==0)){
                transformedAnswer[i]="东风";
            }
            else{
                transformedAnswer[i] = FortuneTeller.getMahjong(answerNum[i]);
            }
        }
        return transformedAnswer;
    }

    //boolean数组里如果全部true就返回true
    public static boolean isAllTrue(boolean[] isGuessed){
        for(boolean i : isGuessed){ if (!i){ return false; } }
        return true;
    }

    //根据boolean[] isGuessed 来替换 String[] answer
    public static String[] displayAnswer(boolean[] isGuessed, String[] transformAnswer){
        for (int i=0;i< transformAnswer.length;i++){
            if(!isGuessed[i]){
                transformAnswer[i] = "无字";
            }
        }
        return transformAnswer;
    }

    //临时性的检测是否是麻将牌用语
    public static boolean isMahjongTile(GroupMessageEvent event){
        String str = event.getMessage().contentToString();
        return str.contains("风")||str.contains("万")||str.contains("筒")||str.contains("条");
    }

    public static void riddleStart(GroupMessageEvent event) throws IOException {


        if (event.getMessage().contentToString().contains("麻将测试")) {
            event.getSubject().sendMessage("麻将测试开始了");
            //检测是否有该群的flag，如果没有则重新生成并在180s之后清空
            if (!riddleResetFlags.containsKey(event.getGroup().getId())) {

                rf.answerNum = getRandomNum(RIDDLE_LENGTH);
                rf.isGuessed = new boolean[RIDDLE_LENGTH];
                riddleResetFlags.put(event.getGroup().getId(), rf);;
                BufferedImage img = getTileImage(displayAnswer(rf.isGuessed, transformAnswer(rf.answerNum)));
                sendTileImage(img, event);

                //每180s清空谜语重置标记
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (!isAllTrue(riddleResetFlags.get(event.getGroup().getId()).isGuessed)) {
                            try {
                                event.getSubject().sendMessage("公布答案:");
                                BufferedImage imgAnswer = getTileImage(resolveRandomTiles(rf.answerNum));
                                sendTileImage(imgAnswer, event);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        riddleResetFlags.remove(event.getGroup().getId());
                    }
                }, 180 * 1000);
            }
        }

        if (isMahjongTile(event)&&riddleResetFlags.containsKey(event.getGroup().getId())) {
            setIsGuessed(rf, event);
            if (gotAnswer(rf.answerNum, event)) {
                event.getSubject().sendMessage("中了!");
                BufferedImage img = getTileImage(displayAnswer(rf.isGuessed, resolveRandomTiles(rf.answerNum)));
                sendTileImage(img, event);
            }


            //检测这次结束之后是否全中，全中了则删除该flag
            if (isAllTrue(riddleResetFlags.get(event.getGroup().getId()).isGuessed)) {
                event.getSubject().sendMessage("恭喜猜中！");
                MahjongRiddle.riddleResetFlags.remove(event.getGroup().getId());
            }
        }
    }
}
