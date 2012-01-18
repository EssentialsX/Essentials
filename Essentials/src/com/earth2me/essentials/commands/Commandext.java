package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandext extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		extinguishPlayers(sender, args[0]);
	}

	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.setFireTicks(0);
			user.sendMessage(_("extinguish"));
			return;
		}

		extinguishPlayers(user, args[0]);
	}

	private void extinguishPlayers(final CommandSender sender, final String name) throws Exception
	{
		for (Player matchPlayer : server.matchPlayer(name))
		{
			matchPlayer.setFireTicks(0);
			sender.sendMessage(_("extinguishOthers", matchPlayer.getDisplayName()));
		}
	}
}
