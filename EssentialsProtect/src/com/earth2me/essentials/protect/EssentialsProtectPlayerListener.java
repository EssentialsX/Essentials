package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class EssentialsProtectPlayerListener extends PlayerListener
{
	private EssentialsProtect parent;
	private int signBlockX;
	private int signBlockY;
	private int signBlockZ;
	private EssentialsProtectData spData = null;

	public EssentialsProtectPlayerListener(EssentialsProtect parent)
	{
		Essentials.loadClasses();
		this.parent = parent;
	}

	public void initialize()
	{
		if (spData != null) return;
		spData = new EssentialsProtectData();
	}

	@Override
	public void onPlayerItem(PlayerItemEvent event)
	{
		if(event.isCancelled()) return;
		ItemStack item = event.getItem();
		User user = User.get(event.getPlayer());
		Block blockPlaced = event.getBlockClicked();
		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.usageList, item.getTypeId()) && !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (EssentialsProtect.onUseAlert.contains(String.valueOf(item.getTypeId())))
		{
			parent.alert(user, item.getType().toString(), "used: ");
		}

		if (item.getTypeId() == 323)
		{
			if (EssentialsProtect.genSettings.get("protect.protect.signs"))
			{
				if (user.isAuthorized("essentials.protect"))
				{

					signBlockX = blockPlaced.getX();
					signBlockY = blockPlaced.getY();
					signBlockZ = blockPlaced.getZ();

					initialize();
					spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), signBlockX,
												  signBlockY + 1, signBlockZ);

					if (EssentialsProtect.genSettings.get("protect.protect.block-below"))
					{
						spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), signBlockX,
													  signBlockY, signBlockZ);
					}
				}
			}
		}
	}
}
