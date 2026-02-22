package net.essentialsx.api.v2.events.discord;

import net.essentialsx.api.v2.services.discord.InteractionMember;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a Discord member's profile is updated.
 * <p>
 * Note: This event is always fired asynchronously.
 */
public class DiscordMemberUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final InteractionMember member;
    private final String oldNickname;
    private final String newNickname;

    public DiscordMemberUpdateEvent(final InteractionMember member, final String oldNickname, final String newNickname) {
        super(!Bukkit.isPrimaryThread());
        this.member = member;
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    /**
     * Gets the Discord member whose profile was updated.
     * @return the member.
     */
    public InteractionMember getMember() {
        return member;
    }

    /**
     * Gets the member's old nickname, or null if they had no nickname.
     * @return the old nickname or null.
     */
    @Nullable
    public String getOldNickname() {
        return oldNickname;
    }

    /**
     * Gets the member's new nickname, or null if they no longer have a nickname.
     * @return the new nickname or null.
     */
    @Nullable
    public String getNewNickname() {
        return newNickname;
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
