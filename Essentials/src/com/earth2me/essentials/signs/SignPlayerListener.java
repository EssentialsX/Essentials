package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import java.util.logging.Level;
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

	//This following code below listens to cancelled events to fix a bukkit issue
	//Right clicking signs with a block in hand, can now fire cancelled events.
	//This is because when the block place is cancelled (for example not enough space for the block to be placed),
	//the event will be marked as cancelled, thus preventing 30% of sign purchases.
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (ess.getSettings().areSignsDisabled() || (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR))
		{
			return;
		}
		final Block block;
		if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_AIR)
		{
			Block targetBlock = null;
			try
			{
				targetBlock = event.getPlayer().getTargetBlock(null, 5);
			}
			catch (IllegalStateException ex)
			{
				if (ess.getSettings().isDebug())
				{
					ess.getLogger().log(Level.WARNING, ex.getMessage(), ex);
				}
			}
			block = targetBlock;
		}
		else
		{
			block = event.getClickedBlock();
		}
		if (block == null)
		{
			return;
		}

		final int mat = block.getTypeId();
		if (mat == Material.SIGN_POST.getId() || mat == Material.WALL_SIGN.getId())
		{
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
