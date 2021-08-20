package lielietea.mirai.plugin.admintools.blacklist;

import java.util.Date;

class BlockedContact {
    long id;
    String reason;
    Date blockedDate;
    String extraNote;

    public BlockedContact(long id, String reason, Date blockedDate) {
        this.id = id;
        this.reason = reason;
        this.blockedDate = blockedDate;
        extraNote = "";
    }

    public String getReason() {
        return reason;
    }

    public Date getBlockedDate() {
        return blockedDate;
    }

    public String getExtraNote() {
        return extraNote;
    }

    public long getId(){
        return id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setExtraNote(String extraNote) {
        this.extraNote = extraNote;
    }

    public void addExtraNote(String extraNote) {
        this.extraNote = this.extraNote + extraNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockedContact)) return false;
        BlockedContact that = (BlockedContact) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
