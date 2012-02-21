package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignPlayerListener implements Listener
{
	private final transient IEssentials ess;
	private final transient ISignsPlugin plugin;

	public SignPlayerListener(final IEssentials ess, final ISignsPlugin plugin)
	{
		this.ess = ess;
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		final Block block = event.getClickedBlock();
		if (block == null)
		{
			return;
		}
		final int mat = block.getTypeId();
		if (mat == Material.SIGN_POST.getId() || mat == Material.WALL_SIGN.getId())
		{
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			{
				return;
			}
			final Sign csign = (Sign)block.getState();
			for (EssentialsSign sign : plugin.getSettings().getEnabledSigns())
			{
				if (csign.getLine(0).equalsIgnoreCase(sign.getSuccessName()))
				{
					sign.onSignInteract(block, event.getPlayer(), ess);
					event.setCancelled(true);
					return;
				}
			}
		}
		else
		{
			for (EssentialsSign sign : plugin.getSettings().getEnabledSigns())
			{
				if (sign.getBlocks().contains(block.getType())
					&& !sign.onBlockInteract(block, event.getPlayer(), ess))
				{
					event.setCancelled(true);
					return;

				}
			}
		}
	}
}
