package com.earth2me.essentials.protect;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;

@Deprecated
public class EmergencyBlockListener extends BlockListener
{

	@Override
	public void onBlockBurn(final BlockBurnEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		event.setCancelled(true);
	}
}
