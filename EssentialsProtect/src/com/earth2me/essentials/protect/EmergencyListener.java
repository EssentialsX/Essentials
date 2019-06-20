package com.earth2me.essentials.protect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;


/**
 * <p>EmergencyListener class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class EmergencyListener implements Listener {
    private EssentialsProtect plugin;

    EmergencyListener(final EssentialsProtect essProtPlugin) {
        plugin = essProtPlugin;
    }

    /**
     * <p>onBlockBurn.</p>
     *
     * @param event a {@link org.bukkit.event.block.BlockBurnEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(final BlockBurnEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onBlockIgnite.</p>
     *
     * @param event a {@link org.bukkit.event.block.BlockIgniteEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onBlockFromTo.</p>
     *
     * @param event a {@link org.bukkit.event.block.BlockFromToEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFromTo(final BlockFromToEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onBlockBreak.</p>
     *
     * @param event a {@link org.bukkit.event.block.BlockBreakEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(final BlockBreakEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onPlayerJoin.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerJoinEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
    }

    /**
     * <p>onEntityExplode.</p>
     *
     * @param event a {@link org.bukkit.event.entity.EntityExplodeEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onEntityDamage.</p>
     *
     * @param event a {@link org.bukkit.event.entity.EntityDamageEvent} object.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(final EntityDamageEvent event) {
        event.setCancelled(true);
    }

    /**
     * <p>onPluginEnabled.</p>
     *
     * @param event a {@link org.bukkit.event.server.PluginEnableEvent} object.
     */
    @EventHandler
    public void onPluginEnabled(final PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("Essentials")) {
            plugin.disableEmergencyMode();
        }
    }
}
