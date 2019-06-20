package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


/**
 * <p>TNTExplodeListener class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class TNTExplodeListener implements Listener, Runnable {
    private final transient IEssentials ess;
    private transient boolean enabled = false;
    private transient int timer = -1;

    /**
     * <p>Constructor for TNTExplodeListener.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     */
    public TNTExplodeListener(final IEssentials ess) {
        super();
        this.ess = ess;
    }

    /**
     * <p>enable.</p>
     */
    public void enable() {
        if (!enabled) {
            enabled = true;
            timer = ess.scheduleSyncDelayedTask(this, 200);
            return;
        }
        if (timer != -1) {
            ess.getScheduler().cancelTask(timer);
            timer = ess.scheduleSyncDelayedTask(this, 200);
        }
    }

    /**
     * <p>onEntityExplode.</p>
     *
     * @param event a {@link org.bukkit.event.entity.EntityExplodeEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (!enabled) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity) {
            return;
        }
        if (event.blockList().size() < 1) {
            return;
        }
        event.setCancelled(true);
        event.getLocation().getWorld().createExplosion(event.getLocation(), 0F);
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        enabled = false;
    }
}
