package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IUser;
import lombok.Cleanup;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtphere extends EssentialsCommand
{
	public Commandtphere()
	{
		super("tphere");
	}

	@Override
	public void run(final Server server, final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		@Cleanup
		final IUser player = getPlayer(server, args, 0);
		player.acquireReadLock();
		if (!player.getData().isTeleportEnabled())
		{
			throw new Exception(_("teleportDisabled", player.getDisplayName()));
		}
		player.getTeleport().teleport(user, new Trade(this.getName(), ess), TeleportCause.COMMAND);
		user.sendMessage(_("teleporting"));
		player.sendMessage(_("teleporting"));
		throw new NoChargeException();
	}
}
