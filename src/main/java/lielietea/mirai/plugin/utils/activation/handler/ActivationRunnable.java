package lielietea.mirai.plugin.utils.activation.handler;

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

            List<Long> currentGroupIDs = new ArrayList<>();

            for(Group group:bot.getGroups()){
                currentGroupIDs.add(group.getId());
            }

            ActivationDatabase.updateActivationDataGroupList(bot,currentGroupIDs);
            ActivationDatabase.updateEntryRecordList(bot,currentGroupIDs);
            ActivationDatabase.quitOutOfDateGroups(bot);

        }
    }
}
