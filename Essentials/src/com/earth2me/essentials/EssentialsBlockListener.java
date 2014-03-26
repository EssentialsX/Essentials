package com.earth2me.essentials;

import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.utils.LocationUtil;
import java.util.Locale;
import net.ess3.api.IEssentials;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsBlockListener implements Listener
{
	private final transient IEssentials ess;

	public EssentialsBlockListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		// Do not rely on getItemInHand();
		// http://leaky.bukkit.org/issues/663
		final ItemStack is = LocationUtil.convertBlockToItem(event.getBlockPlaced());
		if (is == null)
		{
			return;
		}

		if (is.getType() == Material.MOB_SPAWNER && event.getItemInHand() != null && event.getPlayer() != null
			&& event.getItemInHand().getType() == Material.MOB_SPAWNER)
		{
			final BlockState blockState = event.getBlockPlaced().getState();
			if (blockState instanceof CreatureSpawner)
			{
				final CreatureSpawner spawner = (CreatureSpawner)blockState;
				final EntityType type = EntityType.fromId(event.getItemInHand().getData().getData());
				if (type != null && Mob.fromBukkitType(type) != null)
				{
					if (ess.getUser(event.getPlayer()).isAuthorized("essentials.spawnerconvert." + Mob.fromBukkitType(type).name().toLowerCase(Locale.ENGLISH)))
					{
						spawner.setSpawnedType(type);
					}
				}
			}
		}

		final User user = ess.getUser(event.getPlayer());
		if (user.hasUnlimited(is) && user.getGameMode() == GameMode.SURVIVAL)
		{
			ess.scheduleSyncDelayedTask(
					new Runnable()
			{
				@Override
				public void run()
				{
					user.getBase().getInventory().addItem(is);
					user.getBase().updateInventory();
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event)
	{
		final Block block = event.getRetractLocation().getBlock();
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP })
		{
			final Block search = block.getRelative(face, 1);
			final Material type = search.getType();
			if (type == Material.SIGN || type == Material.SIGN_POST)
			{
				final Sign sign = (Sign)(search.getState());
				for (final EssentialsSign esign : ess.getSettings().enabledSigns())
				{
					if (sign.getLine(0).equalsIgnoreCase(esign.getSuccessName()))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
