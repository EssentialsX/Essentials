package com.earth2me.essentials.protect;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;

public class EssentialsProtectPlayerListener extends PlayerListener
{
	private EssentialsProtect parent;

	public EssentialsProtectPlayerListener(EssentialsProtect parent)
	{
		
		this.parent = parent;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled()) return;
		ItemStack item = event.getItem();
		User user = parent.ess.getUser(event.getPlayer());
		Block blockClicked = event.getClickedBlock();

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}


		if (item != null && EssentialsProtect.checkProtectionItems(EssentialsProtect.usageList, item.getTypeId()) && !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (user.isAuthorized("essentials.protect.admin"))
		{
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String owner : EssentialsProtect.getStorage().getOwners(blockClicked))
			{
				if (!first)
				{
					sb.append(", ");
				}
				first = false;
				sb.append(owner);
			}
			String ownerNames = sb.toString();
			if (ownerNames != null && !ownerNames.isEmpty())
			{
				user.sendMessage(Util.format("protectionOwner", ownerNames));
			}
		}
		if (item != null && EssentialsProtect.onUseAlert.contains(String.valueOf(item.getTypeId())))
		{
			parent.alert(user, item.getType().toString(), Util.i18n("alertUsed"));
		}
	}
}
