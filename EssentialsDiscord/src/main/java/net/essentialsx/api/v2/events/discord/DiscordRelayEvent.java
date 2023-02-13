package net.essentialsx.api.v2.events.discord;

import net.ess3.api.IUser;
import net.essentialsx.api.v2.services.discord.InteractionChannel;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Fired before a message is relayed to Minecraft.
 * <p>
 * Note: This event has no guarantee of the thread it is fired on, please use {@link #isAsynchronous()}} to see if this event is off the main Bukkit thread.
 */
public class DiscordRelayEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final InteractionMember member;
    private final InteractionChannel channel;
    private final List<String> groupNames;
    private final String rawMessage;
    private String formattedMessage;
    private final List<IUser> viewers;
    private boolean cancelled = false;

    /**
     * @param member            The member that sent the message.
     * @param channel           The channel the message was sent in.
     * @param groupNames        The message type keys which will be used to determine which player group the message should be sent to.
     * @param rawMessage        The raw message sent from Discord.
     * @param formattedMessage  The formatted message that will be sent to Minecraft.
     * @param viewers           The users that will see this relayed message.
     */
    public DiscordRelayEvent(final InteractionMember member, final InteractionChannel channel, final List<String> groupNames, final String rawMessage, final String formattedMessage, final List<IUser> viewers) {
        super(!Bukkit.isPrimaryThread());
        this.member = member;
        this.channel = channel;
        this.groupNames = groupNames;
        this.rawMessage = rawMessage;
        this.formattedMessage = formattedMessage;
        this.viewers = viewers;
    }

    /**
     * Gets the Discord member that sent the message.
     * @return The member that sent the message.
     */
    public InteractionMember getMember() {
        return member;
    }

    /**
     * Gets the Discord channel the message was sent in.
     * @return The channel the message was sent in.
     */
    public InteractionChannel getChannel() {
        return channel;
    }

    /**
     * Gets the message type group keys.
     * @return The message type group keys.
     */
    public List<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Gets the raw message sent from Discord.
     * @return The raw message sent from Discord.
     */
    public String getRawMessage() {
        return rawMessage;
    }

    /**
     * Gets the formatted message that will be sent to Minecraft.
     * @return The formatted message.
     */
    public String getFormattedMessage() {
        return formattedMessage;
    }

    /**
     * Sets the formatted message that will be sent to Minecraft.
     * @param formattedMessage The formatted message.
     */
    public void setFormattedMessage(final String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    /**
     * Gets the users that will be sent the relayed message.
     * The returned list is mutable. Removing a player from it will hide the message from them.
     * @return The mutable list of users.
     */
    public List<IUser> getViewers() {
        return viewers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
