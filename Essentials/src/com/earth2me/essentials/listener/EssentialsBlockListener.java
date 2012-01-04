package com.earth2me.essentials.listener;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsBlockListener extends BlockListener
{
	private final transient IEssentials ess;

	public EssentialsBlockListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	@Override
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		// Do not rely on getItemInHand();
		// http://leaky.bukkit.org/issues/663
		final ItemStack itemstack = Util.convertBlockToItem(event.getBlockPlaced());
		if (itemstack == null)
		{
			return;
		}
		final IUser user = ess.getUser(event.getPlayer());
		final boolean unlimitedForUser = user.getData().hasUnlimited(itemstack.getType());
		if (unlimitedForUser && user.getGameMode() == GameMode.SURVIVAL)
		{
			ess.scheduleSyncDelayedTask(
					new Runnable()
					{
						@Override
						public void run()
						{
							user.getInventory().addItem(itemstack);
							user.updateInventory();
						}
					});
		}
	}
}
