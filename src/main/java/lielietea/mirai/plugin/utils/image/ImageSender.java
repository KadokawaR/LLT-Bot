package lielietea.mirai.plugin.utils.image;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class ImageSender {
    public static void sendImageFromURL(Contact contact, URL url) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }catch(IOException e) {
            e.printStackTrace();
            Logger.getGlobal().warning("图片文件下载失败！");
        }

            ExternalResource externalResource = ExternalResource.create(output.toByteArray());
            Image imageReady;
            imageReady = contact.uploadImage(externalResource);
            contact.sendMessage(imageReady);

        try{
            externalResource.close();
        } catch(IOException e){
            e.printStackTrace();
            Logger.getGlobal().warning("资源关闭失败！");
        }

    }
}
