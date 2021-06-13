package lielietea.mirai.plugin.messageresponder;

/**
 * 实现该接口，意味着类是可以重载配置文件的
 */
public interface Reloadable {
    /**
     * 一个重载配置文件的方法
     */
    void reload();
}
