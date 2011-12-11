package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import lombok.Cleanup;
import org.bukkit.Server;


public class Commandtpa extends EssentialsCommand
{
	public Commandtpa()
	{
		super("tpa");
	}

	@Override
	public void run(Server server, IUser user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		IUser player = getPlayer(server, args, 0);
		player.acquireReadLock();
		if (!player.getData().isTeleportEnabled())
		{
			throw new Exception(_("teleportDisabled", player.getDisplayName()));
		}
		if (!player.isIgnoringPlayer(user.getName()))
		{
			player.requestTeleport(user, false);
			player.sendMessage(_("teleportRequest", user.getDisplayName()));
			player.sendMessage(_("typeTpaccept"));
			player.sendMessage(_("typeTpdeny"));
			if (ess.getSettings().getTpaAcceptCancellation() != 0)
			{
				player.sendMessage(_("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation()));
			}
		}
		user.sendMessage(_("requestSent", player.getDisplayName()));
	}
}
