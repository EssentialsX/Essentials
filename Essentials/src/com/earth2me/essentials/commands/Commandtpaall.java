package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import lombok.Cleanup;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandtpaall extends EssentialsCommand
{
	public Commandtpaall()
	{
		super("tpaall");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			if (sender instanceof Player)
			{
				teleportAAllPlayers(server, sender, ess.getUser(sender));
				return;
			}
			throw new NotEnoughArgumentsException();
		}

		final IUser player = getPlayer(server, args, 0);
		teleportAAllPlayers(server, sender, player);
	}

	private void teleportAAllPlayers(final Server server, final CommandSender sender, final IUser user)
	{
		sender.sendMessage(_("teleportAAll"));
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			@Cleanup
			final IUser player = ess.getUser(onlinePlayer);
			player.acquireReadLock();
			if (user == player)
			{
				continue;
			}
			if (!player.getData().isTeleportEnabled())
			{
				continue;
			}
			try
			{
				player.requestTeleport(user, true);
				player.sendMessage(_("teleportHereRequest", user.getDisplayName()));
				player.sendMessage(_("typeTpaccept"));
				if (ess.getSettings().getTpaAcceptCancellation() != 0)
				{
					player.sendMessage(_("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation()));
				}
			}
			catch (Exception ex)
			{
				ess.showCommandError(sender, getName(), ex);
			}
		}
	}
}
