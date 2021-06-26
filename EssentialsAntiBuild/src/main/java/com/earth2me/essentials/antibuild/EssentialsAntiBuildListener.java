package com.earth2me.essentials.antibuild;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsAntiBuildListener implements Listener {
    private static final Logger logger = Logger.getLogger("EssentialsAntiBuild");
    final private transient IAntiBuild prot;
    final private transient IEssentials ess;

    EssentialsAntiBuildListener(final IAntiBuild parent) {
        this.prot = parent;
        this.ess = prot.getEssentialsConnect().getEssentials();

        if (isEntityPickupEvent()) {
            ess.getServer().getPluginManager().registerEvents(new EntityPickupItemListener(), prot);
        } else {
            ess.getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(), prot);
        }
    }

    private static boolean isEntityPickupEvent() {
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            return true;
        } catch (final ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean metaPermCheck(final User user, final String action, final Block block) {
        if (block == null) {
            if (ess.getSettings().isDebug()) {
                logger.log(Level.INFO, "AntiBuild permission check failed, invalid block.");
            }
            return false;
        }
        return metaPermCheck(user, action, block.getType(), block.getData());
    }

    public boolean metaPermCheck(final User user, final String action, final Material material) {
        final String blockPerm = "essentials.build." + action + "." + material;
        return user.isAuthorized(blockPerm);
    }

    private boolean metaPermCheck(final User user, final String action, final Material material, final short data) {
        final String blockPerm = "essentials.build." + action + "." + material;
        final String dataPerm = blockPerm + ":" + data;

        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
            if (user.getBase().isPermissionSet(dataPerm)) {
                return user.isAuthorized(dataPerm);
            } else {
                if (ess.getSettings().isDebug()) {
                    logger.log(Level.INFO, "DataValue perm on " + user.getName() + " is not directly set: " + dataPerm);
                }
            }
        }

        return user.isAuthorized(blockPerm);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final User user = ess.getUser(event.getPlayer());
        final Block block = event.getBlockPlaced();
        final Material type = block.getType();

        // Happens with blocks like eyes of ender, we shouldn't treat state changes as a block place.
        if (type.equals(event.getBlockReplacedState().getType())) {
            return;
        }

        if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !metaPermCheck(user, "place", block)) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", EssentialsAntiBuild.getNameForType(type)));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_placement, type) && !user.isAuthorized("essentials.protect.exemptplacement")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", EssentialsAntiBuild.getNameForType(type)));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.alert_on_placement, type) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, EssentialsAntiBuild.getNameForType(type), tl("alertPlaced"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final User user = ess.getUser(event.getPlayer());
        final Block block = event.getBlock();
        final Material type = block.getType();

        if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !metaPermCheck(user, "break", block)) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildBreak", EssentialsAntiBuild.getNameForType(type)));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_break, type) && !user.isAuthorized("essentials.protect.exemptbreak")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildBreak", EssentialsAntiBuild.getNameForType(type)));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.alert_on_break, type) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, EssentialsAntiBuild.getNameForType(type), tl("alertBroke"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(final HangingBreakByEntityEvent event) {
        final Entity entity = event.getRemover();
        if (entity instanceof Player) {
            final User user = ess.getUser((Player) entity);
            final EntityType type = event.getEntity().getType();
            final boolean warn = ess.getSettings().warnOnBuildDisallow();
            if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild()) {
                if (type == EntityType.PAINTING && !metaPermCheck(user, "break", Material.PAINTING)) {
                    if (warn) {
                        user.sendMessage(tl("antiBuildBreak", Material.PAINTING.toString()));
                    }
                    event.setCancelled(true);
                } else if (type == EntityType.ITEM_FRAME && !metaPermCheck(user, "break", Material.ITEM_FRAME)) {
                    if (warn) {
                        user.sendMessage(tl("antiBuildBreak", Material.ITEM_FRAME.toString()));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemFrameInteract(final PlayerInteractEntityEvent event) {
        final User user = ess.getUser(event.getPlayer());

        if (!(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }

        if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build") && !metaPermCheck(user, "place", Material.ITEM_FRAME)) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", Material.ITEM_FRAME.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_placement, Material.ITEM_FRAME) && !user.isAuthorized("essentials.protect.exemptplacement")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", Material.ITEM_FRAME.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.alert_on_placement, Material.ITEM_FRAME) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, Material.ITEM_FRAME.toString(), tl("alertPlaced"));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStandInteract(final PlayerInteractAtEntityEvent event) {
        final User user = ess.getUser(event.getPlayer());

        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build") && !metaPermCheck(user, "place", Material.ARMOR_STAND)) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", Material.ARMOR_STAND.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_placement, Material.ARMOR_STAND) && !user.isAuthorized("essentials.protect.exemptplacement")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildPlace", Material.ARMOR_STAND.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.alert_on_placement, Material.ARMOR_STAND) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, Material.ARMOR_STAND.toString(), tl("alertPlaced"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockEntityDamage(final EntityDamageByEntityEvent event) {
        Player player = null;

        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }

        final User user = ess.getUser(player);
        Material type = null;

        if (event.getEntity() instanceof ItemFrame) {
            type = Material.ITEM_FRAME;
        } else if (event.getEntity() instanceof ArmorStand) {
            type = Material.ARMOR_STAND;
        } else if (event.getEntity() instanceof EnderCrystal) {
            type = Material.END_CRYSTAL;
        } else {
            return;
        }

        if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build") && !metaPermCheck(user, "break", type)) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildBreak", type.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_break, type) && !user.isAuthorized("essentials.protect.exemptbreak")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildBreak", type.toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (prot.checkProtectionItems(AntiBuildConfig.alert_on_break, type) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, type.toString(), tl("alertBroke"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            if (prot.checkProtectionItems(AntiBuildConfig.blacklist_piston, block.getType())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        for (final Block block : event.getBlocks()) {
            if (prot.checkProtectionItems(AntiBuildConfig.blacklist_piston, block.getType())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        // Do not return if cancelled, because the interact event has 2 cancelled states.
        final User user = ess.getUser(event.getPlayer());
        final ItemStack item = event.getItem();

        if (item != null && prot.checkProtectionItems(AntiBuildConfig.blacklist_usage, item.getType()) && !user.isAuthorized("essentials.protect.exemptusage")) {
            if (ess.getSettings().warnOnBuildDisallow()) {
                user.sendMessage(tl("antiBuildUse", item.getType().toString()));
            }
            event.setCancelled(true);
            return;
        }

        if (item != null && prot.checkProtectionItems(AntiBuildConfig.alert_on_use, item.getType()) && !user.isAuthorized("essentials.protect.alerts.notrigger")) {
            prot.getEssentialsConnect().alert(user, item.getType().toString(), tl("alertUsed"));
        }

        if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild()) {
            if (event.hasItem() && !metaPermCheck(user, "interact", item.getType(), item.getDurability())) {
                event.setCancelled(true);
                if (ess.getSettings().warnOnBuildDisallow()) {
                    user.sendMessage(tl("antiBuildUse", item.getType().toString()));
                }
                return;
            }
            if (event.hasBlock() && !metaPermCheck(user, "interact", event.getClickedBlock())) {
                event.setCancelled(true);
                if (ess.getSettings().warnOnBuildDisallow()) {
                    user.sendMessage(tl("antiBuildInteract", event.getClickedBlock().getType().toString()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCraftItemEvent(final CraftItemEvent event) {
        final HumanEntity entity = event.getWhoClicked();

        if (entity instanceof Player) {
            final User user = ess.getUser((Player) entity);
            final ItemStack item = event.getRecipe().getResult();

            if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild()) {
                if (!metaPermCheck(user, "craft", item.getType(), item.getDurability())) {
                    event.setCancelled(true);
                    if (ess.getSettings().warnOnBuildDisallow()) {
                        user.sendMessage(tl("antiBuildCraft", item.getType().toString()));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {

        final User user = ess.getUser(event.getPlayer());
        final ItemStack item = event.getItemDrop().getItemStack();

        if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild()) {
            if (!metaPermCheck(user, "drop", item.getType(), item.getDurability())) {
                event.setCancelled(true);
                user.getBase().updateInventory();
                if (ess.getSettings().warnOnBuildDisallow()) {
                    user.sendMessage(tl("antiBuildDrop", item.getType().toString()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(final BlockDispenseEvent event) {
        final ItemStack item = event.getItem();
        if (prot.checkProtectionItems(AntiBuildConfig.blacklist_dispenser, item.getType())) {
            event.setCancelled(true);
        }
    }

    private class EntityPickupItemListener implements Listener {
        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onPlayerPickupItem(final EntityPickupItemEvent event) {
            if (!(event.getEntity() instanceof Player)) return;

            final User user = ess.getUser((Player) event.getEntity());
            final ItemStack item = event.getItem().getItemStack();

            if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild()) {
                if (!metaPermCheck(user, "pickup", item.getType(), item.getDurability())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private class PlayerPickupItemListener implements Listener {
        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onPlayerPickupItem(final PlayerPickupItemEvent event) {

            final User user = ess.getUser(event.getPlayer());
            final ItemStack item = event.getItem().getItemStack();

            if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild()) {
                if (!metaPermCheck(user, "pickup", item.getType(), item.getDurability())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
