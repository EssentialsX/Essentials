package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.signs.EssentialsSign.ISign;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * This handles common boilerplate for other SignEvent
 */
public class SignEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    ISign sign;
    EssentialsSign essSign;
    IUser user;

    public SignEvent(final ISign sign, final EssentialsSign essSign, final IUser user) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.sign = sign;
        this.essSign = essSign;
        this.user = user;
    }

    public ISign getSign() {
        return sign;
    }

    public EssentialsSign getEssentialsSign() {
        return essSign;
    }

    public IUser getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
