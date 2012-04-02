package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
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

	public SignPlayerListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (ess.getSettings().areSignsDisabled())
		{
			return;
		}

		final Block block = event.getClickedBlock();
		if (block == null)
		{
			return;
		}

		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)
		{
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			{
				return;
			}
			final Sign csign = (Sign)block.getState();
			for (EssentialsSign sign : ess.getSettings().enabledSigns())
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
			for (EssentialsSign sign : ess.getSettings().enabledSigns())
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
