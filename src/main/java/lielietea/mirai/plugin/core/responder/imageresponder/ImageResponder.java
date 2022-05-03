package lielietea.mirai.plugin.core.responder.imageresponder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lielietea.mirai.plugin.core.harbor.Harbor;
import lielietea.mirai.plugin.utils.IdentityUtil;
import lielietea.mirai.plugin.utils.fileutils.Copy;
import lielietea.mirai.plugin.utils.fileutils.Read;
import lielietea.mirai.plugin.utils.fileutils.Touch;
import lielietea.mirai.plugin.utils.fileutils.Write;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ImageResponder {

    static final String IMAGE_DIR_PATH =  System.getProperty("user.dir") + File.separator + "image" + File.separator;

    static final String IMAGE_DATA_PATH =  IMAGE_DIR_PATH + "imagedata.json";

    ImageResponder(){}

    private static final ImageResponder INSTANCE;

    static class DataListClass{
        List<ImageResponderData> dataList;
        DataListClass(){
            this.dataList = new ArrayList<ImageResponderData>(){{add(new ImageResponderData("/qt","qt","感谢使用七筒开放版。",TriggerType.Equal,ResponseType.Any));}};
        }
    }

    enum TriggerType{
        Equal,
        Contain
    }

    enum ResponseType{
        Friend,
        Group,
        Any
    }

    static {
        INSTANCE = new ImageResponder();
        initialize();
    }

    private DataListClass dataListClass;

    public static ImageResponder getINSTANCE() {
        return INSTANCE;
    }

    static void initialize(){

        getINSTANCE().dataListClass = new DataListClass();
        if(Touch.file(IMAGE_DATA_PATH)){
            try {
                getINSTANCE().dataListClass = new Gson().fromJson(Read.fromFile(IMAGE_DATA_PATH), DataListClass.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeRecord();
        }

        //范例图片文件夹
        if(!Touch.dir(IMAGE_DIR_PATH+"qt")) Copy.fromInnerResource("/pics/chitung/chitung public.png",IMAGE_DIR_PATH+"qt/chitung public.png");

    }

    static DataListClass readRecord(){
        try {
            return new Gson().fromJson(Read.fromFile(IMAGE_DATA_PATH), DataListClass.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeRecord(){
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(getINSTANCE().dataListClass);
        Write.cover(jsonString, IMAGE_DATA_PATH);
    }

    static BufferedImage getImage(String path){
        File sourceImage = new File(path);
        try{
            return ImageIO.read(sourceImage);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static BufferedImage getImage(File image){
        try{
            return ImageIO.read(image);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static boolean isImage(File file){
        try {
            BufferedImage image = ImageIO.read(file);
            if (image!=null) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static File[] getImages(String dirPath){
        File[] tempList = null;
        try {
            tempList = new File(dirPath).listFiles();
        } catch(Exception e){
            e.printStackTrace();
        }
        if(tempList==null) return null;

        List<File> fileList = new ArrayList<>();

        for(File file:tempList){
            if(isImage(file)) fileList.add(file);
        }

        return fileList.toArray(new File[0]);
    }

    static File returnRandomFile(File[] images){
        return images[new Random().nextInt(images.length)];
    }

    public static ImageResponderData triggeredWordData(String message){
        for(ImageResponderData ird:getINSTANCE().dataListClass.dataList){
            for(String keyword: ird.keyword){
                switch(ird.triggerType){
                    case Contain:
                        if(message.contains(keyword)) return ird;
                        break;
                    case Equal:
                        if(message.equals(keyword)) return ird;
                        break;
                }
            }
        }
        return null;
    }

    static boolean isTriggered(ImageResponderData ird, MessageEvent event){
        if(ird==null) return false;
        switch(ird.responseType){
            case Friend:
                if(event instanceof FriendMessageEvent) return true;
                break;
            case Group:
                if(event instanceof GroupMessageEvent) return true;
                break;
            case Any:
                return true;
        }
        return false;
    }

    static void reset(MessageEvent event,String message){
        if(!IdentityUtil.isAdmin(event)) return;
        if(message.toLowerCase().contains("/reset ir")){
            getINSTANCE().dataListClass=readRecord();
            event.getSubject().sendMessage("已经重置 Image Responder 的配置文件。");
        }
    }

    public static void check(MessageEvent event,String message){
        if(message.equalsIgnoreCase("/check ir")||message.equalsIgnoreCase("/check -ir")||message.equalsIgnoreCase("查看图库响应")){
            MessageChainBuilder mcb = new MessageChainBuilder().append("图库响应关键词：\n");
            for(int i=0;i<getINSTANCE().dataListClass.dataList.size();i++){
                ImageResponderData ird = getINSTANCE().dataListClass.dataList.get(i);
                mcb.append("关键词：");
                StringBuilder sb = new StringBuilder();
                for(String s:ird.keyword){
                    sb.append(s);
                    sb.append(" ");
                }
                mcb.append(sb.toString().trim()).append("\n").append("响应模式：").append(String.valueOf(ird.triggerType)).append("\n");
                mcb.append("当前环境响应状态：").append(String.valueOf(isTriggered(ird,event)));
                if(i!=getINSTANCE().dataListClass.dataList.size()-1){
                    mcb.append("\n\n");
                }
            }
            event.getSubject().sendMessage(mcb.asMessageChain());
        }
    }

    public static void handle(MessageEvent event){

        String message = event.getMessage().contentToString();
        ImageResponderData ird = triggeredWordData(message);

        //管理员入口
        reset(event,message);
        //检查入口
        check(event,message);

        if(!isTriggered(ird,event)) return;

        //检查目录是否存在
        String dirPath = IMAGE_DIR_PATH + ird.directoryName;
        if(!new File(dirPath).exists()){
            event.getSubject().sendMessage("触发词触发的相应图片目录不存在，请检查imagedata.json。#Error01");
            return;
        }

        if(new File(dirPath).listFiles()==null){
            event.getSubject().sendMessage("触发词触发的相应图片目录不存在，请检查imagedata.json。#Error02");
            return;
        }

        //检查图片是否存在
        if(Objects.requireNonNull(new File(dirPath).listFiles()).length==0){
            event.getSubject().sendMessage("触发词触发的相应图片目录内没有图片，请添加图片。#Error03");
            return;
        }

        File[] images = getImages(dirPath);

        //检查文件里内是否有图片
        if(images == null){
            event.getSubject().sendMessage("触发词触发的相应图片目录内没有图片，请添加图片。#Error04");
            return;
        }

        if(images.length==0){
            event.getSubject().sendMessage("触发词触发的相应图片目录内没有图片，请添加图片。#Error05");
            return;
        }

        //随机抓取图片
        File image = returnRandomFile(images);

        //判定是否是图片
        if(!isImage(image)){
            event.getSubject().sendMessage("触发词触发的相应图片目录内有非图片文件，请检查。#Error06");
            return;
        }

        //发送图片

        if(!ird.text.equals("")){
            event.getSubject().sendMessage(ird.text);
        }

        ExternalResource er = ExternalResource.create(image);
        event.getSubject().sendMessage(event.getSubject().uploadImage(er));
        er.getClosed();

        Harbor.count(event);

    }

    public void ini(){
        System.out.println("Initialize Universal Image Responder");
    }

}
