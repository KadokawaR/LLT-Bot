package lielietea.mirai.plugin.core.responder.help;

import lielietea.mirai.plugin.core.groupconfig.GroupConfig;
import lielietea.mirai.plugin.core.groupconfig.GroupConfigManager;
import lielietea.mirai.plugin.core.responder.MessageResponder;
import lielietea.mirai.plugin.core.responder.RespondTask;
import lielietea.mirai.plugin.utils.image.ImageCreater;
import lielietea.mirai.plugin.utils.image.ImageSender;
import lielietea.mirai.plugin.utils.multibot.config.ConfigHandler;
import lielietea.mirai.plugin.utils.multibot.config.FunctionConfig;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewFunct implements MessageResponder<MessageEvent> {

    static final List<MessageType> types = new ArrayList<>(Arrays.asList(MessageType.FRIEND, MessageType.GROUP));
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static class Position{
        int X;
        int Y;
        Position(int X,int Y){
            this.X=X;
            this.Y=Y;
        }
    }

    static Position[] positions = new Position[8];

    enum Index{
        Responder(1),
        GroupConfig(2),
        Casino(3),
        Fish(4),
        Lottery(5),
        Responder2(6),
        Mahjong(7);

        private int code;
        Index(int code){
            this.code=code;
        }


    }

    static{
        positions[Index.Responder.code] = new Position(25,333); //responder 1->1
        positions[Index.GroupConfig.code] = new Position(226 ,184); //groupconfig 4->2
        positions[Index.Casino.code] = new Position(226,727); //casino 6->3
        positions[Index.Fish.code] = new Position(226,1221); //fish 7->4
        positions[Index.Lottery.code] = new Position(426,184); //lottery 5->5
        positions[Index.Responder2.code] = new Position(426,508); //responder part2 3->6
        positions[Index.Mahjong.code] = new Position(25,1201); //mahjong 2->7
        //如下不是位置，是图片长宽
        positions[0] = new Position(826,1428);
    }

    static BufferedImage getImage(String path){
        try(InputStream is = NewFunct.class.getResourceAsStream(path)){
            if(is==null) return null;
            return ImageIO.read(is);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    enum Type{
        Friend,
        Group
    }

    static FunctionConfig getRC(Type type, Bot bot){
        if (type == Type.Friend) {
            return ConfigHandler.getConfig(bot).getFriendFC();
        }
        return ConfigHandler.getConfig(bot).getGroupFC();
    }

    static GroupConfig getGC(long ID){
        return GroupConfigManager.getGroupConfig(ID);
    }

    static BufferedImage assemblePic(Type type,long groupID,Bot bot){

        String filePath = "/pics/function/help-0";

        BufferedImage[] images = new BufferedImage[8];
        String[] paths = new String[8];

        for(int i=0;i<paths.length;i++){
            paths[i] = filePath+i;
        }

        if(type==Type.Friend) {

            if (!getRC(type,bot).isResponder()) {
                paths[Index.Responder.code] += "-closed";
                paths[Index.Responder2.code] += "-closed";
            }

            paths[Index.Mahjong.code] += "-closed";
            paths[Index.GroupConfig.code] += "-closed";
            paths[Index.Mahjong.code] += "-closed";


            if (!getRC(type,bot).isCasino() || !getRC(type,bot).isGame()) {
                paths[Index.Casino.code] += "-closed";
            }

            if (!getRC(type,bot).isFish() || !getRC(type,bot).isGame()) {
                paths[Index.Fish.code] += "-closed";
            }

        } else {

            if (!getRC(type,bot).isResponder()||!getGC(groupID).isResponder()) {
                paths[Index.Responder.code] += "-closed";
                paths[Index.Responder2.code] += "-closed";
            }

            if (!getRC(type,bot).isGame() || !getGC(groupID).isGame()) {
                paths[Index.Mahjong.code] += "-closed";
            }

            if (!getRC(type,bot).isLottery() || !getGC(groupID).isLottery()) {
                paths[Index.Lottery.code] += "-closed";
            }

            if (!getRC(type,bot).isCasino() || !getRC(type,bot).isGame() || !getGC(groupID).isCasino() || !getGC(groupID).isGame()) {
                paths[Index.Casino.code] += "-closed";
            }

            if (!getRC(type,bot).isFish() || !getRC(type,bot).isGame() || !getGC(groupID).isFish() || !getGC(groupID).isGame()){
                paths[Index.Fish.code] += "-closed";
            }

        }

        for(int i=0;i<images.length;i++){
            paths[i] += ".png";
        }

        for(int i=0;i<images.length;i++){
            images[i] = getImage(paths[i]);
        }

        BufferedImage result = new BufferedImage(images[0].getWidth(),images[0].getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,images[0].getWidth(),images[0].getHeight());
        g2d.drawImage(images[0],0,0,null);

        for(int i=1;i<images.length;i++){
            assert images[0] != null;
            assert images[i] != null;
            g2d.drawImage(images[i],(int)((double)positions[i].X * (double)images[0].getWidth()/ (double)positions[0].X), (int)((double) positions[i].Y * (double) images[0].getHeight()/ (double)positions[0].Y),null);
        }

        return result;
    }


    @Override
    public boolean match(String content){
        return content.equalsIgnoreCase("/funct")|| content.equals("查看功能");
    }

    @Override
    public RespondTask handle(MessageEvent event){
        RespondTask.Builder builder = new RespondTask.Builder(event, this);
        if (match(event.getMessage().contentToString())){
            builder.addTask(()-> send(event));
        }
        return builder.build();
    }

    @Override
    public String getName() {
        return "功能";
    }

    public static void send(MessageEvent event){
        executor.schedule(new sendFunction(event),10, TimeUnit.MILLISECONDS);
    }

    static class sendFunction implements Runnable{

        private final MessageEvent event;
        sendFunction(MessageEvent event){
            this.event=event;
        }
        @Override
        public void run(){
            if(event instanceof GroupMessageEvent) {
                ImageSender.sendImageFromBufferedImage(event.getSubject(), assemblePic(Type.Group,((GroupMessageEvent) event).getGroup().getId(),event.getBot()));
            } else {
                ImageSender.sendImageFromBufferedImage(event.getSubject(), assemblePic(Type.Friend,0L,event.getBot()));
            }
        }
    }

    @NotNull
    @Override
    public List<MessageType> types() { return types; }
}
