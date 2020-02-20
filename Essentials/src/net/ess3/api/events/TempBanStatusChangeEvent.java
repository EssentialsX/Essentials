package net.ess3.api.events;

import net.ess3.api.IUser;

public class TempBanStatusChangeEvent extends BanStatusChangeEvent implements TimestampEvent {
    private long timestamp;

    public TempBanStatusChangeEvent(IUser affected, IUser controller, String reason, long banTimestamp) {
        super(affected, controller, true, reason);
        this.timestamp = banTimestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
