package com.earth2me.essentials;

public class Offence {

    public enum Type {
        BAN, TEMPBAN, BANIP, TEMPBANIP, MUTE, TEMPMUTE, KICK
    }

    private final Type type;
    private final String targetName;
    private final String staffName;
    private final String reason;
    private final long timestamp;
    private final String extra;

    public Offence(final Type type, final String targetName, final String staffName, final String reason, final long timestamp, final String extra) {
        this.type = type;
        this.targetName = targetName;
        this.staffName = staffName;
        this.reason = reason;
        this.timestamp = timestamp;
        this.extra = extra;
    }

    public Type getType() {
        return type;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getExtra() {
        return extra;
    }
}


