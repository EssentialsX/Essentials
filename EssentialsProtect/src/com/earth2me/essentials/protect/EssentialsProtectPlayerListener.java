package com.earth2me.essentials.protect;

import com.earth2me.essentials.IEssentials;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class EssentialsProtectPlayerListener extends PlayerListener
{
	private final transient IProtect prot;
	private final transient IEssentials ess;

	public EssentialsProtectPlayerListener(final IProtect prot)
	{
		this.prot = prot;
		this.ess = prot.getEssentials();
	}

	@Override
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());

		if (prot.getSettingBool(ProtectConfig.disable_build) && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}

		final ItemStack item = event.getItem();
		if (item != null
			&& prot.checkProtectionItems(ProtectConfig.blacklist_usage, item.getTypeId())
			&& !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (user.isAuthorized("essentials.protect.admin"))
		{
			final StringBuilder stringBuilder = new StringBuilder();
			boolean first = true;
			final Block blockClicked = event.getClickedBlock();
			for (String owner : prot.getStorage().getOwners(blockClicked))
			{
				if (!first)
				{
					stringBuilder.append(", ");
				}
				first = false;
				stringBuilder.append(owner);
			}
			final String ownerNames = stringBuilder.toString();
			if (ownerNames != null && !ownerNames.isEmpty())
			{
				user.sendMessage(Util.format("protectionOwner", ownerNames));
			}
		}
		if (item != null
			&& prot.checkProtectionItems(ProtectConfig.alert_on_use, item.getTypeId()))
		{
			prot.alert(user, item.getType().toString(), Util.i18n("alertUsed"));
		}
	}
}
