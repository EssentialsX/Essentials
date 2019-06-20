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
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SignEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    ISign sign;
    EssentialsSign essSign;
    IUser user;

    /**
     * <p>Constructor for SignEvent.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param essSign a {@link com.earth2me.essentials.signs.EssentialsSign} object.
     * @param user a {@link net.ess3.api.IUser} object.
     */
    public SignEvent(final ISign sign, final EssentialsSign essSign, final IUser user) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.sign = sign;
        this.essSign = essSign;
        this.user = user;
    }

    /**
     * <p>Getter for the field <code>sign</code>.</p>
     *
     * @return a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     */
    public ISign getSign() {
        return sign;
    }

    /**
     * <p>getEssentialsSign.</p>
     *
     * @return a {@link com.earth2me.essentials.signs.EssentialsSign} object.
     */
    public EssentialsSign getEssentialsSign() {
        return essSign;
    }

    /**
     * <p>Getter for the field <code>user</code>.</p>
     *
     * @return a {@link net.ess3.api.IUser} object.
     */
    public IUser getUser() {
        return user;
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * <p>getHandlerList.</p>
     *
     * @return a {@link org.bukkit.event.HandlerList} object.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /** {@inheritDoc} */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
