package lielietea.mirai.plugin.core.game.zeppelin.data;

import com.google.gson.annotations.SerializedName;

public class CityInfo {
    @SerializedName("Coords")
    public Coordinate coordinate;
    @SerializedName("Name")
    public String nameCN;
    @SerializedName("Code")
    public String code;
}
