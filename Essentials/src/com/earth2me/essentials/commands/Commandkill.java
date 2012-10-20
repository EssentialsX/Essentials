package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		//TODO: TL this
		if (args[0].trim().length() < 2)
		{
			throw new NotEnoughArgumentsException("You need to specify a player to kill.");
		}

		for (Player matchPlayer : server.matchPlayer(args[0]))
		{
			final EntityDamageEvent ede = new EntityDamageEvent(matchPlayer, sender instanceof Player && ((Player)sender).getName().equals(matchPlayer.getName()) ? EntityDamageEvent.DamageCause.SUICIDE : EntityDamageEvent.DamageCause.CUSTOM, Short.MAX_VALUE);
			server.getPluginManager().callEvent(ede);
			if (ede.isCancelled() && sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.kill.force"))
			{
				continue;
			}

			matchPlayer.damage(Short.MAX_VALUE);
					
			if (matchPlayer.getHealth() > 0)
			{
				matchPlayer.setHealth(0);
			}

			sender.sendMessage(_("kill", matchPlayer.getDisplayName()));
		}
	}
}
