package com.earth2me.essentials.protect;

import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;


public class EssentialsProtectBlockListener implements Listener
{
	final private IProtect prot;
	final private IEssentials ess;

	public EssentialsProtectBlockListener(final IProtect parent)
	{
		this.prot = parent;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if (event.getBlock().getType() == Material.OBSIDIAN
			|| event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_portal_creation));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_fire_spread));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_flint_fire));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_fire_spread));
			return;
		}
		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lightning_fire_spread));
			return;
		}
		
		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FIREBALL))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_fireball_fire));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		final Block block = event.getBlock();

		if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_flow));
			return;
		}

		if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_flow));
			return;
		}

		if (block.getType() == Material.AIR)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBurn(final BlockBurnEvent event)
	{
		if (prot.getSettingBool(ProtectConfig.prevent_fire_spread))
		{
			event.setCancelled(true);
		}
	}
}
