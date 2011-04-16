package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
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
		this.parent = parent;
	}

	public void initialize()
	{
		if (spData != null) return;
		spData = new EssentialsProtectData();
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		initialize();
		if (event.isCancelled()) return;
		ItemStack item = event.getItem();
		User user = User.get(event.getPlayer());
		Block blockClicked = event.getClickedBlock();

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			if(Essentials.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(ChatColor.RED + "You are not permitted to build");
			}
			event.setCancelled(true);
			return;
		}


		if (item != null && EssentialsProtect.checkProtectionItems(EssentialsProtect.usageList, item.getTypeId()) && !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (user.isAuthorized("essentials.protect.ownerinfo"))
		{
			String ownerName = spData.getBlockOwner(user.getWorld().getName(), user.getName(),
													blockClicked);
			if (ownerName != null)
			{
				user.sendMessage(ChatColor.GOLD + "[EssentialsProtect] Protection owner: "
								 + ownerName);
			}
		}
		if (item != null && EssentialsProtect.checkProtectionItems(EssentialsProtect.onUseAlert, item.getTypeId()))
		{
			parent.alert(user, item.getType().toString(), "used: ");
		}

		/*if (item != null && item.getTypeId() == 323)
		{
			if (EssentialsProtect.genSettings.get("protect.protect.signs"))
			{
				if (user.isAuthorized("essentials.protect"))
				{

					signBlockX = blockClicked.getX();
					signBlockY = blockClicked.getY();
					signBlockZ = blockClicked.getZ();

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
		}*/

	}
}
