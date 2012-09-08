package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtpall extends EssentialsCommand
{
	public Commandtpall()
	{
		super("tpall");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			if (sender instanceof Player)
			{
				teleportAllPlayers(server, sender, ess.getUser(sender));
				return;
			}
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0);
		teleportAllPlayers(server, sender, player);
	}

	private void teleportAllPlayers(Server server, CommandSender sender, User user)
	{
		sender.sendMessage(_("teleportAll"));
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (user == player)
			{
				continue;
			}
			if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.worlds." + user.getWorld().getName()))
			{
				continue;
			}
			try
			{
				player.getTeleport().now(user, false, TeleportCause.COMMAND);
			}
			catch (Exception ex)
			{
				ess.showError(sender, ex, getName());
			}
		}
	}
}
