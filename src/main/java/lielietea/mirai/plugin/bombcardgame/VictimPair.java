package lielietea.mirai.plugin.bombcardgame;

import java.util.Objects;

//用于传递炸弹牌受害者信息
class VictimPair{
    public long qqID;
    public long groupID;

    public VictimPair(long qqID, long groupID) {
        this.qqID = qqID;
        this.groupID = groupID;
    }

    //重写equals和hashCode这样就可以用Set了
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VictimPair)) return false;
        VictimPair that = (VictimPair) o;
        return qqID == that.qqID && groupID == that.groupID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qqID, groupID);
    }
}
