package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsProtectPlayerListener extends PlayerListener
{
	private EssentialsProtect parent;
	
	public EssentialsProtectPlayerListener(EssentialsProtect parent)
	{
		Essentials.loadClasses();
		this.parent = parent;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled()) return;
		User user = User.get(event.getPlayer());

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}
		
		if (user.isAuthorized("essentials.protect.admin"))
		{
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String owner : EssentialsProtect.getStorage().getOwners(event.getClickedBlock())) {
				if (!first) {
					sb.append(", ");
				}
				first = false;
				sb.append(owner);
			}
			String ownerNames = sb.toString();
			if (ownerNames != null)
			{
				user.sendMessage(ChatColor.GOLD + "[EssentialsProtect] Protection owners: " + ownerNames);
			}
		}
	}

	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if(event.isCancelled()) return;
		ItemStack item = event.getItem().getItemStack();
		User user = User.get(event.getPlayer());
		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.usageList, item.getTypeId()) && !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (EssentialsProtect.onUseAlert.contains(String.valueOf(item.getTypeId())))
		{
			parent.alert(user, item.getType().toString(), "used: ");
		}
	}
}
