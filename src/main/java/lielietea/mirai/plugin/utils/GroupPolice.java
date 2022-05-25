package lielietea.mirai.plugin.utils;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GroupPolice {

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    final static GroupPolice INSTANCE = new GroupPolice();

    GroupPolice() {
        executor.scheduleAtFixedRate(new AutoClear(), 5, 3*60, TimeUnit.MINUTES);
}

    static public GroupPolice getINSTANCE() {
        return INSTANCE;
    }

    static class AutoClear implements Runnable {
        @Override
        public void run() {
            for (Bot bot : Bot.getInstances()) {
                for (Group group : bot.getGroups()) {
                    if (IdentityUtil.DevGroup.isDevGroup(group.getId())) continue;
                    if (group.getMembers().getSize() < 7) {
                        group.sendMessage("七筒目前不接受加入7人以下的群聊，将自动退群，请在其他群中使用七筒。感谢您使用七筒的服务。");
                        MessageUtil.notifyDevGroup("由于群聊人数不满7人，七筒已经从"+group.getName()+"("+group.getId()+")中离开。");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        executor.schedule(() -> {
                            Objects.requireNonNull(bot.getGroup(group.getId())).quit();
                        }, 15, TimeUnit.SECONDS);
                    }

                    for (NormalMember nm : group.getMembers()) {
                        for (Bot bot2 : Bot.getInstances()) {
                            if (bot2.getId() == (bot.getId())) continue;
                            if (nm.getId() == bot2.getId()) {
                                if(nm.getId()<bot.getId()) continue;
                                group.sendMessage("检测到其他在线七筒账户在此群聊中，将自动退群。");
                                MessageUtil.notifyDevGroup("由于检测到其他七筒，七筒已经从"+group.getName()+"("+group.getId()+")中离开。");
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                executor.schedule(() -> {
                                    Objects.requireNonNull(bot.getGroup(group.getId())).quit();
                                }, 15, TimeUnit.SECONDS);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class BotAutoClear implements Runnable {

        private Bot bot;

        public BotAutoClear(Bot bot){
            this.bot=bot;
        }

        @Override
        public void run() {

            for (Group group : bot.getGroups()) {
                if (IdentityUtil.DevGroup.isDevGroup(group.getId())) continue;
                if (group.getMembers().getSize() < 7) {
                    group.sendMessage("七筒目前不接受加入7人以下的群聊，将自动退群，请在其他群中使用七筒。感谢您使用七筒的服务。");
                    MessageUtil.notifyDevGroup("由于群聊人数不满7人，七筒已经从"+group.getName()+"("+group.getId()+")中离开。");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    executor.schedule(() -> {
                        Objects.requireNonNull(bot.getGroup(group.getId())).quit();
                    }, 15, TimeUnit.SECONDS);
                    continue;
                }

                for (NormalMember nm : group.getMembers()) {
                    for (Bot bot2 : Bot.getInstances()) {
                        if (bot2.getId() == (bot.getId())) continue;
                        if (nm.getId() == bot2.getId()) {
                            if(nm.getId()<bot.getId()) continue;
                            group.sendMessage("检测到其他在线七筒账户在此群聊中，将自动退群。");
                            MessageUtil.notifyDevGroup("由于检测到其他七筒，七筒已经从"+group.getName()+"("+group.getId()+")中离开。");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            executor.schedule(() -> {
                                Objects.requireNonNull(bot.getGroup(group.getId())).quit();
                            }, 15, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        }

    }


    public void ini(){}
}
