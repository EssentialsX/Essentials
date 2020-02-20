package net.ess3.api.events;

public interface TimestampEvent {
    long getTimestamp();
    void setTimestamp(long timestamp);
}
