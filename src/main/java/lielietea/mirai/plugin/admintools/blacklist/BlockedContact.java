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

    String getReason() {
        return reason;
    }

    Date getBlockedDate() {
        return blockedDate;
    }

    String getExtraNote() {
        return extraNote;
    }

    long getId(){
        return id;
    }

    void setReason(String reason) {
        this.reason = reason;
    }

    void setExtraNote(String extraNote) {
        this.extraNote = "["+ new Date() + "] "+ extraNote + "\n";
    }

    void addExtraNote(String extraNote) {
        this.extraNote = this.extraNote + "["+ new Date() + "] "+ extraNote + "\n";
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
