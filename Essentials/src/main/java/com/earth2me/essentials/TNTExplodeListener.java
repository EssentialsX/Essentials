package com.earth2me.essentials;

import com.earth2me.essentials.commands.Commandnuke;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TNTExplodeListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) {
            return;
        }
        if (!event.getEntity().hasMetadata(Commandnuke.NUKE_META_KEY)) {
            return;
        }
        event.setCancelled(true);
        event.getLocation().getWorld().createExplosion(event.getLocation(), 0F);
    }
}
