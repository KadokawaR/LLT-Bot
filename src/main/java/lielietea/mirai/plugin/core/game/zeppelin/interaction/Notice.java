package lielietea.mirai.plugin.core.game.zeppelin.interaction;

public class Notice {
    public static final String NOT_REGISTERED = "未查询到您的飞艇信息，请先输入/register注册。";
    public static final String REPEATED_REGISTRATION = "您已注册过飞艇，无法重复注册。";
    public static final String REGISTRATION_IN_GROUP = "暂不受理私聊注册，请在含有七筒的群聊中注册您的飞艇。";
    public static final String WRONG_NAME_CHANGING_FORMAT = "您输入的名称不符合规范，请使用7位数字与大写字母混合命名。";

    public static final String ILLEGAL_MODE = "您输入的飞艇型号不正确，请查看商店。";
    public static final String NOT_IN_CITY = "您目前还不在城市里，请抵达可更换飞艇的城市进行更换。";
    public static final String NOT_IN_TARGET_CITY = "您想要更换的飞艇型号无法在这座城市内更换，请抵达相应城市进行更换。";
    public static final String IS_IN_ACTIVITY = "您目前正在飞行中，无法进行操作。";
    public static final String IS_NOT_IN_ACTIVITY = "您目前不在飞行中，无法进行操作。";
    public static final String DOESNT_HAVE_ENOUGH_MONEY = "您的南瓜比索数量不足，请稍后再尝试。";

    public static final String CANNOT_CHANGE_PIRATE_STATUS = "无法切换身份状态。";
    public static final String CANNOT_CHANGE_HOME_PORT = "您的母港已经在这种城市，无法切换。";
    public static final String HOME_PORT_CHANGED = "已经切换母港至";
    public static final String BECOME_PIRATE = "您已经注册成为海盗。";
    public static final String BECOME_TRADER = "您已经注册成为商船。";

    public static final String EMPTY_INDICATOR = "请输入/starttravel+空格+目的地代码启动飞艇，如果您是海盗，请输入目标飞艇的7位编号，或者使用/stationed驻扎。";
    public static final String WRONG_DESTINATION_INDICATOR = "您输入的目的地不符合格式。";
    public static final String WRONG_SHIP_INDICATOR = "您输入的目标飞艇7位编号无效。";
    public static final String SHIP_DOESNT_EXIST = "您输入的目标飞艇目前不在执飞。";
    public static final String WRONG_CITY_CODE = "您输入的城市代码无效。";
    public static final String SAME_CITY_WARNING = "您已经在这座城市内。";
    public static final String NOT_IN_MAP_RANGE = "您输入的坐标不在地图范围内。";

    public static final String NOT_PIRATE = "您并未携带海盗标记，所以无法驻扎。";
    public static final String NOT_ENOUGH_MONEY = "您的南瓜比索数量已经不足，将会自动停靠在当前地点。";

}
