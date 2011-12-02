package com.earth2me.essentials;

import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsBlockListener extends BlockListener
{
	private final IEssentials ess;
	private static final Logger logger = Logger.getLogger("Minecraft");

	public EssentialsBlockListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		// Do not rely on getItemInHand();
		// http://leaky.bukkit.org/issues/663
		final ItemStack is = Util.convertBlockToItem(event.getBlockPlaced());
		if (is == null)
		{
			return;
		}
		boolean unlimitedForUser = user.hasUnlimited(is);
		if (unlimitedForUser && user.getGameMode() == GameMode.SURVIVAL)
		{
			ess.scheduleSyncDelayedTask(
					new Runnable()
					{
						@Override
						public void run()
						{
							user.getInventory().addItem(is);
							user.updateInventory();
						}
					});
		}
	}
}
