package lielietea.mirai.plugin.resource.reload;

import net.mamoe.mirai.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class ReloadManager {
    final List<Reloadable> reloadables;

    static final ReloadManager INSTANCE = new ReloadManager();

    ReloadManager() {
        reloadables = new ArrayList<>();
    }

    public static ReloadManager getINSTANCE() {
        return INSTANCE;
    }

    public String reload(MessageEvent event) {
        return new Interpreter(event.getMessage().toString()).handle();
    }

    public void register(Reloadable reloadable) {
        ReloadManager.getINSTANCE().reloadables.add(reloadable);
    }

    /**
     * 初始化该管理器类。必须在插件启动时调用。
     */
    public void ini() {
        //TODO:waiting for implementing.
    }

    static class Interpreter {
        final String command;

        public Interpreter(String command) {
            this.command = command;
        }

        String handle() {
            //TODO:waiting for implementing.
            return "Reloading does not implemented yet!";
        }
    }
}
