package com.earth2me.essentials.protect;

import com.earth2me.essentials.utils.EnumUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
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
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getBlock().getType() == Material.OBSIDIAN || event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_portal_creation));
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_fire_spread));
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_flint_fire));
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_fire_spread));
            return;
        }
        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lightning_fire_spread));
            return;
        }

        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FIREBALL)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_fireball_fire));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block block = event.getBlock();

        if (WATER_TYPES.contains(block.getType())) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_flow));
            return;
        }

        if (LAVA_TYPES.contains(block.getType())) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_flow));
            return;
        }

        if (block.getType() == Material.AIR) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event) {
        if (prot.getSettingBool(ProtectConfig.prevent_fire_spread)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalLight(PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.FIRE) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_portal_creation));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignDye(PlayerInteractEvent event) {
        Material clickedBlock = event.getClickedBlock().getBlockData().getMaterial();
        Action action = event.getAction();
        Material itemInHandMat = event.getPlayer().getInventory().getItemInMainHand().getType();

        // Check Right Click Action with Dye on Sign
        if ((clickedBlock == Material.ACACIA_SIGN ||
                clickedBlock == Material.ACACIA_WALL_SIGN ||
                clickedBlock == Material.SPRUCE_SIGN ||
                clickedBlock == Material.SPRUCE_WALL_SIGN ||
                clickedBlock == Material.BIRCH_SIGN ||
                clickedBlock == Material.BIRCH_WALL_SIGN ||
                clickedBlock == Material.DARK_OAK_SIGN ||
                clickedBlock == Material.DARK_OAK_WALL_SIGN ||
                clickedBlock == Material.JUNGLE_SIGN ||
                clickedBlock == Material.JUNGLE_WALL_SIGN ||
                clickedBlock == Material.OAK_SIGN ||
                clickedBlock == Material.OAK_WALL_SIGN) &&
                action == Action.RIGHT_CLICK_BLOCK &&
                (itemInHandMat == Material.BLACK_DYE ||
                        itemInHandMat == Material.BLUE_DYE ||
                        itemInHandMat == Material.BROWN_DYE ||
                        itemInHandMat == Material.CYAN_DYE ||
                        itemInHandMat == Material.GRAY_DYE ||
                        itemInHandMat == Material.GREEN_DYE ||
                        itemInHandMat == Material.LIGHT_BLUE_DYE ||
                        itemInHandMat == Material.LIGHT_GRAY_DYE ||
                        itemInHandMat == Material.LIME_DYE ||
                        itemInHandMat == Material.MAGENTA_DYE ||
                        itemInHandMat == Material.ORANGE_DYE ||
                        itemInHandMat == Material.PINK_DYE ||
                        itemInHandMat == Material.PURPLE_DYE ||
                        itemInHandMat == Material.RED_DYE ||
                        itemInHandMat == Material.WHITE_DYE ||
                        itemInHandMat == Material.YELLOW_DYE)) {
            event.setCancelled(prot.getSettingBool(ProtectConfig.disable_sign_dyeing));
        }
    }
}
