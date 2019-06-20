package com.earth2me.essentials.messaging;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;

import java.lang.ref.WeakReference;

import static com.earth2me.essentials.I18n.tl;

/**
 * Represents a simple reusable implementation of {@link com.earth2me.essentials.messaging.IMessageRecipient}. This class provides functionality for the following methods:
 * <ul>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#sendMessage(IMessageRecipient, String)}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#onReceiveMessage(IMessageRecipient, String)}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#getReplyRecipient()}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#setReplyRecipient(IMessageRecipient)}</li>
 * </ul>
 *
 * <b>The given {@code parent} must implement the following methods to prevent overflow:</b>
 * <ul>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#sendMessage(String)}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#getName()}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#getDisplayName()}</li>
 *     <li>{@link com.earth2me.essentials.messaging.IMessageRecipient#isReachable()}</li>
 * </ul>
 *
 * The reply-recipient is wrapped in a {@link java.lang.ref.WeakReference}.
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SimpleMessageRecipient implements IMessageRecipient {

    private final IEssentials ess;
    private final IMessageRecipient parent;

    private long lastMessageMs;
    private WeakReference<IMessageRecipient> replyRecipient;

    /**
     * <p>getUser.</p>
     *
     * @param recipient a {@link com.earth2me.essentials.messaging.IMessageRecipient} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    protected static User getUser(IMessageRecipient recipient) {
        if (recipient instanceof SimpleMessageRecipient) {
            return ((SimpleMessageRecipient) recipient).parent instanceof User ? (User) ((SimpleMessageRecipient) recipient).parent : null;
        }
        return recipient instanceof User ? (User) recipient : null;
    }

    /**
     * <p>Constructor for SimpleMessageRecipient.</p>
     *
     * @param ess a {@link com.earth2me.essentials.IEssentials} object.
     * @param parent a {@link com.earth2me.essentials.messaging.IMessageRecipient} object.
     */
    public SimpleMessageRecipient(IEssentials ess, IMessageRecipient parent) {
        this.ess = ess;
        this.parent = parent;
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String message) {
        this.parent.sendMessage(message);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.parent.getName();
    }

    /** {@inheritDoc} */
    @Override public String getDisplayName() {
        return this.parent.getDisplayName();
    }

    /** {@inheritDoc} */
    @Override public MessageResponse sendMessage(IMessageRecipient recipient, String message) {
        MessageResponse messageResponse = recipient.onReceiveMessage(this.parent, message);
        switch (messageResponse) {
            case UNREACHABLE:
                sendMessage(tl("recentlyForeverAlone", recipient.getDisplayName()));
                break;
            case MESSAGES_IGNORED:
                sendMessage(tl("msgIgnore", recipient.getDisplayName()));
                break;
            case SENDER_IGNORED:
                break;
            // When this recipient is AFK, notify the sender. Then, proceed to send the message.
            case SUCCESS_BUT_AFK:
                // Currently, only IUser can be afk, so we unsafely cast to get the afk message.
                if (((IUser) recipient).getAfkMessage() != null) {
                    sendMessage(tl("userAFKWithMessage", recipient.getDisplayName(), ((IUser) recipient).getAfkMessage()));
                } else {
                    sendMessage(tl("userAFK", recipient.getDisplayName()));
                }
            default:
                sendMessage(tl("msgFormat", tl("me"), recipient.getDisplayName(), message));

                // Better Social Spy
                User senderUser = getUser(this);
                User recipientUser = getUser(recipient);
                if (senderUser != null // not null if player.
                    // Dont spy on chats involving socialspy exempt players
                    && !senderUser.isAuthorized("essentials.chat.spy.exempt")
                    && (recipientUser != null && !recipientUser.isAuthorized("essentials.chat.spy.exempt"))) {
                    for (User onlineUser : ess.getOnlineUsers()) {
                        if (onlineUser.isSocialSpyEnabled()
                            // Don't send socialspy messages to message sender/receiver to prevent spam
                            && !onlineUser.equals(senderUser)
                            && !onlineUser.equals(recipient)) {
                            if (senderUser.isMuted() && ess.getSettings().getSocialSpyListenMutedPlayers()) {
                                onlineUser.sendMessage(tl("socialMutedSpyPrefix") + tl("socialSpyMsgFormat", getDisplayName(), recipient.getDisplayName(), message));
                            } else {
                                onlineUser.sendMessage(tl("socialSpyPrefix") + tl("socialSpyMsgFormat", getDisplayName(), recipient.getDisplayName(), message));
                            }
                        }
                    }
                }
        }
        // If the message was a success, set this sender's reply-recipient to the current recipient.
        if (messageResponse.isSuccess()) {
            setReplyRecipient(recipient);
        }
        return messageResponse;
    }

    /** {@inheritDoc} */
    @Override
    public MessageResponse onReceiveMessage(IMessageRecipient sender, String message) {
        if (!isReachable()) {
            return MessageResponse.UNREACHABLE;
        }

        User user = getUser(this);
        boolean afk = false;
        boolean isLastMessageReplyRecipient = ess.getSettings().isLastMessageReplyRecipient();
        if (user != null) {
            if (user.isIgnoreMsg() && sender instanceof IUser && !((IUser) sender).isAuthorized("essentials.msgtoggle.bypass")) { // Don't ignore console and senders with permission
                return MessageResponse.MESSAGES_IGNORED;
            }
            afk = user.isAfk();
            isLastMessageReplyRecipient = user.isLastMessageReplyRecipient();
            // Check whether this recipient ignores the sender, only if the sender is not the console.
            if (sender instanceof IUser && user.isIgnoredPlayer((IUser) sender)) {
                return MessageResponse.SENDER_IGNORED;
            }
        }
        // Display the formatted message to this recipient.
        sendMessage(tl("msgFormat", sender.getDisplayName(), tl("me"), message));

        if (isLastMessageReplyRecipient) {
            // If this recipient doesn't have a reply recipient, initiate by setting the first
            // message sender to this recipient's replyRecipient.
            long timeout = ess.getSettings().getLastMessageReplyRecipientTimeout() * 1000;
            if (getReplyRecipient() == null || !getReplyRecipient().isReachable()
                || System.currentTimeMillis() - this.lastMessageMs > timeout) {
                setReplyRecipient(sender);
            }
        } else { // Old message functionality, always set the reply recipient to the last person who sent us a message.
            setReplyRecipient(sender);
        }
        this.lastMessageMs = System.currentTimeMillis();
        return afk ? MessageResponse.SUCCESS_BUT_AFK : MessageResponse.SUCCESS;
    }

    /** {@inheritDoc} */
    @Override public boolean isReachable() {
        return this.parent.isReachable();
    }

    /**
     * {@inheritDoc}
     *
     * <b>This {@link com.earth2me.essentials.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public IMessageRecipient getReplyRecipient() {
        return replyRecipient == null ? null : replyRecipient.get();
    }

    /**
     * {@inheritDoc}
     *
     * <b>This {@link com.earth2me.essentials.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public void setReplyRecipient(final IMessageRecipient replyRecipient) {
        this.replyRecipient = new WeakReference<>(replyRecipient);
    }
}
