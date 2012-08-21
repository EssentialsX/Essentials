package com.earth2me.essentials.protect;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


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
	}
}
