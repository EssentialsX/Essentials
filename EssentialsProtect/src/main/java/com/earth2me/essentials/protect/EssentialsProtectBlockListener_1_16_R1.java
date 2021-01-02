package com.earth2me.essentials.protect;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EssentialsProtectBlockListener_1_16_R1 implements Listener {

    final private IProtect prot;

    EssentialsProtectBlockListener_1_16_R1(final IProtect parent) {
        this.prot = parent;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        final Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        final World.Environment environment = block.getWorld().getEnvironment();
        if (block.getType() == Material.RESPAWN_ANCHOR && !environment.equals(World.Environment.NETHER)) {
            final RespawnAnchor respawnAnchor = (RespawnAnchor) block.getBlockData();
            if ((respawnAnchor.getCharges() > 0 && (event.getItem() == null || event.getItem().getType() != Material.GLOWSTONE)) || respawnAnchor.getCharges() == respawnAnchor.getMaximumCharges()) {
                event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_respawn_anchor_explosion));
            }
        }
    }
}
