package net.essentialsx.api.v2.services.mail;

import java.util.UUID;

/**
 * An immutable representation of a message sent as mail.
 */
public class MailMessage {
    private final boolean read;
    private final boolean legacy;
    private final String senderName;
    private final UUID senderId;
    private final long timestamp;
    private final long expire;
    private final String message;

    public MailMessage(boolean read, boolean legacy, String sender, UUID uuid, long timestamp, long expire, String message) {
        this.read = read;
        this.legacy = legacy;
        this.senderName = sender;
        this.senderId = uuid;
        this.timestamp = timestamp;
        this.expire = expire;
        this.message = message;
    }

    /**
     * Checks if this message has been read by its recipient yet.
     * @return true if this message has been read.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Checks if this message was created via legacy api or converted from legacy format.
     *
     * A legacy messages only contains data for the read state and message.
     * @see #isRead()
     * @see #getMessage()
     * @return true if this message is a legacy message.
     */
    public boolean isLegacy() {
        return legacy;
    }

    /**
     * Gets the sender's username at the time of sending the message.
     * @return The sender's username.
     */
    public String getSenderUsername() {
        return senderName;
    }

    /**
     * Gets the sender's {@link UUID} or null if the sender does not have a UUID.
     * @return The sender's {@link UUID} or null.
     */
    public UUID getSenderUUID() {
        return senderId;
    }

    /**
     * Gets the millisecond epoch time when the message was sent.
     * @return The epoch time when message was sent.
     */
    public long getTimeSent() {
        return timestamp;
    }

    /**
     * Gets the millisecond epoch at which this message will expire and no longer been shown to the user.
     * @return The epoch time when the message will expire.
     */
    public long getTimeExpire() {
        return expire;
    }

    /**
     * Gets the message content for normal mail or the entire mail format for legacy mail.
     * @see #isLegacy()
     * @return The mail content or format.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Helper method to check if this mail has expired and should not been shown to the recipient.
     * @return true if this mail has expired.
     */
    public boolean isExpired() {
        if (getTimeExpire() == 0L) {
            return false;
        }
        return System.currentTimeMillis() >= getTimeExpire();
    }
}
