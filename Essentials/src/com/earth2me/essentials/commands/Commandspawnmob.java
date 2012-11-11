package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.SpawnMob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandspawnmob extends EssentialsCommand
{
	public Commandspawnmob()
	{
		super("spawnmob");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			final String mobList = SpawnMob.mobList(user);
			throw new NotEnoughArgumentsException(_("mobsAvailable", mobList));
		}

		String[] mobData = SpawnMob.mobData(args[0]);

		int mobCount = 1;
		if (args.length >= 2)
		{
			mobCount = Integer.parseInt(args[1]);
		}

		if (args.length >= 3)
		{
			final User target = getPlayer(ess.getServer(), args, 2);
			SpawnMob.spawnmob(ess, server, user, target, mobData, mobCount);
			return;
		}

		SpawnMob.spawnmob(ess, server, user, mobData, mobCount);
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 3)
		{
			final String mobList = Util.joinList(Mob.getMobList());
			throw new NotEnoughArgumentsException(_("mobsAvailable", mobList));
		}

		String[] mobData = SpawnMob.mobData(args[0]);
		int mobCount = Integer.parseInt(args[1]);

		final User target = getPlayer(ess.getServer(), args, 2);
		SpawnMob.spawnmob(ess, server, sender, target, mobData, mobCount);
	}
}
