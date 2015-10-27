package com.earth2me.essentials.messaging;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;

import java.lang.ref.WeakReference;

/**
 * Represents a simple reusable implementation of {@link IMessageRecipient}. This class provides functionality for the following methods:
 * <ul>
 *     <li>{@link IMessageRecipient#sendMessage(IMessageRecipient, String)}</li>
 *     <li>{@link IMessageRecipient#onReceiveMessage(IMessageRecipient, String)}</li>
 *     <li>{@link IMessageRecipient#getReplyRecipient()}</li>
 *     <li>{@link IMessageRecipient#setReplyRecipient(IMessageRecipient)}</li>
 * </ul>
 * 
 * <b>The given {@code parent} must implement the following methods to prevent overflow:</b>
 * <ul>
 *     <li>{@link IMessageRecipient#sendMessage(String)}</li>
 *     <li>{@link IMessageRecipient#getName()}</li>
 *     <li>{@link IMessageRecipient#getDisplayName()}</li>
 * </ul>
 * 
 * The reply-recipient is wrapped in a {@link WeakReference}.
 */
public class SimpleMessageRecipient implements IMessageRecipient {

    private final IEssentials ess;
    private final IMessageRecipient parent;
    
    private WeakReference<IMessageRecipient> replyRecipient;
    
    public SimpleMessageRecipient(IEssentials ess, IMessageRecipient parent) {
        this.ess = ess;
        this.parent = parent;
    }

    @Override
    public void sendMessage(String message) {
        this.parent.sendMessage(message);
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override public String getDisplayName() {
        return this.parent.getDisplayName();
    }

    @Override public MessageResponse sendMessage(IMessageRecipient recipient, String message) {
        MessageResponse messageResponse = recipient.onReceiveMessage(this.parent, message);
        switch (messageResponse) {
            case MESSAGES_IGNORED:
                sendMessage(tl("msgIgnore", getDisplayName()));
                break;
            case SENDER_IGNORED:
                break;
            // When this recipient is AFK, notify the sender. Then, proceed to send the message.
            case SUCCESS_BUT_AFK: // TODO double check this functionality!
                sendMessage(tl("userAFK", getDisplayName()));
            default:
                sendMessage(tl("msgFormat", tl("me"), recipient.getDisplayName(), message));
        }

        if (ess.getSettings().isLastMessageReplyRecipient()) {
            // If the message was a success, set this sender's reply-recipient to the current recipient.
            if (messageResponse.isSuccess()) {
                setReplyRecipient(recipient);
            }
        }
        return messageResponse;
    }

    @Override
    public MessageResponse onReceiveMessage(IMessageRecipient sender, String message) {
        User user = this.parent instanceof User ? (User) this.parent : null;
        boolean afk = false;
        if (user != null) {
            if (user.isIgnoreMsg()
                && !(sender instanceof Console)) { // Console must never be ignored.
                return MessageResponse.MESSAGES_IGNORED;
            }
            afk = user.isAfk();
            // Check whether this recipient ignores the sender, only if the sender is not the console.
            if (sender instanceof IUser && user.isIgnoredPlayer((IUser) sender)) {
                return MessageResponse.SENDER_IGNORED;
            }
        }
        // Display the formatted message to this recipient.
        sendMessage(tl("msgFormat", sender.getDisplayName(), tl("me"), message));

        if (ess.getSettings().isLastMessageReplyRecipient()) {
            // If this recipient doesn't have a reply recipient, initiate by setting the first
            // message sender to this recipient's replyRecipient.
            if (getReplyRecipient() == null) {
                setReplyRecipient(sender);
            }
        } else { // Old message functionality, always set the reply recipient to the last person who sent us a message.
            setReplyRecipient(sender);
        }
        return afk ? MessageResponse.SUCCESS_BUT_AFK : MessageResponse.SUCCESS;
    }

    /**
     * {@inheritDoc}
     * <p />
     * <b>This {@link com.earth2me.essentials.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public IMessageRecipient getReplyRecipient() {
        return replyRecipient == null ? null : replyRecipient.get();
    }

    /**
     * {@inheritDoc}
     * <p />
     * <b>This {@link com.earth2me.essentials.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public void setReplyRecipient(final IMessageRecipient replyRecipient) {
        this.replyRecipient = new WeakReference<>(replyRecipient);
    }
}
