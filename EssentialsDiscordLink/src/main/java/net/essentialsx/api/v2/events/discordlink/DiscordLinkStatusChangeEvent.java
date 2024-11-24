package net.essentialsx.api.v2.events.discordlink;

import net.ess3.api.IUser;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a User's link status has changed.
 */
public class DiscordLinkStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final InteractionMember member;
    private final String memberId;
    private final boolean state;
    private final Cause cause;

    public DiscordLinkStatusChangeEvent(IUser user, InteractionMember member, String memberId, boolean state, Cause cause) {
        this.user = user;
        this.member = member;
        this.memberId = memberId;
        this.state = state;
        this.cause = cause;
    }

    /**
     * Gets the Essentials {@link IUser user} whose link status has been changed in this event.
     * @return the user.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * Gets the Discord {@link InteractionMember member} whose link status has been changed in this event.
     * <p>
     * This will return {@code null} if {@link #getCause()} returns {@link Cause#UNSYNC_LEAVE}.
     * @see #getCause()
     * @see #getMemberId()
     * @return the member or null.
     */
    public InteractionMember getMember() {
        return member;
    }

    /**
     * Gets the ID of the Discord member whose link status has been changed in this event.
     * <p>
     * Unlink {@link #getMember()}, this method will never return null.
     * @return the member's id.
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Gets the new link status of this {@link #getUser() user} after this event.
     * @return true if the user is linked to a discord account.
     */
    public boolean isLinked() {
        return state;
    }

    /**
     * The cause which triggered this event.
     * @see Cause
     * @return the cause.
     */
    public Cause getCause() {
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The cause of the link status change.
     */
    public enum Cause {
        /**
         * Used when a player successfully completes an account link with the /link account in Minecraft.
         */
        SYNC_PLAYER,
        /**
         * Used when a player is linked via an external plugin using API.
         */
        SYNC_API,
        /**
         * Used when a player unlinks their account via the /unlink Discord or Minecraft command.
         */
        UNSYNC_PLAYER,
        /**
         * Used when a player is unlinked via an external plugin using API.
         */
        UNSYNC_API,
        /**
         * Used when a player is unlinked due to them leaving the Discord server.
         */
        UNSYNC_LEAVE,
    }
}
