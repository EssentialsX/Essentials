package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.MaterialUtil;
import net.ess3.api.IEssentials;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class EssentialsBlockListener implements Listener {
    private final transient IEssentials ess;

    public EssentialsBlockListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final ItemStack is = event.getItemInHand();

        if (is.getType() == MaterialUtil.SPAWNER && ess.getPersistentDataProvider().getString(is, "convert") != null) {
            final BlockState blockState = event.getBlockPlaced().getState();
            if (blockState instanceof CreatureSpawner) {
                final CreatureSpawner spawner = (CreatureSpawner) blockState;
                final EntityType type = ess.getSpawnerItemProvider().getEntityType(event.getItemInHand());
                if (type != null && Mob.fromBukkitType(type) != null) {
                    if (ess.getUser(event.getPlayer()).isAuthorized("essentials.spawnerconvert." + Mob.fromBukkitType(type).name().toLowerCase(Locale.ENGLISH))) {
                        spawner.setSpawnedType(type);
                        spawner.update();
                    }
                }
            }
        }

        final User user = ess.getUser(event.getPlayer());
        if (user.hasUnlimited(is) && user.getBase().getGameMode() == GameMode.SURVIVAL) {
            ess.scheduleSyncDelayedTask(() -> {
                if (is != null && is.getType() != null && !MaterialUtil.isAir(is.getType())) {
                    final ItemStack cloneIs = is.clone();
                    cloneIs.setAmount(1);
                    Inventories.addItem(user.getBase(), cloneIs);
                    user.getBase().updateInventory();
                }
            });
        }
    }
}
