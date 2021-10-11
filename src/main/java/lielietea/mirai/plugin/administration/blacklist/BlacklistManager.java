package lielietea.mirai.plugin.administration.blacklist;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lielietea.mirai.plugin.administration.Operation;
import lielietea.mirai.plugin.utils.ContactUtil;
import lielietea.mirai.plugin.utils.MessageUtil;
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
    static File bannedUserJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json");
    static File bannedGroupJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json");
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();
    boolean deserializationFailureFlag = false;
    Set<BlockedContact> blockedGroup;
    Set<BlockedContact> blockedUser;

    BlacklistManager() {
        initialize();
    }

    // 初始化
    void initialize() {
        try {
            Files.createDirectories(Path.of(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist"));
            if (!bannedUserJson.exists()) bannedUserJson.createNewFile();
            if (!bannedGroupJson.exists()) bannedGroupJson.createNewFile();
            List<Set<BlockedContact>> readFromJson = JsonHelper.deserialize();
            blockedUser = readFromJson.get(0);
            blockedGroup = readFromJson.get(1);
        } catch (IOException e) {
            deserializationFailureFlag = true;
            blockedGroup = new HashSet<>();
            blockedUser = new HashSet<>();
            e.printStackTrace();
        }

    }

    // 处理消息
    public Optional<Operation> Handle(MessageEvent event) {
        return CommandParser.parse(event);
    }

    // 查询是否在黑名单中
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
                if (success) ContactUtil.tryQuitGroup(id);
            } else {
                success = blockedUser.add(new BlockedContact(id, reason, new Date()));
                // 如果拥有此人好友，那么自动删除
                if (success) ContactUtil.tryDeleteFriend(id);
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
            List<Set<BlockedContact>> readFromJson = JsonHelper.deserialize();
            blockedUser = readFromJson.get(0);
            blockedGroup = readFromJson.get(1);
        } catch (IOException e) {
            deserializationFailureFlag = true;
            blockedGroup = new HashSet<>();
            blockedUser = new HashSet<>();
            e.printStackTrace();
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
                if (builder.length() >= 1000) {
                    result.add(builder.toString());
                    builder = new StringBuilder();
                }
                buildNaturalLanguage(blockedContact, isGroup);
            }
            result.add(builder.toString());
            return result;
        } finally {
            readLock.unlock();
        }
    }

    // 触发保存黑名单
    void saveBlackList() {
        if(deserializationFailureFlag){
            MessageUtil.notifyDevGroup("请注意！由于黑名单读取出错，为保护黑名单文件，保存黑名单操作已被取消。");
        } else {
            writeLock.lock();
            try {
                JsonHelper.serialize(blockedUser, blockedGroup);
            } finally {
                writeLock.unlock();
            }
        }
    }

    static BlacklistManager INSTANCE = new BlacklistManager();

    public static BlacklistManager getInstance() {
        return INSTANCE;
    }


    static class JsonHelper {
        /**
         * 反序列化黑名单
         *
         * @return 返回的List中第一个为用户黑名单，第二个为群黑名单
         */
        static List<Set<BlockedContact>> deserialize() throws IOException {
            Gson gson = new Gson();
            List<Set<BlockedContact>> result = new ArrayList<>();
            Type typeToken = new TypeToken<HashSet<BlockedContact>>() {
            }.getType();

            // 读取用户黑名单Json
            BufferedReader reader = new BufferedReader(new FileReader(bannedUserJson, StandardCharsets.UTF_8));
            String jsonString = readFromReader(reader);
            Set<BlockedContact> blockedUser;
            if (jsonString.equals("")) blockedUser = new HashSet<>();
            else blockedUser = gson.fromJson(readFromReader(reader), typeToken);
            result.add(blockedUser);

            // 读取群黑名单Json
            reader = new BufferedReader(new FileReader(bannedGroupJson, StandardCharsets.UTF_8));
            jsonString = readFromReader(reader);
            Set<BlockedContact> blockedGroup;
            if (jsonString.equals("")) blockedGroup = new HashSet<>();
            else blockedGroup = gson.fromJson(readFromReader(reader), typeToken);
            result.add(blockedGroup);

            return result;
        }

        // 从Json文件中读取文本
        static String readFromReader(BufferedReader reader) throws IOException {
            StringBuilder builder = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            return builder.toString();
        }

        // 持久化黑名单对象
        // 黑名单对象将在创建与更新时存储为Json文件
        static void serialize(Set<BlockedContact> blockedUser, Set<BlockedContact> blockedGroup) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json"), StandardCharsets.UTF_8))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(blockedUser);
                writer.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json"), StandardCharsets.UTF_8))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(blockedGroup);
                writer.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
