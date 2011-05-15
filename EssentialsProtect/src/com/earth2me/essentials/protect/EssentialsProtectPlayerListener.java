package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class EssentialsProtectPlayerListener extends PlayerListener
{
	private EssentialsProtect parent;
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
		User user = Essentials.getStatic().getUser(event.getPlayer());
		Block blockClicked = event.getClickedBlock();

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			if(Essentials.getStatic().getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(Util.i18n("buildAlert"));
			}
			event.setCancelled(true);
			return;
		}


		if (item != null && EssentialsProtect.checkProtectionItems(EssentialsProtect.usageList, item.getTypeId()) && !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && user.isAuthorized("essentials.protect.ownerinfo"))
		{
			String ownerName = spData.getBlockOwner(user.getWorld().getName(), user.getName(),
													blockClicked);
			if (ownerName != null)
			{
				user.sendMessage(Util.format("protectionOwner", ownerName));
			}
		}
		if (item != null && EssentialsProtect.checkProtectionItems(EssentialsProtect.onUseAlert, item.getTypeId()))
		{
			parent.alert(user, item.getType().toString(), Util.i18n("alertUsed"));
		}
	}
}
