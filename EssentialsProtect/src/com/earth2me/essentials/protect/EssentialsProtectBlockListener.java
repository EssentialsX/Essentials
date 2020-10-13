package com.earth2me.essentials.protect;

import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Set;

public class EssentialsProtectBlockListener implements Listener {

    private static final Set<Material> WATER_TYPES = EnumUtil.getAllMatching(Material.class, "WATER", "STATIONARY_WATER");
    private static final Set<Material> LAVA_TYPES = EnumUtil.getAllMatching(Material.class, "LAVA", "STATIONARY_LAVA");

    final private IProtect prot;

    EssentialsProtectBlockListener(final IProtect parent) {
        this.prot = parent;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (event.getBlock().getType() == Material.OBSIDIAN || event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN && prot.getSettingBool(ProtectConfig.prevent_portal_creation)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD) && prot.getSettingBool(ProtectConfig.prevent_fire_spread)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) && prot.getSettingBool(ProtectConfig.prevent_flint_fire)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA) && prot.getSettingBool(ProtectConfig.prevent_lava_fire_spread)) {
            event.setCancelled(true);
            return;
        }
        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING) && prot.getSettingBool(ProtectConfig.prevent_lightning_fire_spread)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FIREBALL) && prot.getSettingBool(ProtectConfig.prevent_fireball_fire)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Item) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                event.setCancelled(prot.getSettingBool(ProtectConfig.disable_lava_item_dmg));
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                if (event.getDamager() instanceof TNTPrimed && prot.getSettingBool(ProtectConfig.prevent_tnt_itemdmg)) {
                    event.setCancelled(true);
                }

                if (event.getDamager() instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_itemdmg)) {
                    event.setCancelled(true);
                }

                if (event.getDamager() instanceof ExplosiveMinecart && prot.getSettingBool(ProtectConfig.prevent_tntminecart_itemdmg)) {
                    event.setCancelled(true);
                }

                if (event.getDamager() instanceof WitherSkull && prot.getSettingBool(ProtectConfig.prevent_witherskull_itemdmg)) {
                    event.setCancelled(true);
                }

                if (event.getDamager() instanceof Fireball && prot.getSettingBool(ProtectConfig.prevent_fireball_itemdmg)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block block = event.getBlock();

        if (WATER_TYPES.contains(block.getType()) && prot.getSettingBool(ProtectConfig.prevent_water_flow)) {
            event.setCancelled(true);
            return;
        }

        if (LAVA_TYPES.contains(block.getType()) && prot.getSettingBool(ProtectConfig.prevent_lava_flow)) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.AIR && prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event) {
        if (prot.getSettingBool(ProtectConfig.prevent_fire_spread)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalLight(final PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.FIRE && prot.getSettingBool(ProtectConfig.prevent_portal_creation)) {
            event.setCancelled(true);
        }
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
        if (MaterialUtil.isBed(block.getType()) && !environment.equals(World.Environment.NORMAL) && prot.getSettingBool(ProtectConfig.prevent_bed_explosion)) {
            event.setCancelled(true);
        }
    }
}
