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

public class EmergencyListener implements Listener {
    private final EssentialsProtect plugin;

    EmergencyListener(final EssentialsProtect essProtPlugin) {
        plugin = essProtPlugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(final BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFromTo(final BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(final BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(final EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPluginEnabled(final PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("Essentials")) {
            plugin.disableEmergencyMode();
        }
    }
}
