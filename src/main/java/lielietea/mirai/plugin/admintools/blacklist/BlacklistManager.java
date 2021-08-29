package lielietea.mirai.plugin.admintools.blacklist;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlacklistManager {
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();
    Set<BlockedContact> blockedGroup = new HashSet<>();
    Set<BlockedContact> blockedUser = new HashSet<>();

    static{
        try{
            Files.createDirectories(Path.of(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blockedUser"));
            Files.createDirectories(Path.of(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blockedGroup"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断群是否在黑名单中
     */
    public boolean match(Group group){
        readLock.lock();
        try{
            for(BlockedContact blocked: INSTANCE.blockedGroup){
                if(group.getId()==blocked.getId()) return true;
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 判断用户是否在黑名单中
     */
    public boolean match(User user){
        readLock.lock();
        try{
            for(BlockedContact blocked: INSTANCE.blockedUser){
                if(user.getId()==blocked.getId()) return true;
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }


    //从黑名单中移除群
    public boolean removeGroup(long id){
        writeLock.lock();
        try{
            BlockedContact tryFind = null;
            for(BlockedContact blocked: INSTANCE.blockedGroup){
                if(id==blocked.getId()) tryFind = blocked;
                break;
            }
            if(tryFind!=null) {
                INSTANCE.blockedGroup.remove(tryFind);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    ///从黑名单中移除用户
    public boolean removeUser(long id){
        writeLock.lock();
        try{
            BlockedContact tryFind = null;
            for(BlockedContact blocked: INSTANCE.blockedUser){
                if(id==blocked.getId()) tryFind = blocked;
                break;
            }
            if(tryFind!=null) {
                INSTANCE.blockedUser.remove(tryFind);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    //手动重载黑名单
    void reloadBlackList(){
        writeLock.lock();
        try{
            JsonHelper.deserialize();
        } finally {
            writeLock.unlock();
        }
    }

    //获取某个黑名单详细信息
    String getSpecificInfomation(long id, boolean isGroup){
        readLock.lock();
        try{
            //TODO
            return "";
        } finally {
            readLock.unlock();
        }
    }

    //触发保存黑名单
    void saveBlackList(){
        readLock.lock();
        try{
            JsonHelper.serialize();
        } finally {
            readLock.unlock();
        }
    }

    static BlacklistManager INSTANCE = new BlacklistManager();

    public static BlacklistManager getInstance(){
        return INSTANCE;
    }

    static class JsonHelper{
        //读取所有黑名单对象
        static void deserialize(){
            File bannedUserJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json");
            File bannedGroupJson = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json");
            Gson gson = new Gson();
            Type typeToken = new TypeToken<HashSet<BlockedContact>>(){}.getType();
            try(BufferedReader reader = new BufferedReader(new FileReader(bannedUserJson, StandardCharsets.UTF_8))){
                INSTANCE.blockedUser = gson.fromJson(readFromReader(reader),typeToken);
            } catch (IOException e){
                e.printStackTrace();
            }
            try(BufferedReader reader = new BufferedReader(new FileReader(bannedGroupJson, StandardCharsets.UTF_8))){
                INSTANCE.blockedGroup = gson.fromJson(readFromReader(reader),typeToken);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        static String readFromReader(BufferedReader reader) throws IOException {
            StringBuilder builder = new StringBuilder();
            String temp;
            while((temp= reader.readLine())!=null){
                builder.append(temp);
            }

            return builder.toString();
        }

        //保存新黑名单对象
        //黑名单对象将在创建与更新时存储为Json文件
        static void serialize(){
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator  + "blocked_user.json"), "UTF-8"))){
                File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_user.json");
                if(!file.exists()){
                    file.createNewFile();
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(INSTANCE.blockedUser);
                writer.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator  + "blocked_group.json"), "UTF-8"))){
                File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "blacklist" + File.separator + "blocked_group.json");
                if(!file.exists()){
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
