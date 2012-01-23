package com.earth2me.essentials.protect;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsProtectPlayerListener implements Listener
{
	private final transient IProtect prot;
	private final transient IEssentials ess;

	public EssentialsProtectPlayerListener(final IProtect prot)
	{
		this.prot = prot;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		// Do not return if cancelled, because the interact event has 2 cancelled states.
		final User user = ess.getUser(event.getPlayer());

		if (event.hasItem()
			&& (event.getItem().getType() == Material.WATER_BUCKET
				|| event.getItem().getType() == Material.LAVA_BUCKET)
			&& prot.getSettingBool(ProtectConfig.disable_build) && !user.canBuild())
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("buildAlert"));
			}
			event.setCancelled(true);
			return;
		}

		if (prot.getSettingBool(ProtectConfig.disable_use) && !user.canBuild())
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("buildAlert"));
			}
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

		if (user.isAuthorized("essentials.protect.ownerinfo") && event.getAction() == Action.RIGHT_CLICK_BLOCK)
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
				user.sendMessage(_("protectionOwner", ownerNames));
			}
		}
		if (item != null
			&& !user.hasPermission("essentials.protect.alerts.notrigger")
			&& prot.checkProtectionItems(ProtectConfig.alert_on_use, item.getTypeId()))
		{
			prot.getEssentialsConnect().alert(user, item.getType().toString(), _("alertUsed"));
		}
	}
}
