package lielietea.mirai.plugin.utils.activation.handler;

import lielietea.mirai.plugin.utils.ContactUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;
import java.util.List;

public class ActivationRunnable {

    public static class AutoClear implements Runnable{

        private final Bot bot;

        AutoClear(Bot bot){
            this.bot = bot;
        }

        @Override
        public void run(){

            ActivationOperation.autoClearActions(bot);

        }
    }

    public static class QuitGroup implements Runnable{

        private final Group group;

        QuitGroup(Group group){
            this.group = group;
        }

        @Override
        public void run(){

            ActivationDatabase.deleteGroup(group.getId(),group.getBot());
            group.sendMessage("该群已经被取消激活，将会自动退群。如有问题请联系开发者。");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ContactUtil.tryQuitGroup(group.getId());

        }
    }

}
