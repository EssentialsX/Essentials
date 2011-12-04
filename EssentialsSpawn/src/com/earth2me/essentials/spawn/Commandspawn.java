package com.earth2me.essentials.spawn;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import static com.earth2me.essentials.I18n._;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandspawn extends EssentialsCommand
{
	public Commandspawn()
	{
		super("spawn");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		if (args.length > 0 && user.isAuthorized("essentials.spawn.others"))
		{
			final User otherUser = getPlayer(server, args, 0);
			otherUser.getTeleport().respawn(ess.getSpawn(), charge);
			if (!otherUser.equals(user))
			{
				otherUser.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
				user.sendMessage(_("teleporting"));
			}
		}
		else
		{
			user.getTeleport().respawn(ess.getSpawn(), charge);
		}
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User user = getPlayer(server, args, 0);
		user.getTeleport().respawn(ess.getSpawn(), null);
		user.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
		sender.sendMessage(_("teleporting"));
	}
}
