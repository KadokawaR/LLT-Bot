package lielietea.mirai.plugin.core.game.zeppelin.interaction;

public class Notice {
    public static final String NOT_REGISTERED = "未查询到您的飞艇信息，请先输入/register注册。";
    public static final String REPEATED_REGISTRATION = "您已注册过飞艇，无法重复注册。";
    public static final String REGISTRATION_IN_GROUP = "暂不受理私聊注册，请在含有七筒的群聊中注册您的飞艇。";

    public static final String ILLEGAL_MODE = "您输入的飞艇型号不正确，请查看商店。";
    public static final String NOT_IN_CITY = "您目前还不在城市里，请抵达可更换飞艇的城市进行更换。";
    public static final String NOT_IN_TARGET_CITY = "您想要更换的飞艇型号无法在这座城市内更换，请抵达相应城市进行更换。";
    public static final String IS_IN_ACTIVITY = "您目前正在飞行中，无法进行操作。";
    public static final String DOESNT_HAVE_ENOUGH_MONEY = "您的南瓜比索数量不足，请稍后再尝试。";
}
