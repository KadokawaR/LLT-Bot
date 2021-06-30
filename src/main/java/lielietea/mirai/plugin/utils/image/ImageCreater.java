package lielietea.mirai.plugin.utils.image;

import lielietea.mirai.plugin.game.mahjongriddle.MahjongRiddle;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageCreater {

    static int wx = 94;
    static int hx = 251;

    public static InputStream getInputStream(String urlPath) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlPath);
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(3000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                // 从服务器返回一个输入流
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static BufferedImage createWinnerImage(Contact winner) throws IOException {
        //读入头像和wanted底图
        InputStream is1 = getInputStream(winner.getAvatarUrl());
        BufferedImage img1 = ImageIO.read(is1);
        InputStream is2 = ImageCreater.class.getResourceAsStream("/pics/winner/wanted.jpg");
        BufferedImage img2 = ImageIO.read(is2);
        //头像变形
        ImageScale isc = new ImageScale();
        BufferedImage img0 = isc.imageZoomOut(img1,512,512,false);

        int w0 = img0.getWidth();
        int h0 = img0.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        // 从图片中读取RGB
        int[] ImageArrayOne = new int[w0 * h0];
        ImageArrayOne = img0.getRGB(0, 0, w0, h0, ImageArrayOne, 0, w0); // 逐行扫描图像中各个像素的RGB到数组中

        int[] ImageArrayTwo = new int[w2 * h2];
        ImageArrayTwo = img2.getRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2);

        // 生成新图片
        BufferedImage img_new;
        img_new = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);
        img_new.setRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2); // 设置上半部分或左半部分的RGB
        img_new.setRGB(wx, hx, w0, h0, ImageArrayOne, 0, w0);

        return img_new;
    }

    public static void sendWinnerImage(BufferedImage image, GroupMessageEvent event) throws IOException {
        InputStream is;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageOutputStream imOut;
        imOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(image, "png", imOut);
        is = new ByteArrayInputStream(bs.toByteArray());
        event.getSubject().sendMessage(Contact.uploadImage(event.getSubject(), is));
        is.close();
    }
}
