package lielietea.mirai.plugin.core.messagehandler.game.garden;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Garden extends GardenUtils {

    private static final Garden INSTANCE = new Garden();

    private Garden() {
    }

    public static Garden getINSTANCE() {
        return INSTANCE;
    }

    public static void start(GroupMessageEvent event, GardenWorld gw) {
        String message = event.getMessage().contentToString();
        long groupID = event.getGroup().getId();
        if (message.contains("/garden")) {
            if (getGroupGarden(groupID, gw) == groupID) {
                initialize(gw, event);
            }
        }
    }
}
