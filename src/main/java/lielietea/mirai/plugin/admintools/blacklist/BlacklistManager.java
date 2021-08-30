package lielietea.mirai.plugin.admintools.blacklist;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.admintools.AdminCommandDispatcher;
import lielietea.mirai.plugin.admintools.Operation;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlacklistManager {
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();
    Set<BlockedContact> blockedGroup = new HashSet<>();
    Set<BlockedContact> blockedUser = new HashSet<>();

    static {
        try {
            Files.createDirectories(Path.of(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blockedUser"));
            Files.createDirectories(Path.of(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blockedGroup"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理消息
    public Optional<Operation> Handle(MessageEvent event) {
        return CommandParser.parse(event);
    }

    //查询是否在黑名单中
    public boolean contains(long id, boolean isGroup) {
        readLock.lock();
        try {
            if (isGroup) {
                for (BlockedContact blocked : INSTANCE.blockedGroup) {
                    if (id == blocked.getId()) return true;
                }
            } else {
                for (BlockedContact blocked : INSTANCE.blockedUser) {
                    if (id == blocked.getId()) return true;
                }
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }

    // 获取某个黑名单详细信息
    public String getSpecificInform(long id, boolean isGroup) {
        readLock.lock();
        try {
            Object[] contact = isGroup ?
                    blockedGroup.stream().filter(blockedContact -> blockedContact.getId() == id).toArray() :
                    blockedUser.stream().filter(blockedContact -> blockedContact.getId() == id).toArray();
            if (contact.length == 0) return "该对象不在黑名单中";
            else return "查找到如下信息：" + buildNaturalLanguage((BlockedContact) contact[0], isGroup);
        } finally {
            readLock.unlock();
        }
    }


    // 添加用户或群进入黑名单
    boolean add(long id, String reason, boolean isGroup) {
        writeLock.lock();
        try {
            boolean success;
            if (isGroup) {
                success = blockedGroup.add(new BlockedContact(id, reason, new Date()));
                // 如果在这个群里，那么自动退群
                if(success) AdminCommandDispatcher.getInstance().tryQuitGroup(id);
            }
            else {
                success = blockedUser.add(new BlockedContact(id, reason, new Date()));
                // 如果拥有此人好友，那么自动删除
                if(success) AdminCommandDispatcher.getInstance().tryDeleteFriend(id);
            }
            return success;
        } finally {
            writeLock.unlock();
        }
    }


    // 从黑名单中移除用户或群
    boolean remove(long id, boolean isGroup) {
        writeLock.lock();
        try {
            if (isGroup) return blockedGroup.removeIf(blockedContact -> blockedContact.getId() == id);
            else return blockedUser.removeIf(blockedContact -> blockedContact.getId() == id);

        } finally {
            writeLock.unlock();
        }
    }


    // 手动重载黑名单
    void reloadBlackList() {
        writeLock.lock();
        try {
            JsonHelper.deserialize();
        } finally {
            writeLock.unlock();
        }
    }

    // 编辑备注
    boolean editNote(long id, String note, boolean isGroup, boolean override) {
        writeLock.lock();
        try {
            Object[] contact = isGroup ?
                    blockedGroup.stream().filter(blockedContact -> blockedContact.getId() == id).toArray() :
                    blockedUser.stream().filter(blockedContact -> blockedContact.getId() == id).toArray();
            if (contact.length == 0) return false;
            if (override) ((BlockedContact) contact[0]).setExtraNote(note);
            else ((BlockedContact) contact[0]).addExtraNote(note);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    // 重设封禁原因
    boolean setReason(long id, String reason, boolean isGroup) {
        writeLock.lock();
        try {
            Object[] contact = isGroup ?
                    blockedGroup.stream().filter(blockedContact -> blockedContact.getId() == id).toArray() :
                    blockedUser.stream().filter(blockedContact -> blockedContact.getId() == id).toArray();
            if (contact.length == 0) return false;
            ((BlockedContact) contact[0]).setReason(reason);
            return true;
        } finally {
            writeLock.unlock();
        }
    }


    // 将黑名单对象转换为人话
    String buildNaturalLanguage(BlockedContact blockedContact, boolean isGroup) {
        return (isGroup ? "\n群号：" : "\nQQ号：") + blockedContact.getId()
                + "\n封禁时间： " + blockedContact.getBlockedDate()
                + "\n封禁原因： " + blockedContact.getReason()
                + (blockedContact.getExtraNote().equals("") ? "" : "备注： " + blockedContact.getExtraNote());
    }

    // 获取黑名单中全部对象的号码
    String getAllInform(boolean isGroup) {
        readLock.lock();
        try {
            Set<BlockedContact> blacklist = isGroup ? blockedGroup : blockedUser;
            if (blacklist.isEmpty()) return (isGroup ? "群" : "用户") + "黑名单为空。";
            StringBuilder builder = new StringBuilder();
            builder.append(isGroup ? "群" : "用户").append("黑名单如下：\n");
            for (BlockedContact blockedContact : blacklist) {
                builder.append(blockedContact.getId()).append("\n");
            }
            return builder.toString();
        } finally {
            readLock.unlock();
        }
    }

    // 获取黑名单中全部对象的封禁信息
    List<String> getAllDetailedInform(boolean isGroup) {
        readLock.lock();
        try {
            Set<BlockedContact> blacklist = isGroup ? blockedGroup : blockedUser;
            if (blacklist.isEmpty())
                return new ArrayList<>(Collections.singleton((isGroup ? "群" : "用户") + "黑名单为空。"));
            List<String> result = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            builder.append(isGroup ? "群" : "用户").append("黑名单如下：");
            for (BlockedContact blockedContact : blacklist) {
                if(builder.length()>=1000){
                    result.add(builder.toString());
                    builder = new StringBuilder();
                }
                buildNaturalLanguage(blockedContact,isGroup);
            }
            result.add(builder.toString());
            return result;
        } finally {
            readLock.unlock();
        }
    }

    // 触发保存黑名单
    void saveBlackList() {
        writeLock.lock();
        try {
            JsonHelper.serialize();
        } finally {
            writeLock.unlock();
        }
    }

    static BlacklistManager INSTANCE = new BlacklistManager();

    public static BlacklistManager getInstance() {
        return INSTANCE;
    }

    static class JsonHelper {
        // 读取所有黑名单对象
        static void deserialize() {
            File bannedUserJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json");
            File bannedGroupJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json");
            Gson gson = new Gson();
            Type typeToken = new TypeToken<HashSet<BlockedContact>>() {
            }.getType();
            try (BufferedReader reader = new BufferedReader(new FileReader(bannedUserJson, StandardCharsets.UTF_8))) {
                INSTANCE.blockedUser = gson.fromJson(readFromReader(reader), typeToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(bannedGroupJson, StandardCharsets.UTF_8))) {
                INSTANCE.blockedGroup = gson.fromJson(readFromReader(reader), typeToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static String readFromReader(BufferedReader reader) throws IOException {
            StringBuilder builder = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }

            return builder.toString();
        }

        // 保存新黑名单对象
        // 黑名单对象将在创建与更新时存储为Json文件
        static void serialize() {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json"), StandardCharsets.UTF_8))) {
                File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json");
                if (!file.exists()) {
                    file.createNewFile();
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(INSTANCE.blockedUser);
                writer.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json"), StandardCharsets.UTF_8))) {
                File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json");
                if (!file.exists()) {
                    file.createNewFile();
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(INSTANCE.blockedGroup);
                writer.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
