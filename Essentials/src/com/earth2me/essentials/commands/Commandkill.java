package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;


public class Commandkill extends EssentialsCommand
{
	public Commandkill()
	{
		super("kill");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			final EntityDamageEvent ede = new EntityDamageEvent(p, sender instanceof Player && ((Player)sender).getName().equals(p.getName()) ? EntityDamageEvent.DamageCause.SUICIDE : EntityDamageEvent.DamageCause.CUSTOM, 1000);
			server.getPluginManager().callEvent(ede);
			if (ede.isCancelled() && !sender.hasPermission("essentials.kill.force"))
			{
				continue;
			}

			p.setHealth(0);
			sender.sendMessage(Util.format("kill", p.getDisplayName()));
		}
	}
}
