package lielietea.mirai.plugin.core.game.zeppelin.Notification;

import lielietea.mirai.plugin.core.game.zeppelin.Config;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.Aircraft;
import lielietea.mirai.plugin.core.game.zeppelin.aircraft.ShipKind;
import lielietea.mirai.plugin.core.game.zeppelin.data.Notification;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationCenter {

    public List<Notification> notifications;
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static{
        System.out.println("Notification Center Task Arranged");
        executor.scheduleAtFixedRate(new MainTask(), Config.NOTIFICATION_INITIAL_DELAY,1, TimeUnit.MINUTES);
    }

    NotificationCenter(){
        notifications = new ArrayList<>();
    }

    static final NotificationCenter INSTANCE = new NotificationCenter();
    public static NotificationCenter getInstance(){return INSTANCE;}


    static class MainTask implements Runnable{
        @Override
        public void run() {
            filter();
            send();
        }
    }

    static void send(){
        for(Notification n: getInstance().notifications){
            try {
                if (n.getMessageID() == 0) {
                    Objects.requireNonNull(Bot.getInstance(n.getBotName().getValue()).getFriend(n.getPlayerID())).sendMessage(n.getMessage());
                } else {
                    MessageChainBuilder mcb = new MessageChainBuilder();
                    mcb.append(new At(n.getPlayerID())).append(n.getMessage());
                    Objects.requireNonNull(Bot.getInstance(n.getBotName().getValue()).getGroup(n.getMessageID())).sendMessage(mcb.asMessageChain());
                }
                Thread.sleep(1000);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        getInstance().notifications.clear();
    }

    public static void add(Notification n){
        getInstance().notifications.add(n);
    }

    static void filter(){
        List<Notification> deleteList = new ArrayList<>();
        for(Notification n: getInstance().notifications){

            long playerID = n.getPlayerID();

            if(!Aircraft.exist(playerID)){
                deleteList.add(n);
                continue;
            }

            if(Objects.requireNonNull(Aircraft.get(playerID)).getShipKind()== ShipKind.PhantomShip || Objects.requireNonNull(Aircraft.get(playerID)).getShipKind()== ShipKind.Police) {
                deleteList.add(n);
                continue;
            }
            if(n.getPlayerID()<=0) {
                deleteList.add(n);
                continue;
            }
            if(Bot.getInstanceOrNull(n.getBotName().getValue())==null) {
                deleteList.add(n);
            }
        }
        getInstance().notifications.removeAll(deleteList);
    }

    public void ini(){}

}
