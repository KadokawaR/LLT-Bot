package lielietea.mirai.plugin.messageresponder;

/**
 * 实现该接口，意味着类你的类在Mirai关闭的时候需要进行一些收尾工作
 */
public interface CloseRequiredHandler {
    void onclose();
}
